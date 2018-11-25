package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.core.Config;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import ahodanenok.gwt.stub.gui.dialog.EditConfigDialog;
import javafx.event.ActionEvent;

public class EditConfigAction extends Action<Config> {

    private Stubs stubs;
    private Config config;

    public EditConfigAction(Stubs stubs, StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
    }

    @Override
    protected Config doHandle(ActionEvent event) throws Exception {
        Config updatedConfig = new EditConfigDialog(stubs, getExceptionHandler()).getConfig();
        if (updatedConfig != null) {
            stubs.saveConfig(updatedConfig);
            return updatedConfig;
        } else {
            return null;
        }
    }
}
