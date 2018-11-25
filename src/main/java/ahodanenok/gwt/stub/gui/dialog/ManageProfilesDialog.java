package ahodanenok.gwt.stub.gui.dialog;

import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.action.ActionResultHandler;
import ahodanenok.gwt.stub.gui.action.CreateProfileAction;
import ahodanenok.gwt.stub.gui.action.DeleteProfileAction;
import ahodanenok.gwt.stub.gui.action.UpdateProfileAction;
import ahodanenok.gwt.stub.core.StubsException;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

public class ManageProfilesDialog extends Dialog<ButtonType> {

    public ManageProfilesDialog(Stubs stubs, StubsExceptionHandler exceptionHandler) {

        setTitle("Manage profiles");

        ListView<Profile> profileListView = new ListView<>();
        profileListView.setEditable(false);
        profileListView.setMaxWidth(Double.MAX_VALUE);
        profileListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        profileListView.setCellFactory(new Callback<ListView<Profile>, ListCell<Profile>>() {
            @Override
            public ListCell<Profile> call(ListView<Profile> param) {
                return new ListCell<Profile>() {
                    @Override
                    protected void updateItem(Profile item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item.getDisplayName());
                        }
                    }
                };
            }
        });
        try {
            profileListView.setItems(FXCollections.observableList(stubs.listProfiles()));
        } catch (StubsException e) {
            exceptionHandler.handleNotifyUser(e);
        }

        Button addBtn = new Button("Add");
        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CreateProfileAction action = new CreateProfileAction(stubs, exceptionHandler);
                action.setResultHandler(new ActionResultHandler<Profile>() {
                    @Override
                    public void handleResult(Profile result) {
                        profileListView.getItems().add(result);
                    }
                });
                action.handle(event);
            }
        });

        Button editBtn = new Button("Edit");
        editBtn.disableProperty().bind(profileListView.getSelectionModel().selectedItemProperty().isNull());
        editBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UpdateProfileAction action = new UpdateProfileAction(stubs, profileListView.getSelectionModel().getSelectedItem(), exceptionHandler);
                action.setResultHandler(new ActionResultHandler<Profile>() {
                    @Override
                    public void handleResult(Profile result) {
                        if (result == null) {
                            return;
                        }

                        int idx = profileListView.getItems().indexOf(result);
                        profileListView.getItems().set(idx, result);
                        profileListView.getSelectionModel().clearSelection();
                        profileListView.getSelectionModel().select(result);
                        profileListView.refresh();
                    }
                });
                action.handle(event);
            }
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.disableProperty().bind(
                profileListView.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Profile selectedProfile = profileListView.getSelectionModel().getSelectedItem();
                DeleteProfileAction action = new DeleteProfileAction(stubs, selectedProfile, exceptionHandler);
                action.setResultHandler(new ActionResultHandler<Boolean>() {
                    @Override
                    public void handleResult(Boolean result) {
                        if (Boolean.TRUE.equals(result)) {
                            profileListView.getItems().remove(selectedProfile);
                        }
                    }
                });
                action.handle(event);
            }
        });

        HBox buttonsBox = new HBox(5, addBtn, editBtn, deleteBtn);

        GridPane formPane = new GridPane();
        formPane.setHgap(5);
        formPane.setVgap(5);
        formPane.add(buttonsBox, 0, 0);
        GridPane.setHgrow(buttonsBox, Priority.ALWAYS);
        formPane.add(profileListView, 0, 1);
        GridPane.setHgrow(profileListView, Priority.ALWAYS);
        GridPane.setVgrow(profileListView, Priority.ALWAYS);

        getDialogPane().setContent(formPane);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        setResizable(true);
        getDialogPane().setPrefWidth(400);
    }
}
