package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.core.MethodStub;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.dialog.MethodStubDialog;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;

import javafx.event.ActionEvent;

public final class UpdateMethodStubAction extends Action<MethodStub> {

    private Stubs stubs;
    private MethodStub methodStub;

    public UpdateMethodStubAction(Stubs stubs,
                                  MethodStub methodStub,
                                  StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
        this.methodStub = methodStub;
    }

    @Override
    protected MethodStub doHandle(ActionEvent event) throws Exception {
        MethodStubDialog dialog = new MethodStubDialog(stubs, methodStub, getExceptionHandler());
        MethodStub stub = dialog.getMethodStub();
        if (stub != null) {
            stubs.saveMethodStub(stub);
            return stub;
        } else {
            return null;
        }
    }
}
