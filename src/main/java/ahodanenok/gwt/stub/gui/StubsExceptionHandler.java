package ahodanenok.gwt.stub.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class StubsExceptionHandler {

    private static Logger LOGGER = Logger.getLogger(StubsExceptionHandler.class.getName());

    public interface ErrorHandleListener {
        void onHandle(Throwable t);
    }

    private ErrorHandleListener errorHandleListener;

    public void setErrorHandleListener(ErrorHandleListener errorHandleListener) {
        this.errorHandleListener = errorHandleListener;
    }

    public void handle(Throwable e) {
        LOGGER.log(Level.SEVERE, "Error", e);
        if (errorHandleListener != null) {
            errorHandleListener.onHandle(e);
        }
    }

    public void handleNotifyUser(Throwable e) {
        handle(e);
        showExceptionDialog(e);
    }

    private void showExceptionDialog(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(null);
        alert.setResizable(true);

        String errorMsg;
        if (StringUtils.isNotBlank(t.getMessage())) {
            errorMsg = t.getMessage();
        } else {
            errorMsg = t.getClass().getName();
        }

        alert.setContentText("Exception:\n" + errorMsg);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(true);
        alert.getDialogPane().setPrefWidth(600);
        alert.getDialogPane().setPrefHeight(400);

        alert.showAndWait();
    }
}

