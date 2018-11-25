package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import ahodanenok.gwt.stub.gui.dialog.ProfileDialog;
import javafx.event.ActionEvent;

public final class UpdateProfileAction extends Action<Profile> {

    private Stubs stubs;
    private Profile profile;

    public UpdateProfileAction(Stubs stubs, Profile profile, StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
        this.profile = profile;
    }

    @Override
    protected Profile doHandle(ActionEvent event) throws Exception {
        Profile result = new ProfileDialog(stubs, profile).getProfile();
        if (result != null) {
            stubs.saveProfile(result);
            return result;
        } else {
            return null;
        }
    }
}
