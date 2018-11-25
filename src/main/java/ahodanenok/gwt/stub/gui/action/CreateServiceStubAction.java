package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.core.ServiceStub;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.dialog.ServiceStubDialog;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;

import javafx.event.ActionEvent;

public final class CreateServiceStubAction extends Action<ServiceStub> {

    private Stubs stubs;

    public CreateServiceStubAction(Stubs stubs, StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
    }

    @Override
    protected ServiceStub doHandle(ActionEvent event) throws Exception {
        ServiceStubDialog dialog = new ServiceStubDialog(stubs, getExceptionHandler());
        ServiceStub stub = dialog.getServiceStub();
        if (stub != null) {
            stubs.saveServiceStub(stub);
            return stub;
        } else {
            return null;
        }
    }
}
