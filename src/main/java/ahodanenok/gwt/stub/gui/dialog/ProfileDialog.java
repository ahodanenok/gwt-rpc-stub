package ahodanenok.gwt.stub.gui.dialog;

import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.component.ComponentUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class ProfileDialog extends Dialog<Profile> {

    private Stubs stubs;

    private TextField profileIdTextField;
    private TextField profileDisplayNameTextField;

    public ProfileDialog(Stubs stubs) {
        this(stubs, null);
    }

    public ProfileDialog(Stubs stubs, Profile profile) {
        this.stubs = stubs;

        if (profile != null) {
            setTitle("Edit profile");
        } else {
            setTitle("Create profile");
        }

        setResultConverter(new Callback<ButtonType, Profile>() {
            @Override
            public Profile call(ButtonType param) {
                if (param == ButtonType.OK) {
                    return new Profile(
                        profile != null ? profile.getId() : null,
                        profileDisplayNameTextField.getText().trim());
                } else {
                    return null;
                }
            }
        });

        Label profileIdTitleLabel = ComponentUtils.createItemTitleLabel("ID:");

        profileIdTextField = new TextField();
        profileIdTextField.setDisable(true);
        profileIdTextField.setEditable(false);
        if (profile != null) {
            profileIdTextField.setText(profile.getId());
        } else {
            profileIdTextField.setText("< generated automatically >");
        }

        Label profileTitleLabel = ComponentUtils.createMandatoryItemTitleLabel("Name", ":");

        profileDisplayNameTextField = new TextField();
        if (profile != null) {
            profileDisplayNameTextField.setText(profile.getDisplayName());
        }

        GridPane formPane = new GridPane();
        formPane.setHgap(5);
        formPane.setVgap(5);
        formPane.add(profileIdTitleLabel, 0, 0);
        formPane.add(profileIdTextField, 1, 0);
        GridPane.setHgrow(profileIdTextField, Priority.ALWAYS);
        formPane.add(profileTitleLabel, 0, 1);
        formPane.add(profileDisplayNameTextField, 1, 1);
        GridPane.setHgrow(profileDisplayNameTextField, Priority.ALWAYS);

        getDialogPane().setContent(formPane);

        getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        getDialogPane()
                .lookupButton(ButtonType.OK)
                .addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (!validate()) {
                            event.consume();
                        }
                    }
                });

        setWidth(600);
        getDialogPane().setPrefWidth(600);
    }

    private boolean validate() {
        if (StringUtils.isBlank(profileDisplayNameTextField.getText())) {
            ComponentUtils.markInvalid(profileDisplayNameTextField, "Profile name can't be empty");
            return false;
        }

        if (stubs.existsProfile(profileDisplayNameTextField.getText().trim())) {
            ComponentUtils.markInvalid(profileDisplayNameTextField, "Profile '" + profileDisplayNameTextField.getText().trim() + "' already exists");
            return false;
        }

        ComponentUtils.clearInvalid(profileDisplayNameTextField);
        return true;
    }

    public Profile getProfile() {
        Optional<Profile> result = showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }
}
