package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import ahodanenok.gwt.stub.gui.dialog.ProfileDialog;

import javafx.event.ActionEvent;

public final class CreateProfileAction extends Action<Profile> {

    private Stubs stubs;

    public CreateProfileAction(Stubs stubs, StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
    }

    @Override
    protected Profile doHandle(ActionEvent event) throws Exception {
        Profile profile = new ProfileDialog(stubs).getProfile();
        if (profile != null) {
            stubs.saveProfile(profile);
            return profile;
        } else {
            return null;
        }
    }
}
