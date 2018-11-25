package ahodanenok.gwt.stub.gui.dialog;

import ahodanenok.gwt.stub.core.Config;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.component.ComponentUtils;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EditConfigDialog extends Dialog<Config> {

    private TextField portTextField;
    private TextField staticResourcesPath = new TextField();
    private TextArea classPathTextArea = new TextArea();

    public EditConfigDialog(Stubs stubs, StubsExceptionHandler exceptionHandler) {
        setTitle("Edit config | Profile: " + stubs.getActiveProfile().getDisplayName());

        setResultConverter(new Callback<ButtonType, Config>() {
            @Override
            public Config call(ButtonType param) {
                if (param == ButtonType.OK) {
                    Config config = new Config();
                    config.setServerPort(Integer.parseInt(portTextField.getText()));
                    config.setStaticResourcesPath(staticResourcesPath.getText());

                    List<String> cpList = new ArrayList<>();
                    String[] cp = StringUtils.split(classPathTextArea.getText());
                    Collections.addAll(cpList, cp);
                    config.setClassPath(cpList);

                    return config;
                } else {
                    return null;
                }
            }
        });

        Config config = stubs.getConfig();

        Label portTitleLabel = ComponentUtils.createMandatoryItemTitleLabel("Port", ":");
        portTextField = new TextField(Integer.toString(config.getServerPort()));

        Label staticResourcesTitleLabel = ComponentUtils.createMandatoryItemTitleLabel("Static resources", ":");
        staticResourcesPath = new TextField(config.getStaticResourcesPath());

        Label classPathTitleLabel = ComponentUtils.createItemTitleLabel("Classpath:");
        classPathTextArea = new TextArea(StringUtils.join(config.getClassPath(), "\n"));
        classPathTextArea.setWrapText(true);
        classPathTextArea.setPrefHeight(400);

        GridPane formPane = new GridPane();
        formPane.setHgap(5);
        formPane.setVgap(5);
        formPane.add(portTitleLabel, 0, 0);
        formPane.add(portTextField, 1, 0);
        GridPane.setHgrow(portTextField, Priority.ALWAYS);
        formPane.add(staticResourcesTitleLabel, 0, 1);
        formPane.add(staticResourcesPath, 1, 1);
        GridPane.setHgrow(staticResourcesPath, Priority.ALWAYS);
        formPane.add(classPathTitleLabel, 0, 2);
        GridPane.setValignment(classPathTitleLabel, VPos.TOP);
        formPane.add(classPathTextArea, 1, 2);
        GridPane.setHgrow(classPathTextArea, Priority.ALWAYS);

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

        ComponentUtils.clearInvalid(portTextField);
        ComponentUtils.clearInvalid(staticResourcesPath);

        boolean valid = true;

        if (StringUtils.isBlank(portTextField.getText())) {
            ComponentUtils.markInvalid(portTextField, "Please enter port");
            valid = false;
        } else {
            try {
                Integer.parseInt(portTextField.getText());
            } catch (NumberFormatException e) {
                ComponentUtils.markInvalid(portTextField, "Invalid port");
                valid = false;
            }
        }

        if (StringUtils.isBlank(staticResourcesPath.getText())) {
            ComponentUtils.markInvalid(portTextField, "Please enter static resources path");
            valid = false;
        }

        return valid;
    }

    public Config getConfig() {
        Optional<Config> data = showAndWait();
        if (data.isPresent()) {
            return data.get();
        } else {
            return null;
        }
    }
}
