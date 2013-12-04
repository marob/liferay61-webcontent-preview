package fr.smile.dirtech.liferay.preview.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EsigateDecoratorAspect {

    private static Log _log = LogFactory.getLog(EsigateDecoratorAspect.class);

    public void openEntry(ProceedingJoinPoint call, ServletContext servletContext, HttpServletRequest request,
                          HttpServletResponse response, RenderRequest renderRequest,
                          RenderResponse renderResponse, String content) {
        _log.debug("openEntry");

        /*Object[] args = joinPoint.getArgs();
        // Nom de la méthode interceptée
        String name = joinPoint.getSignature().toLongString();
        StringBuffer sb = new StringBuffer(name + " called with: [");

        // Liste des valeurs des arguments reçus par la méthode
        for (int i = 0; i < args.length; i++) {
            Object o = args[i];
            sb.append("'" + o + "'");
            sb.append(i == args.length - 1 ? "" : ", ");
        }
        sb.append("]");
        _log.debug(sb);*/
    }

    /*public void closeExit(StaticPart staticPart) {
        _log.debug("closeExit");

        // Nom de la méthode interceptée
        String name = staticPart.getSignature().toLongString();
        _log.debug(name);
    } */
}
