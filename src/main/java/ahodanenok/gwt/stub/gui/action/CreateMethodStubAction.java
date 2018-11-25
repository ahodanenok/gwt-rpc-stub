package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.gui.dialog.MethodStubDialog;
import ahodanenok.gwt.stub.core.MethodStub;
import ahodanenok.gwt.stub.core.ServiceStub;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;

import javafx.event.ActionEvent;

public final class CreateMethodStubAction extends Action<MethodStub> {

    private Stubs stubs;
    private ServiceStub serviceStub;

    public CreateMethodStubAction(Stubs stubs,
                                  StubsExceptionHandler exceptionHandler) {
        this(stubs, null, exceptionHandler);
    }

    public CreateMethodStubAction(Stubs stubs,
                                  ServiceStub serviceStub,
                                  StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
        this.serviceStub = serviceStub;
    }


    @Override
    protected MethodStub doHandle(ActionEvent event) throws Exception {
        MethodStubDialog dialog = new MethodStubDialog(stubs, serviceStub, getExceptionHandler());
        MethodStub stub = dialog.getMethodStub();
        if (stub != null) {
            stubs.saveMethodStub(stub);
            return stub;
        } else {
            return null;
        }
    }
}
