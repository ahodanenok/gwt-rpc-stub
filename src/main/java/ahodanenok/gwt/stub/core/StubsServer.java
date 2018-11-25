package ahodanenok.gwt.stub.core;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.component.LifeCycle;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class StubsServer {

    private static Logger LOGGER = Logger.getLogger(StubsServer.class.getName());

    public enum Status {
        STARTING, RUNNING, STOPPING, STOPPED, ERROR
    }

    public interface StatusListener {
        void onStatusChange(Status newStatus);
        void onError(Throwable e);
    }

    public interface ExceptionHandler {
        void handle(Throwable t);
    }

    private Stubs stubs;
    private Server server;
    private ServerConnector connector;
    private Status status;
    private StatusListener statusListener;
    private ExceptionHandler exceptionHandler;
    private ResourceHandler resourceHandler;
    private JettyRPCStubHandler stubHandler;

    public StubsServer(Stubs stubs) throws StubsException {
        this.stubs = stubs;
        this.status = Status.STOPPED;

        stubs.addProfileChangeListener(new Stubs.ProfileChangeListener() {
            @Override
            public void onChange(Profile newProfile, Profile oldProfile) {
                // hot-swap
                if (stubs.getConfig() != null) {
                    reconfigure();
                }
            }
        });
    }

    public boolean isConfigured() {
        return stubs.getConfig() != null;
    }

    public int getPort() {
        if (stubs.getConfig() == null) {
            throw new IllegalStateException("Server is not configured");
        }

        return stubs.getConfig().getServerPort();
    }

    public String getStaticResourcesPath() {
        if (stubs.getConfig() == null) {
            throw new IllegalStateException("Server is not configured");
        }

        return stubs.getConfig().getStaticResourcesPath();
    }

    public Stubs getStubs() {
        return stubs;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        if (stubHandler != null) {
            stubHandler.setExceptionHandler(exceptionHandler);
        }
    }

    public void start() {
        try {
            doStart();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Can't start server", e);
        }
    }

    private void doStart() throws Exception {
        initServer();
        if (server.isStopped()) {
            server.start();
        }
    }

    public void stop() {
        try {
            doStop();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Can't stop server", e);
        }
    }

    private void doStop() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    private void reconfigure() {
        Config config = stubs.getConfig();
        if (config == null) {
            throw new IllegalStateException("Server is not configured");
        }

        if (connector != null) {
            connector.setPort(config.getServerPort());
        }

        if (resourceHandler != null) {
            resourceHandler.setResourceBase(config.getStaticResourcesPath());
        }
    }

    private void initServer() {
        if (server != null) {
            return;
        }

        if (stubs.getConfig() == null) {
            throw new IllegalStateException("Server is not configured");
        }

        LOGGER.config("Initializing server instance...");

        server = new Server();
        server.setStopAtShutdown(true);

        Config config = stubs.getConfig();

        connector = new ServerConnector(server);
        connector.setPort(config.getServerPort());
        server.addConnector(connector);

        resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase(config.getStaticResourcesPath());

        stubHandler = new JettyRPCStubHandler(new RPCStubRequestHandler(stubs));
        if (exceptionHandler != null) {
            stubHandler.setExceptionHandler(exceptionHandler);
        }

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, stubHandler });
        server.setHandler(handlers);

        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        errorHandler.setShowMessageInTitle(true);
        server.setErrorHandler(errorHandler);

        server.addLifeCycleListener(new LifeCycle.Listener() {
            public void lifeCycleStarting(LifeCycle lifeCycle) {
                LOGGER.fine("Server life cycle: " + Status.STARTING);
                status = Status.STARTING;
                fireStatusChangeEvent(Status.STARTING);
            }

            public void lifeCycleStarted(LifeCycle lifeCycle) {
                LOGGER.fine("Server life cycle: " + Status.RUNNING);
                status = Status.RUNNING;
                fireStatusChangeEvent(Status.RUNNING);
            }

            public void lifeCycleFailure(LifeCycle lifeCycle, Throwable throwable) {
                LOGGER.fine("Server life cycle: " + Status.ERROR);
                status = Status.ERROR;
                fireStatusChangeEvent(Status.ERROR);
                fireOnErrorEvent(throwable);
            }

            public void lifeCycleStopping(LifeCycle lifeCycle) {
                LOGGER.fine("Server life cycle: " + Status.STOPPING);
                status = Status.STOPPING;
                fireStatusChangeEvent(Status.STOPPING);
            }

            public void lifeCycleStopped(LifeCycle lifeCycle) {
                LOGGER.fine("Server life cycle: " + Status.STOPPED);
                status = Status.STOPPED;
                fireStatusChangeEvent(Status.STOPPED);
            }
        });
    }

    private void fireStatusChangeEvent(Status status) {
        if (statusListener != null) {
            statusListener.onStatusChange(status);
        }
    }

    private void fireOnErrorEvent(Throwable t) {
        if (statusListener != null) {
            statusListener.onError(t);
        }
    }
}
