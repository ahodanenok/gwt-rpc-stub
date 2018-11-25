package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.core.ServiceStub;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import ahodanenok.gwt.stub.gui.dialog.ServiceStubDialog;

import javafx.event.ActionEvent;

public final class UpdateServiceStubAction extends Action<ServiceStub> {

    private Stubs stubs;
    private ServiceStub serviceStub;

    public UpdateServiceStubAction(Stubs stubs,
                                   ServiceStub serviceStub,
                                   StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
        this.serviceStub = serviceStub;
    }

    @Override
    protected ServiceStub doHandle(ActionEvent event) throws Exception {
        ServiceStubDialog dialog = new ServiceStubDialog(stubs, serviceStub, getExceptionHandler());
        ServiceStub updatedStub = dialog.getServiceStub();
        if (updatedStub != null) {
            stubs.saveServiceStub(updatedStub);
            return updatedStub;
        } else {
            return null;
        }
    }
}
