package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.dialog.SelectProfileDialog;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;

import javafx.event.ActionEvent;

public final class SelectProfileAction extends Action<Profile> {

    private Stubs stubs;

    public SelectProfileAction(Stubs stubs, StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
    }

    @Override
    protected Profile doHandle(ActionEvent event) throws Exception {
        Profile profile = new SelectProfileDialog(stubs, getExceptionHandler()).getProfile();
        if (profile != null) {
            stubs.setActiveProfile(profile);
            return profile;
        } else {
            return null;
        }
    }
}
