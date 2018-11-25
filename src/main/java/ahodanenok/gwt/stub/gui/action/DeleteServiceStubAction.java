package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.core.ServiceStub;
import ahodanenok.gwt.stub.core.Stubs;

import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public final class DeleteServiceStubAction extends Action<Boolean> {

    private Stubs stubs;
    private ServiceStub serviceStub;

    public DeleteServiceStubAction(Stubs stubs, ServiceStub serviceStub, StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
        this.serviceStub = serviceStub;
    }

    @Override
    protected Boolean doHandle(ActionEvent event) throws Exception {
        String msg = "Delete service stub:\n" + serviceStub.getDisplayString();
        msg += "\nWARNING: All method stubs for this service will be also deleted";

        Alert askDialog = new Alert(Alert.AlertType.CONFIRMATION, msg);
        askDialog.setHeaderText(null);
        askDialog.setWidth(500);
        askDialog.getDialogPane().setPrefWidth(500);

        boolean canDelete = false;

        Optional<ButtonType> result = askDialog.showAndWait();
        if (result.isPresent()) {
            canDelete = (result.get() == ButtonType.OK);
        }

        if (canDelete) {
            stubs.deleteServiceStub(serviceStub.getId());
            return true;
        } else {
            return false;
        }
    }
}
