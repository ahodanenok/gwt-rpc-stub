package ahodanenok.gwt.stub.gui.dialog;

import ahodanenok.gwt.stub.core.*;
import ahodanenok.gwt.stub.gui.component.ComponentUtils;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import ahodanenok.gwt.stub.core.util.JsonUtils;
import ahodanenok.gwt.stub.core.util.ReflectionUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class MethodStubDialog extends Dialog<MethodStub> {

    private Stubs stubs;
    private StubsExceptionHandler exceptionHandler;

    private TextField idTextField;
    private ComboBox<ServiceStub> serviceStubComboBox;
    private ComboBox<Method> methodComboBox;
    private TextArea responseTextArea;
    private ToggleGroup returnTypeGroup;
    private RadioButton rpcRadio;
    private RadioButton jsonRadio;

    public MethodStubDialog(Stubs stubs, ServiceStub serviceStub, StubsExceptionHandler exceptionHandler) {
        this(stubs, serviceStub, null, exceptionHandler);
    }

    public MethodStubDialog(Stubs stubs, MethodStub methodStub, StubsExceptionHandler exceptionHandler) {
        this(stubs, null, methodStub, exceptionHandler);
    }

    private MethodStubDialog(Stubs stubs, ServiceStub serviceStub, MethodStub methodStub, StubsExceptionHandler exceptionHandler) {
        this.stubs = stubs;
        this.exceptionHandler = exceptionHandler;

        setResultConverter(new Callback<ButtonType, MethodStub>() {
            @Override
            public MethodStub call(ButtonType param) {
                if (param == ButtonType.OK) {
                    MethodStub resultStub = new MethodStub();
                    if (methodStub != null) {
                        resultStub.setId(methodStub.getId());
                    }
                    resultStub.setServiceStubId(serviceStubComboBox.getValue().getId());
                    resultStub.setMethod(methodComboBox.getValue());
                    resultStub.setReturnValue(responseTextArea.getText());

                    if (returnTypeGroup.getSelectedToggle() == rpcRadio) {
                        resultStub.setReturnValueType(ReturnValueType.GWT_RPC);
                    } else if (returnTypeGroup.getSelectedToggle() == jsonRadio) {
                        resultStub.setReturnValueType(ReturnValueType.JSON);
                    } else {
                        throw new IllegalStateException("Unknown return value type");
                    }

                    return resultStub;
                } else {
                    return null;
                }
            }
        });

        createForm();
        if (methodStub != null) {
            initForEdit(methodStub);
        } else if (serviceStub != null) {
            initForCreateWithServiceStub(serviceStub);
        } else {
            initForCreate();
        }
    }

    private void createForm() {
        Label idTitleLabel = ComponentUtils.createItemTitleLabel("ID:");
        idTextField = new TextField();
        idTextField.setDisable(true);

        Label serviceStubTitleLabel = ComponentUtils.createMandatoryItemTitleLabel("Service stub", ":");
        serviceStubComboBox = new ComboBox<>();
        serviceStubComboBox.setMaxWidth(Double.MAX_VALUE);
        serviceStubComboBox.setConverter(new StringConverter<ServiceStub>() {
            @Override
            public String toString(ServiceStub object) {
                return object.getDisplayString();
            }

            @Override
            public ServiceStub fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });

        Label methodTitleLabel = ComponentUtils.createMandatoryItemTitleLabel("Method", ":");
        methodComboBox = new ComboBox<>();
        methodComboBox.setMaxWidth(Double.MAX_VALUE);
        methodComboBox.setConverter(new StringConverter<Method>() {
            @Override
            public String toString(Method object) {
                return ReflectionUtils.toString(object);
            }

            @Override
            public Method fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });

        Label dataTitleLabel = ComponentUtils.createMandatoryItemTitleLabel("Response", ":");
        responseTextArea = new TextArea();
        responseTextArea.setWrapText(true);
        responseTextArea.setPrefHeight(400);

        Label returnValueTypeTitleLabel = ComponentUtils.createItemTitleLabel("Response type:");

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
        formPane.add(idTitleLabel, 0, 0);
        formPane.add(idTextField, 1, 0);
        GridPane.setHgrow(idTextField, Priority.ALWAYS);
        formPane.add(serviceStubTitleLabel, 0, 1);
        formPane.add(serviceStubComboBox, 1, 1);
        GridPane.setHgrow(serviceStubComboBox, Priority.ALWAYS);
        formPane.add(methodTitleLabel, 0, 2);
        formPane.add(methodComboBox, 1, 2);
        GridPane.setHgrow(methodComboBox, Priority.ALWAYS);
        formPane.add(returnValueTypeTitleLabel, 0, 3);
        formPane.add(new HBox(5, rpcRadio, jsonRadio), 1, 3);
        formPane.add(dataTitleLabel, 0, 4);
        GridPane.setValignment(dataTitleLabel, VPos.TOP);
        formPane.add(responseTextArea, 1, 4);
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

    private void initForCreate() {
        setTitle("Create method stub");

        idTextField.setText("< generated automatically >");

        List<ServiceStub> serviceStubs = Collections.emptyList();
        try {
            serviceStubs = stubs.listServiceStubs();
        } catch (StubsException e) {
            exceptionHandler.handleNotifyUser(e);
        }
        serviceStubComboBox.setItems(FXCollections.observableList(serviceStubs));
        serviceStubComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ServiceStub>() {
            @Override
            public void changed(ObservableValue<? extends ServiceStub> observable, ServiceStub oldValue, ServiceStub newValue) {
                if (newValue != null) {
                    methodComboBox.setItems(FXCollections.observableList(getAvailableMethods(newValue)));
                } else {
                    methodComboBox.setItems(null);
                }
            }
        });
    }

    private void initForCreateWithServiceStub(ServiceStub stub) {
        setTitle("Create method stub");

        idTextField.setText("< generated automatically >");

        serviceStubComboBox.setDisable(true);
        serviceStubComboBox.setValue(stub);

        methodComboBox.setItems(FXCollections.observableList(getAvailableMethods(stub)));
    }

    private void initForEdit(MethodStub methodStub) {
        setTitle("Edit method stub");

        idTextField.setText(methodStub.getId());

        try {
            serviceStubComboBox.setDisable(true);
            serviceStubComboBox.setValue(stubs.getServiceStub(methodStub.getServiceStubId()));
        } catch (StubsException e) {
            exceptionHandler.handleNotifyUser(e);
        }

        methodComboBox.setDisable(true);
        methodComboBox.setValue(methodStub.getMethod());

        responseTextArea.setText(methodStub.getReturnValue());

        if (methodStub.getReturnValueType() == ReturnValueType.JSON) {
            jsonRadio.setSelected(true);
        } else if (methodStub.getReturnValueType() == ReturnValueType.GWT_RPC) {
            rpcRadio.setSelected(true);
        } else {
            throw new IllegalStateException("Unknown return value type: " + methodStub.getReturnValueType());
        }
    }

    private List<Method> getAvailableMethods(ServiceStub serviceStub) {
        List<Method> methods = !serviceStub.isBroken() ? ReflectionUtils.getMethods(serviceStub.getServiceClass()) : Collections.emptyList();
        try {
            List<MethodStub> methodStubs = stubs.listMethodStubs(serviceStub.getId());
            Iterator<Method> methodsIterator = methods.iterator();
            // remove methods which have stubs
            while (methodsIterator.hasNext()) {
                Method m = methodsIterator.next();
                for (MethodStub stub : methodStubs) {
                    if (stub.getMethod().equals(m)) {
                        methodsIterator.remove();
                    }
                }
            }
        } catch (StubsException e) {
            exceptionHandler.handleNotifyUser(e);
            methods = Collections.emptyList();
        }

        return methods;
    }

    private boolean validate() {

        boolean valid = true;

        ComponentUtils.clearInvalid(serviceStubComboBox);
        ComponentUtils.clearInvalid(methodComboBox);
        ComponentUtils.clearInvalid(responseTextArea);

        if (serviceStubComboBox.getValue() == null) {
            ComponentUtils.markInvalid(serviceStubComboBox, "Please select service stub");
            valid = false;
        }

        Method selectedMethod = methodComboBox.getValue();
        if (selectedMethod == null) {
            ComponentUtils.markInvalid(methodComboBox, "Please select a method");
            valid = false;
        }

        Class<?> returnType = null;
        String data = null;
        if (selectedMethod != null) {
            returnType = selectedMethod.getReturnType();
            data = responseTextArea.getText();
        }

        if (StringUtils.isBlank(data)) {
            ComponentUtils.markInvalid(responseTextArea, "Please enter return value");
            valid = false;
        }

        if (data != null && returnType != null && returnTypeGroup.getSelectedToggle() == jsonRadio) {
            try {
                JsonUtils.fromJson(data, returnType);
            } catch (Exception e) {
                ComponentUtils.markInvalid(responseTextArea, "Not a valid JSON");
                valid = false;
            }
        }

        return valid;
    }

    public MethodStub getMethodStub() {
        Optional<MethodStub> result = showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }
}
