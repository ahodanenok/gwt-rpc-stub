package ahodanenok.gwt.stub.gui;

import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.core.StubsServer;
import ahodanenok.gwt.stub.gui.component.StubsPane;
import ahodanenok.gwt.stub.gui.component.ServerControlsPane;
import ahodanenok.gwt.stub.gui.component.StubsMenuBar;
import ahodanenok.gwt.stub.gui.component.TitleBar;
import ahodanenok.gwt.stub.core.storage.StubsFileStorage;

import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.FileInputStream;
import java.util.logging.LogManager;

public class StubsApp extends Application {

    private static final String LOGGING_CONFIG_PATH = "cfg/logging.properties";
    private static final String STORAGE_PATH = "storage";

    public static void main(String[] args) {
        Application.launch(StubsApp.class, args);
    }

    public void start(Stage primaryStage) throws Exception {
        StubsExceptionHandler exceptionHandler = new StubsExceptionHandler();
        setUpUncaughtExceptionsHandler(exceptionHandler);

        setUpLogging();

        Stubs stubs = new Stubs(new StubsFileStorage(STORAGE_PATH));
        final StubsServer server = new StubsServer(stubs);
        server.setExceptionHandler(new StubsServer.ExceptionHandler() {
            @Override
            public void handle(Throwable t) {
                exceptionHandler.handle(t);
            }
        });

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet("/ahodanenok/gwt/stub/gui/style.css");

        setUpPrimaryStage(primaryStage, stubs, server, exceptionHandler);
        primaryStage.show();
    }

    private void setUpUncaughtExceptionsHandler(StubsExceptionHandler exceptionHandler) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                exceptionHandler.handleNotifyUser(e);
            }
        });
    }

    private void setUpLogging() {
        String logConfigFile = System.getProperty("java.util.logging.config.file");
        if (logConfigFile == null || logConfigFile.trim().length() == 0) {
            // если не указан java.util.logging.config.file,
            // то используем конфигурацию по умолчанию из LOGGING_CONFIG_PATH
            try {
                LogManager.getLogManager().readConfiguration(new FileInputStream(LOGGING_CONFIG_PATH));
            } catch (Exception e) {
                System.err.println("Can't load logging configuration from classpath: " + LOGGING_CONFIG_PATH);
            }
        }
    }

    private void setUpPrimaryStage(final Stage stage,
                                   Stubs stubs,
                                   StubsServer server,
                                   StubsExceptionHandler exceptionHandler) {

        StubsMenuBar menuBar = new StubsMenuBar(stubs, exceptionHandler);
        TitleBar titleBar = new TitleBar(stubs, exceptionHandler);
        ServerControlsPane controlsPane = new ServerControlsPane(server, exceptionHandler);
        StubsPane stubsPane = new StubsPane(stubs, exceptionHandler);

        VBox topBox = new VBox();
        topBox.getChildren().add(menuBar.asNode());
        topBox.getChildren().add(controlsPane.asNode());
        topBox.getChildren().add(new Separator(Orientation.HORIZONTAL));
        topBox.getChildren().add(titleBar.asNode());

        BorderPane rootPane = new BorderPane();
        rootPane.setTop(topBox);
        rootPane.setCenter(stubsPane.asNode());

        Scene scene = new Scene(rootPane);

        setStageTitle(stage, stubs.getActiveProfile());
        stubs.addProfileChangeListener(new Stubs.ProfileChangeListener() {
            @Override
            public void onChange(Profile newProfile, Profile oldProfile) {
                setStageTitle(stage, newProfile);
            }
        });

        stage.setScene(scene);
        stage.setWidth(1000);
        stage.setHeight(600);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (server.getStatus() == StubsServer.Status.RUNNING) {
                    server.stop();
                }
            }
        });
    }

    private void setStageTitle(Stage stage, Profile profile) {
        if (profile != null) {
            stage.setTitle("Stubs | Profile: " + profile.getDisplayName());
        } else {
            stage.setTitle("Stubs");
        }
    }
}
