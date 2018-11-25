package ahodanenok.gwt.stub.gui.dialog;

import ahodanenok.gwt.stub.gui.component.ComponentUtils;
import ahodanenok.gwt.stub.core.ReturnValueType;
import ahodanenok.gwt.stub.core.ServiceStub;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.core.StubsException;
import ahodanenok.gwt.stub.core.AllowAllSerializationPolicyProvider;
import ahodanenok.gwt.stub.core.util.ReflectionUtils;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.server.rpc.RPCRequest;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public final class ImportFromRequestDialog extends Dialog<ImportFromRequestDialog.ImportData> {

    public static class ImportData {

        private String request;
        private String response;
        private ReturnValueType responseType;
        private String serviceUrl;

        private ImportData() { }

        public String getRequest() {
            return request;
        }

        public String getResponse() {
            return response;
        }

        public ReturnValueType getResponseType() {
            return responseType;
        }

        public String getServiceUrl() {
            return serviceUrl;
        }
    }

    private TextField serviceClassTextField;
    private TextField serviceUrlTextField;
    private TextField methodTextField;
    private TextArea requestTextArea;
    private TextArea responseTextArea;
    private ToggleGroup returnTypeGroup;
    private RadioButton rpcRadio;
    private RadioButton jsonRadio;

    public ImportFromRequestDialog(Stubs stubs) {
        setTitle("Import method stub");

        setResultConverter(new Callback<ButtonType, ImportData>() {
            @Override
            public ImportData call(ButtonType param) {
                if (param == ButtonType.OK) {
                    ImportData data = new ImportData();
                    data.request = requestTextArea.getText();
                    data.response = responseTextArea.getText();
                    data.serviceUrl = serviceUrlTextField.getText();

                    if (returnTypeGroup.getSelectedToggle() == rpcRadio) {
                        data.responseType = ReturnValueType.GWT_RPC;
                    } else if (returnTypeGroup.getSelectedToggle() == jsonRadio) {
                        data.responseType = ReturnValueType.JSON;
                    } else {
                        throw new IllegalStateException("Unknown response type");
                    }

                    return data;
                } else {
                    return null;
                }
            }
        });

        Label serviceClassLabel = ComponentUtils.createMandatoryItemTitleLabel("Service class", ":");
        Label serviceUrlLabel = ComponentUtils.createMandatoryItemTitleLabel("Service url", ":");
        Label methodLabel = ComponentUtils.createMandatoryItemTitleLabel("Method", ":");
        Label requestTitleLabel = ComponentUtils.createMandatoryItemTitleLabel("Request", ":");
        Label responseTitleLabel = ComponentUtils.createMandatoryItemTitleLabel("Response", ":");
        Label responseTypeTitleLabel = ComponentUtils.createItemTitleLabel("Response type:");

        serviceClassTextField = new TextField();
        serviceClassTextField.setDisable(true);

        serviceUrlTextField = new TextField();
        serviceUrlTextField.setDisable(true);

        methodTextField = new TextField();
        methodTextField.setDisable(true);

        requestTextArea = new TextArea();
        requestTextArea.setWrapText(true);
        requestTextArea.setPrefHeight(100);
        requestTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (StringUtils.isBlank(newValue)) {
                    serviceClassTextField.setText(null);
                    serviceUrlTextField.setText(null);
                    serviceUrlTextField.setDisable(true);
                    return;
                }

                try {
                    RPCRequest request = stubs.decodeRequest(newValue, new AllowAllSerializationPolicyProvider());
                    Class<?> serviceClass = request.getMethod().getDeclaringClass();
                    ServiceStub stub = stubs.getServiceStub(serviceClass);
                    if (stub != null) {
                        serviceClassTextField.setText(stub.getDisplayString());
                        serviceUrlTextField.setText(stub.getUrl());
                        serviceUrlTextField.setDisable(true);
                    } else {
                        serviceClassTextField.setText(serviceClass.getName());
                        serviceUrlTextField.setText(null);
                        serviceUrlTextField.setDisable(false);
                    }

                    methodTextField.setText(ReflectionUtils.toString(request.getMethod()));
                    ComponentUtils.clearInvalid(requestTextArea);
                } catch (IncompatibleRemoteServiceException | StubsException e) {
                    ComponentUtils.markInvalid(requestTextArea, e.getMessage());

                    serviceClassTextField.setText(null);
                    serviceUrlTextField.setText(null);
                    serviceUrlTextField.setDisable(true);
                    methodTextField.setText(null);
                }
            }
        });

        responseTextArea = new TextArea();
        responseTextArea.setWrapText(true);
        responseTextArea.setPrefHeight(400);

        returnTypeGroup = new ToggleGroup();

        rpcRadio = new RadioButton();
        rpcRadio.setText("GWT-RPC");
        rpcRadio.setToggleGroup(returnTypeGroup);
        rpcRadio.setSelected(true);

        jsonRadio = new RadioButton();
        jsonRadio.setText("JSON");
        jsonRadio.setToggleGroup(returnTypeGroup);

        GridPane formPane = new GridPane();
        formPane.setHgap(5);
        formPane.setVgap(5);
        formPane.add(serviceClassLabel, 0, 0);
        formPane.add(serviceClassTextField, 1, 0);
        GridPane.setHgrow(serviceClassTextField, Priority.ALWAYS);
        formPane.add(serviceUrlLabel, 0, 1);
        formPane.add(serviceUrlTextField, 1, 1);
        GridPane.setHgrow(serviceUrlTextField, Priority.ALWAYS);
        formPane.add(methodLabel, 0, 2);
        formPane.add(methodTextField, 1, 2);
        GridPane.setHgrow(methodTextField, Priority.ALWAYS);
        formPane.add(requestTitleLabel, 0, 3);
        GridPane.setValignment(requestTitleLabel, VPos.TOP);
        formPane.add(requestTextArea, 1, 3);
        GridPane.setHgrow(requestTextArea, Priority.ALWAYS);
        formPane.add(responseTypeTitleLabel, 0, 4);
        formPane.add(new HBox(5, rpcRadio, jsonRadio), 1, 4);
        formPane.add(responseTitleLabel, 0, 5);
        GridPane.setValignment(responseTitleLabel, VPos.TOP);
        formPane.add(responseTextArea, 1, 5);
        GridPane.setHgrow(responseTextArea, Priority.ALWAYS);

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

        setWidth(800);
        getDialogPane().setPrefWidth(800);
    }

    private boolean validate() {

        boolean valid = true;

        ComponentUtils.clearInvalid(requestTextArea);
        ComponentUtils.clearInvalid(responseTextArea);
        ComponentUtils.clearInvalid(serviceUrlTextField);

        if (StringUtils.isBlank(requestTextArea.getText())) {
            ComponentUtils.markInvalid(requestTextArea, "Please enter request");
            valid = false;
        }

        if (StringUtils.isBlank(responseTextArea.getText())) {
            ComponentUtils.markInvalid(responseTextArea, "Please enter response");
            valid = false;
        }

        if (!serviceUrlTextField.isDisable() && StringUtils.isBlank(serviceUrlTextField.getText())) {
            ComponentUtils.markInvalid(serviceUrlTextField, "Please enter service URL");
            valid = false;
        }

        if (StringUtils.isNotBlank(serviceUrlTextField.getText())) {
            try {
                new URI(serviceUrlTextField.getText());
            } catch (URISyntaxException e) {
                ComponentUtils.markInvalid(serviceUrlTextField, "Service URL is not valid");
                valid = false;
            }
        }

        // TODO: json validation
//        if (StringUtils.isNotBlank(responseTextArea.getText())
//                && StringUtils.isNotBlank(methodTextField.getText())
//                && returnTypeGroup.getSelectedToggle() == jsonRadio) {
//            try {
//                JsonUtils.fromJson(responseTextArea.getText(), );
//            } catch (Exception e) {
//                appearanceConfig.markInvalid(responseTextArea, "Not a valid JSON");
//                responseValid = false;
//            }
//        }

        return valid;
    }

    public ImportData getImportData() {
        Optional<ImportData> data = showAndWait();
        if (data.isPresent()) {
            return data.get();
        } else {
            return null;
        }
    }
}
