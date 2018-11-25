package ahodanenok.gwt.stub.core;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JettyRPCStubHandler extends AbstractHandler {

    private static Logger LOGGER = Logger.getLogger(JettyRPCStubHandler.class.getName());

    private RPCStubRequestHandler requestHandler;
    private StubsServer.ExceptionHandler exceptionHandler;

    public JettyRPCStubHandler(RPCStubRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse)
            throws IOException, ServletException {

        if (baseRequest.getContentType() == null
                || !baseRequest.getContentType().contains("text/x-gwt-rpc")) {
            LOGGER.fine("Not a GWT-RPC request: contentType=" + baseRequest.getContentType());
            return;
        }

        try {
            if (!requestHandler.canHandle(target)) {
                LOGGER.fine("Can't handle GWT-PRC request: target=" + target);
                return;
            }

            String request = IOUtils.toString(httpRequest.getReader());
            String response = requestHandler.handle(request);
            if (StringUtils.isNotBlank(response)) {
                httpResponse.setContentType("application/json; charset=utf-8");
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.getWriter().write(response);
                baseRequest.setHandled(true);
            }
        } catch (IOException e) {
            handleError(e);
            throw e;
        } catch (StubsException e) {
            handleError(e);
            throw new ServletException(e);
        }
    }

    public void setExceptionHandler(StubsServer.ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    private void handleError(Throwable e) {
        LOGGER.log(Level.SEVERE, "Can't handle GWT-RPC request", e);
        if (exceptionHandler != null) {
            exceptionHandler.handle(e);
        }
    }
}
