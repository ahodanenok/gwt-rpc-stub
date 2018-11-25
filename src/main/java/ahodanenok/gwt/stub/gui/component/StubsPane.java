package ahodanenok.gwt.stub.gui.component;

import ahodanenok.gwt.stub.core.MethodStub;
import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.ServiceStub;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public final class StubsPane implements Component {

    private StubsTreeView stubsTreeView;
    private BorderPane pane;

    public StubsPane(Stubs stubs,
                     StubsExceptionHandler exceptionHandler) {

        this.pane = new BorderPane();

        stubsTreeView = new StubsTreeView(stubs, exceptionHandler);

        stubs.addProfileChangeListener(new Stubs.ProfileChangeListener() {
            @Override
            public void onChange(Profile newProfile, Profile oldProfile) {
                stubsTreeView.reload();
            }
        });

        stubs.addServiceStubSaveListener(new Stubs.ServiceStubSaveListener() {
            @Override
            public void onSave(ServiceStub stub) {
                stubsTreeView.refresh(stub);
            }
        });

        stubs.addServiceStubDeleteListener(new Stubs.ServiceStubDeleteListener() {
            @Override
            public void onDelete(ServiceStub stub) {
                stubsTreeView.remove(stub);
            }
        });

        stubs.addMethodStubSaveListener(new Stubs.MethodStubSaveListener() {
            @Override
            public void onSave(MethodStub stub) {
                stubsTreeView.refresh(stub);
            }
        });

        stubs.addMethodStubDeleteListener(new Stubs.MethodStubDeleteListener() {
            @Override
            public void onDelete(MethodStub stub) {
                stubsTreeView.remove(stub);
            }
        });

        ScrollPane centerScrollPane = new ScrollPane(stubsTreeView.asNode());
        centerScrollPane.setFitToWidth(true);
        centerScrollPane.setFitToHeight(true);
        pane.setCenter(centerScrollPane);

        // initial load
        stubsTreeView.reload();
    }

    @Override
    public Node asNode() {
        return pane;
    }
}
