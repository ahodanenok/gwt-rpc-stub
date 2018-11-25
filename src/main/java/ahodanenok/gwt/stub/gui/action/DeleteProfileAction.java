package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public final class DeleteProfileAction extends Action<Boolean> {

    private Stubs stubs;
    private Profile profile;

    public DeleteProfileAction(Stubs stubs, Profile profile, StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
        this.profile = profile;
    }

    @Override
    protected Boolean doHandle(ActionEvent event) throws Exception {

        String msg = "Delete profile: " + profile.getDisplayName();
        msg += "\nWARNING: All service and method stubs for this profile will be also deleted";

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
            stubs.deleteProfile(profile);
            return true;
        } else {
            return false;
        }
    }
}
