package fr.smile.dirtech.liferay.preview;

import com.liferay.portal.kernel.struts.BaseStrutsAction;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.Parameters;
import org.esigate.aggregator.AggregateRenderer;
import org.esigate.esi.EsiRenderer;
import org.esigate.servlet.HttpServletMediator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Overrides the default ViewArticleContentAction
 * Calls the original action, and in case of article preview, gets the original layout and inject the article new
 * content via Esigate
 */
public class ViewArticleContentAction extends BaseStrutsAction {

    private static final String JOURNAL_PREVIEW_CMD = "journal.preview.cmd";
    private static final List<String> JOURNAL_PREVIEW_CMD_DEFAULT = new ArrayList<String>(Arrays.asList("preview",
            "view"));
    private static final String JOURNAL_PREVIEW_URL_BASE = "journal.preview.url.base";
    private static final String JOURNAL_PREVIEW_URL_BASE_DEFAULT = "http://localhost:8080";

    private static Log _log = LogFactory.getLog(ViewArticleContentAction.class);

    /* (non-Javadoc)
     * @see com.liferay.portal.kernel.struts.BaseStrutsAction#execute(com.liferay.portal.kernel.struts.StrutsAction, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String execute(StrutsAction originalStrutsAction, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        _log.debug("Entering in webcontent-preview-hook");

        String originalForward = originalStrutsAction.execute(request, response);
        String cmd = ParamUtil.getString(request, Constants.CMD);


        List<String> journalPreviewCmd = Arrays.asList(PropsUtil.getArray(JOURNAL_PREVIEW_CMD));
        if (CollectionUtils.isEmpty(journalPreviewCmd)) {
            journalPreviewCmd = JOURNAL_PREVIEW_CMD_DEFAULT;
        }
        _log.debug(JOURNAL_PREVIEW_CMD + "=" + StringUtils.join(journalPreviewCmd, ", "));

        _log.debug("cmd=" + cmd);
        if (journalPreviewCmd.contains(cmd)) {
            _log.debug("Applying webcontent-preview-hook");

            ThemeDisplay td = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            long originalPlid = td.getPlid();
            String page = PortalUtil.getLayoutActualURL(LayoutLocalServiceUtil.getLayout(originalPlid));

            Properties props = getEsigateProperties();
            DriverFactory.configure(props);
            Driver driver = DriverFactory.getInstance();

            HttpEntityEnclosingRequest httpRequest = new HttpServletMediator(request, response,
                    request.getSession().getServletContext()).getHttpRequest();
            Map<String, String> parameters = new HashMap<String, String>();

            EsiRenderer esiRenderer = new EsiRenderer();
            Map<String, CharSequence> fragments = new HashMap<String, CharSequence>();
            fragments.put("article_preview", ((String) request.getAttribute("JOURNAL_ARTICLE_CONTENT")));
            esiRenderer.setFragmentsToReplace(fragments);

            driver.render(page, parameters, response.getWriter(), httpRequest,
                    new AggregateRenderer(), esiRenderer);
            return null;
        }

        return originalForward;

    }

    /**
     * Builds Esigate configuration
     *
     * @return
     */
    private Properties getEsigateProperties() {
        Properties props = new Properties();

        String remoteUrlBase = PropsUtil.get(JOURNAL_PREVIEW_URL_BASE);
        if (StringUtils.isEmpty(remoteUrlBase)) {
            remoteUrlBase = JOURNAL_PREVIEW_URL_BASE_DEFAULT;
        }
        _log.debug(JOURNAL_PREVIEW_URL_BASE + "=" + remoteUrlBase);

        props.put(Parameters.REMOTE_URL_BASE.name, remoteUrlBase);
        props.put(Parameters.PRESERVE_HOST.name, "true");
        props.put(Parameters.USE_CACHE.name, "false");
        props.put(Parameters.FILTER.name, "org.esigate.filter.CookieForwardingFilter");
        props.put(Parameters.FORWARD_COOKIES.name, "*");
        return props;
    }

}
