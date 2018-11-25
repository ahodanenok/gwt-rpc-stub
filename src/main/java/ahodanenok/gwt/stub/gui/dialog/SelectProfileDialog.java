package ahodanenok.gwt.stub.gui.dialog;

import ahodanenok.gwt.stub.gui.component.ComponentUtils;
import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.core.StubsException;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.Optional;

public class SelectProfileDialog extends Dialog<Profile> {

    private ComboBox<Profile> profilesComboBox;

    public SelectProfileDialog(Stubs stubs, StubsExceptionHandler exceptionHandler) {
        setTitle("Select active profile");
        setResultConverter(new Callback<ButtonType, Profile>() {
            @Override
            public Profile call(ButtonType param) {
                if (param == ButtonType.OK) {
                    return profilesComboBox.getValue();
                } else {
                    return null;
                }
            }
        });

        ObservableList<Profile> profiles = null;
        try {
            profiles = FXCollections.observableArrayList(stubs.listProfiles());
        } catch (StubsException e) {
            exceptionHandler.handleNotifyUser(e);
        }

        Label profileTitleLabel = ComponentUtils.createItemTitleLabel("Profile:");

        profilesComboBox = new ComboBox<>();
        profilesComboBox.setConverter(new StringConverter<Profile>() {
            @Override
            public String toString(Profile object) {
                return object.getDisplayName();
            }

            @Override
            public Profile fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
        profilesComboBox.setMaxWidth(Double.MAX_VALUE);
        if (profiles != null) {
            profilesComboBox.setItems(profiles);
            profilesComboBox.setValue(stubs.getActiveProfile());
        }

        GridPane formPane = new GridPane();
        formPane.setHgap(5);
        formPane.setVgap(5);
        formPane.add(profileTitleLabel, 0, 0);
        formPane.add(profilesComboBox, 1, 0);
        GridPane.setHgrow(profilesComboBox, Priority.ALWAYS);

        getDialogPane().setContent(formPane);

        getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(profilesComboBox.valueProperty().isNull());

        setWidth(600);
        getDialogPane().setPrefWidth(600);
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
