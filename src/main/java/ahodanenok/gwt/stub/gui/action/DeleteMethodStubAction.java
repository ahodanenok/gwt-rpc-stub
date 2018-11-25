package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.core.MethodStub;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public final class DeleteMethodStubAction extends Action<Boolean> {

    private Stubs stubs;
    private MethodStub methodStub;

    public DeleteMethodStubAction(Stubs stubs, MethodStub methodStub, StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
        this.methodStub = methodStub;
    }

    @Override
    protected Boolean doHandle(ActionEvent event) throws Exception {
        String msg = "Delete method stub:\n" + methodStub.getDisplayString();

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
            stubs.removeMethodStub(methodStub.getServiceStubId(), methodStub.getId());
            return true;
        } else {
            return false;
        }
    }
}
