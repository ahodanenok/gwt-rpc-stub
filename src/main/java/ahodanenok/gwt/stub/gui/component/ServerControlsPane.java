package ahodanenok.gwt.stub.gui.component;

import ahodanenok.gwt.stub.core.Config;
import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.core.StubsServer;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public final class ServerControlsPane implements Component {

    private BorderPane pane;

    private Label portLabel;
    private Label resourcesLabel;

    private Button startServerBtn;
    private Button stopServerBtn;

    private StubsServer server;

    public ServerControlsPane(StubsServer server,
                              StubsExceptionHandler exceptionHandler) {
        this.pane = new BorderPane();
        this.pane.getStyleClass().add("server-pane");

        this.server = server;

        startServerBtn = new Button("Start");
        startServerBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                server.start();
            }
        });

        stopServerBtn = new Button("Stop");
        stopServerBtn.setDisable(true);
        stopServerBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                server.stop();
            }
        });

        Label statusTitleLabel = ComponentUtils.createItemTitleLabel("Server status: ");

        final Label statusLabel = new Label(server.getStatus().toString());

        Label portTitleLabel = ComponentUtils.createItemTitleLabel("Port: ");

        portLabel = new Label();

        Label resourcesTitleLabel = ComponentUtils.createItemTitleLabel("Static resources: ");

        resourcesLabel = new Label();

        updateServerInfo();
        updateControlButtons();

        server.getStubs().addProfileChangeListener(new Stubs.ProfileChangeListener() {
            @Override
            public void onChange(Profile newProfile, Profile oldProfile) {
                updateServerInfo();
                updateControlButtons();
            }
        });

        server.getStubs().addConfigSaveListener(new Stubs.ConfigSaveListener() {
            @Override
            public void onSave(Config config) {
                updateServerInfo();
            }
        });

        server.setStatusListener(new StubsServer.StatusListener() {
            public void onStatusChange(StubsServer.Status newStatus) {
                statusLabel.setText(newStatus.toString());
                for (StubsServer.Status s : StubsServer.Status.values()) {
                    statusLabel.getStyleClass().removeAll("server-status-" + s.name().toLowerCase());
                }
                statusLabel.getStyleClass().add("server-status-" + newStatus.name().toLowerCase());

                updateControlButtons();
            }

            @Override
            public void onError(Throwable e) {
                exceptionHandler.handleNotifyUser(e);
            }
        });

        GridPane serverInfoPane = new GridPane();
        serverInfoPane.add(statusTitleLabel, 0, 0);
        serverInfoPane.add(statusLabel, 1, 0);
        serverInfoPane.add(portTitleLabel, 0, 1);
        serverInfoPane.add(portLabel, 1, 1);
        serverInfoPane.add(resourcesTitleLabel, 0, 2);
        serverInfoPane.add(resourcesLabel, 1, 2);
        pane.setLeft(serverInfoPane);

        HBox right = new HBox(5, startServerBtn, stopServerBtn);
        pane.setRight(right);
    }

    private void updateServerInfo() {
        if (server.isConfigured()) {
            portLabel.setText(Integer.toString(server.getPort()));
            resourcesLabel.setText(server.getStaticResourcesPath());
        } else {
            portLabel.setText("---");
            resourcesLabel.setText("---");
        }
    }

    private void updateControlButtons() {
        if (!server.isConfigured()) {
            startServerBtn.setDisable(true);
            stopServerBtn.setDisable(true);
            return;
        }

        StubsServer.Status status = server.getStatus();
        if (status == StubsServer.Status.ERROR
                || status == StubsServer.Status.STOPPED) {
            startServerBtn.setDisable(false);
        } else {
            startServerBtn.setDisable(true);
        }

        if (status == StubsServer.Status.RUNNING) {
            stopServerBtn.setDisable(false);
        } else {
            stopServerBtn.setDisable(true);
        }
    }

    @Override
    public Node asNode() {
        return pane;
    }
}
