package ahodanenok.gwt.stub.gui.component;

import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.action.*;
import ahodanenok.gwt.stub.gui.dialog.ManageProfilesDialog;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public final class StubsMenuBar implements Component<MenuBar> {

    private MenuBar menuBar;

    public StubsMenuBar(Stubs stubs,
                        StubsExceptionHandler exceptionHandler) {

        MenuItem addServiceStubMenuItem = new MenuItem("Create service stub...");
        addServiceStubMenuItem.setOnAction(new CreateServiceStubAction(stubs, exceptionHandler));

        MenuItem addMethodStubMenuItem = new MenuItem("Create method stub...");
        addMethodStubMenuItem.setOnAction(new CreateMethodStubAction(stubs, exceptionHandler));

        MenuItem importReqRespMenuItem = new MenuItem("From request...");
        importReqRespMenuItem.setOnAction(new ImportFromRequestAction(stubs, exceptionHandler));

        Menu importMenu = new Menu("Import method stub");
        importMenu.getItems().add(importReqRespMenuItem);

        MenuItem profilesMenuItem = new MenuItem("Manage profiles...");
        profilesMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new ManageProfilesDialog(stubs, exceptionHandler).showAndWait();
            }
        });

        MenuItem setActiveProfileMenuItem = new MenuItem("Set active profile...");
        setActiveProfileMenuItem.setOnAction(new SelectProfileAction(stubs, exceptionHandler));

        MenuItem editConfigMenuItem = new MenuItem("Edit profile config...");
        editConfigMenuItem.setOnAction(new EditConfigAction(stubs, exceptionHandler));

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        Menu stubsMenu = new Menu("Stubs");
        stubsMenu.getItems().add(addServiceStubMenuItem);
        stubsMenu.getItems().add(addMethodStubMenuItem);
        stubsMenu.getItems().add(importMenu);
        stubsMenu.getItems().add(new SeparatorMenuItem());
        stubsMenu.getItems().add(exitMenuItem);

        Menu profilesMenu = new Menu("Profiles");
        profilesMenu.getItems().add(profilesMenuItem);
        profilesMenu.getItems().add(setActiveProfileMenuItem);
        profilesMenu.getItems().add(new SeparatorMenuItem());
        profilesMenu.getItems().add(editConfigMenuItem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(stubsMenu);
        menuBar.getMenus().addAll(profilesMenu);
    }

    @Override
    public MenuBar asNode() {
        return menuBar;
    }
}
