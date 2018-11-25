package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public abstract class Action<T> implements EventHandler<ActionEvent> {

    private ActionResultHandler<T> resultHandler;
    private StubsExceptionHandler exceptionHandler;

    public Action(StubsExceptionHandler exceptionHandler) {
        if (exceptionHandler == null) {
            throw new IllegalArgumentException("exceptionHandler is null");
        }
        this.exceptionHandler = exceptionHandler;
    }

    protected final StubsExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public final void setResultHandler(ActionResultHandler<T> resultHandler) {
        this.resultHandler = resultHandler;
    }

    @Override
    public final void handle(ActionEvent event) {
        try {
            T result = doHandle(event);
            if (resultHandler != null) {
                resultHandler.handleResult(result);
            }
        } catch (Exception e) {
            exceptionHandler.handleNotifyUser(e);
        }
    }

    protected abstract T doHandle(ActionEvent event) throws Exception;
}
