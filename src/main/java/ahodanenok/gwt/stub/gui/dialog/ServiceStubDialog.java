package ahodanenok.gwt.stub.gui.dialog;

import ahodanenok.gwt.stub.gui.component.ComponentUtils;
import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.ServiceStub;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.core.StubsException;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public final class ServiceStubDialog extends Dialog<ServiceStub> {

    private boolean edit;

    private Stubs stubs;
    private StubsExceptionHandler exceptionHandler;

    private TextField idTextField;
    private ComboBox<Profile> profileComboBox;
    private TextField serviceClassTextField;
    private TextField pathTextField;

    public ServiceStubDialog(Stubs stubs, StubsExceptionHandler exceptionHandler) {
        this(stubs, null, exceptionHandler);
    }

    public ServiceStubDialog(Stubs stubs, ServiceStub stub, StubsExceptionHandler exceptionHandler) {
        this.stubs = stubs;
        this.exceptionHandler = exceptionHandler;
        this.edit = stub != null;

        setResultConverter(new Callback<ButtonType, ServiceStub>() {
            @Override
            public ServiceStub call(ButtonType param) {
                if (param == ButtonType.OK) {
                    ServiceStub resultStub = new ServiceStub();
                    resultStub.setId(stub != null ? stub.getId() : null);
                    resultStub.setProfileId(stub != null ? stub.getProfileId() : profileComboBox.getValue().getId());
                    try {
                        resultStub.setServiceClass(stubs.loadServiceClass(serviceClassTextField.getText()));
                    } catch (ClassNotFoundException e) {
                        exceptionHandler.handleNotifyUser(e);
                        return null;
                    }

                    resultStub.setUrl(pathTextField.getText());
                    return resultStub;
                } else {
                    return null;
                }
            }
        });

        createForm();
        if (edit) {
            initForEdit(stub);
        } else {
            initForCreate();
        }
    }

    private void createForm() {
        Label idTitleLabel = ComponentUtils.createItemTitleLabel("ID:");
        idTextField = new TextField();
        idTextField.setEditable(false);
        idTextField.setDisable(true);

        Label profileTitleLabel = ComponentUtils.createItemTitleLabel("Profile:");
        profileComboBox = new ComboBox<>();
        profileComboBox.setMaxWidth(Double.MAX_VALUE);
        profileComboBox.setConverter(new StringConverter<Profile>() {
            @Override
            public String toString(Profile object) {
                return object.getDisplayName();
            }

            @Override
            public Profile fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });

        Label classTitleLabel = ComponentUtils.createMandatoryItemTitleLabel("Class", ":");
        Label urlTitleLabel = ComponentUtils.createMandatoryItemTitleLabel("Path", ":");

        serviceClassTextField = new TextField();
        pathTextField = new TextField();

        GridPane formPane = new GridPane();
        formPane.setHgap(5);
        formPane.setVgap(5);
        formPane.add(idTitleLabel, 0, 0);
        formPane.add(idTextField, 1, 0);
        GridPane.setHgrow(idTextField, Priority.ALWAYS);
        formPane.add(profileTitleLabel, 0, 1);
        formPane.add(profileComboBox, 1, 1);
        GridPane.setHgrow(profileComboBox, Priority.ALWAYS);
        formPane.add(classTitleLabel, 0, 2);
        formPane.add(serviceClassTextField, 1, 2);
        GridPane.setHgrow(serviceClassTextField, Priority.ALWAYS);
        formPane.add(urlTitleLabel, 0, 3);
        formPane.add(pathTextField, 1, 3);
        GridPane.setHgrow(pathTextField, Priority.ALWAYS);

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

        setWidth(700);
        getDialogPane().setPrefWidth(700);
    }

    private void initForCreate() {
        setTitle("Create service stub");
        idTextField.setText("< generated automatically >");

        try {
            List<Profile> profiles = stubs.listProfiles();
            profileComboBox.setItems(FXCollections.observableList(profiles));
            profileComboBox.setValue(stubs.getActiveProfile());
        } catch (StubsException e) {
            exceptionHandler.handleNotifyUser(e);
        }
    }

    private void initForEdit(ServiceStub stub) {
        setTitle("Edit service stub");
        idTextField.setText(stub.getId());

        profileComboBox.setDisable(true);
        profileComboBox.setEditable(false);
        try {
            Profile p = stubs.getProfile(stub.getProfileId());
            profileComboBox.setItems(FXCollections.observableArrayList(p));
            profileComboBox.setValue(p);
        } catch (StubsException e) {
            exceptionHandler.handleNotifyUser(e);
        }

        serviceClassTextField.setText(stub.getDisplayString());
        pathTextField.setText(stub.getUrl());
    }

    private boolean validate() {
        boolean valid = true;

        ComponentUtils.clearInvalid(profileComboBox);
        ComponentUtils.clearInvalid(serviceClassTextField);
        ComponentUtils.clearInvalid(pathTextField);

        if (profileComboBox.getValue() == null) {
            ComponentUtils.markInvalid(profileComboBox, "Profile isn't selected");
            valid = false;
        }

        String className = serviceClassTextField.getText();
        if (StringUtils.isBlank(className)) {
            ComponentUtils.markInvalid(serviceClassTextField, "Service class name is empty");
            valid = false;
        }

        Class<?> serviceClass = null;
        if (StringUtils.isNotBlank(serviceClassTextField.getText())) {
            try {
                serviceClass = stubs.loadServiceClass(className);
            } catch (ClassNotFoundException e) {
                ComponentUtils.markInvalid(serviceClassTextField, "Service class can't be loaded: not present in classpath");
                valid = false;
            }
        }

        try {
            if (!edit && serviceClass != null && stubs.getServiceStub(serviceClass) != null) {
                ComponentUtils.markInvalid(serviceClassTextField, "Stub already exists for this class");
                valid = false;
            }
        } catch (StubsException e) {
            exceptionHandler.handle(e);
        }

        String url = pathTextField.getText();
        if (StringUtils.isBlank(url)) {
            ComponentUtils.markInvalid(pathTextField, "Service path is empty");
            valid = false;
        }

        if (StringUtils.isNotBlank(pathTextField.getText())) {
            try {
                new URI(pathTextField.getText());
            } catch (URISyntaxException e) {
                ComponentUtils.markInvalid(pathTextField, "Invalid path format: " + e.getMessage());
                valid = false;
            }
        }

        return valid;
    }

    public ServiceStub getServiceStub() {
        Optional<ServiceStub> result = showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }
}
