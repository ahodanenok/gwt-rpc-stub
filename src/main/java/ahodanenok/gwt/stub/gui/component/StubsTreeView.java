package ahodanenok.gwt.stub.gui.component;

import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.action.*;
import ahodanenok.gwt.stub.core.MethodStub;
import ahodanenok.gwt.stub.core.ServiceStub;
import ahodanenok.gwt.stub.core.StubsException;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import javafx.scene.control.*;
import javafx.util.Callback;

public class StubsTreeView implements Component<TreeView<StubsTreeView.StubNode>> {

    interface StubNode { }

    private static class ServiceStubNode implements StubNode {

        private ServiceStub stub;

        ServiceStubNode(ServiceStub stub) {
            this.stub = stub;
        }

        ServiceStub getStub() {
            return stub;
        }
    }

    private static class MethodStubNode implements StubNode {

        private MethodStub stub;

        MethodStubNode(MethodStub stub) {
            this.stub = stub;
        }

        MethodStub getStub() {
            return stub;
        }
    }

    private static class StubNodeCell extends TreeCell<StubNode> {

        private Stubs stubs;
        private StubsExceptionHandler exceptionHandler;

        public StubNodeCell(Stubs stubs, StubsExceptionHandler exceptionHandler) {
            this.stubs = stubs;
            this.exceptionHandler = exceptionHandler;
        }

        @Override
        protected void updateItem(StubNode item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                setContextMenu(null);
            } else {
                if (item instanceof ServiceStubNode) {
                    ServiceStub stub = ((ServiceStubNode) item).getStub();
                    setText((stub.isBroken() ? "(BROKEN) " : "") + stub.getDisplayString() + " (Path: " + stub.getUrl() + ")");
                    setContextMenu(createServiceStubMenu(stub));
                } else if (item instanceof MethodStubNode) {
                    MethodStub stub = ((MethodStubNode) item).getStub();
                    setText((stub.isBroken() ? "(BROKEN) " : "") + stub.getDisplayString());
                    setContextMenu(createMethodStubMenu(stub));

                } else {
                    throw new IllegalStateException("Unknown stub node");
                }
            }
        }

        private ContextMenu createServiceStubMenu(ServiceStub stub) {

            MenuItem createMethodStubMenuItem = new MenuItem("Add method stub...");
            createMethodStubMenuItem.setOnAction(new CreateMethodStubAction(stubs, stub, exceptionHandler));

            MenuItem editMenuItem = new MenuItem("Edit...");
            editMenuItem.setOnAction(new UpdateServiceStubAction(stubs, stub, exceptionHandler));

            MenuItem deleteMenuItem = new MenuItem("Delete...");
            deleteMenuItem.setOnAction(new DeleteServiceStubAction(stubs, stub, exceptionHandler));

            return new ContextMenu(createMethodStubMenuItem, editMenuItem, deleteMenuItem);
        }

        private ContextMenu createMethodStubMenu(MethodStub stub) {
            MenuItem editMenuItem = new MenuItem("Edit...");
            editMenuItem.setOnAction(new UpdateMethodStubAction(stubs, stub, exceptionHandler));

            MenuItem deleteMenuItem = new MenuItem("Delete...");
            deleteMenuItem.setOnAction(new DeleteMethodStubAction(stubs, stub, exceptionHandler));

            return new ContextMenu(editMenuItem, deleteMenuItem);

        }
    }

    private Stubs stubs;
    private StubsExceptionHandler exceptionHandler;
    private TreeView<StubsTreeView.StubNode> treeView;

    public StubsTreeView(Stubs stubs, StubsExceptionHandler exceptionHandler) {
        this.stubs = stubs;
        this.exceptionHandler = exceptionHandler;
        this.treeView = new TreeView<>();

        treeView.setShowRoot(false);
        treeView.setEditable(false);
        treeView.setCellFactory(new Callback<TreeView<StubNode>, TreeCell<StubNode>>() {
            @Override
            public TreeCell<StubNode> call(TreeView<StubNode> param) {
                return new StubNodeCell(stubs, exceptionHandler);
            }
        });

        treeView.setRoot(new TreeItem<>());
        reload();
    }

    public void add(ServiceStub stub) {
        if (treeView.getRoot() != null) {
            treeView.getRoot().getChildren().add(new TreeItem<>(new ServiceStubNode(stub)));
        }
    }

    public void add(MethodStub stub) {
        try {
            ServiceStub serviceStub = stubs.getServiceStub(stub.getServiceStubId());
            TreeItem<StubNode> serviceItem = findServiceStubNode(serviceStub);
            if (serviceItem != null) {
                serviceItem.getChildren().add(new TreeItem<>(new MethodStubNode(stub)));
            }
        } catch (StubsException e) {
            exceptionHandler.handleNotifyUser(e);
        }
    }

    public void refresh(ServiceStub stub) {
        TreeItem<StubNode> stubTreeItem = findServiceStubNode(stub);
        if (stubTreeItem != null) {
            stubTreeItem.setValue(new ServiceStubNode(stub));
            treeView.refresh();
        } else {
            add(stub);
        }
    }

    public void refresh(MethodStub stub) {
        TreeItem<StubNode> methodItem = findMethodStubNode(stub);
        if (methodItem != null) {
            methodItem.setValue(new MethodStubNode(stub));
            treeView.refresh();
        } else {
            add(stub);
        }
    }

    public void remove(ServiceStub stub) {
        TreeItem<StubNode> stubTreeItem = findServiceStubNode(stub);
        if (stubTreeItem != null) {
            stubTreeItem.getParent().getChildren().remove(stubTreeItem);
        }
    }

    public void remove(MethodStub stub) {
        TreeItem<StubNode> methodItem = findMethodStubNode(stub);
        if (methodItem != null) {
            methodItem.getParent().getChildren().remove(methodItem);
        }
    }

    private TreeItem<StubNode> findServiceStubNode(ServiceStub stub) {
        TreeItem<StubNode> root = treeView.getRoot();
        if (root != null) {
            for (TreeItem<StubNode> serviceItem : root.getChildren()) {
                if (serviceItem.getValue() instanceof ServiceStubNode
                        && ((ServiceStubNode) serviceItem.getValue()).getStub().equals(stub)) {
                    return serviceItem;
                }
            }
        }

        return null;
    }

    private TreeItem<StubNode> findMethodStubNode(MethodStub stub) {
        TreeItem<StubNode> root = treeView.getRoot();
        if (root != null) {
            for (TreeItem<StubNode> serviceItem : root.getChildren()) {
                if (serviceItem.getValue() instanceof ServiceStubNode) {
                    for (TreeItem<StubNode> methodItem : serviceItem.getChildren()) {
                        if (methodItem.getValue() instanceof MethodStubNode
                                && ((MethodStubNode) methodItem.getValue()).getStub().equals(stub)) {
                            return methodItem;
                        }
                    }
                }
            }
        }

        return null;
    }

    public void reload() {
        try {
            createTree(treeView.getRoot());
        } catch (StubsException e) {
            exceptionHandler.handleNotifyUser(e);
        }
    }

    private void createTree(TreeItem<StubNode> root) throws StubsException {
        root.getChildren().clear();
        for (ServiceStub serviceStub : stubs.listServiceStubs()) {
            TreeItem<StubNode> serviceItem = new TreeItem<>(new ServiceStubNode(serviceStub));
            for (MethodStub methodStub : stubs.listMethodStubs(serviceStub.getId())) {
                TreeItem<StubNode> methodItem = new TreeItem<>(new MethodStubNode(methodStub));
                serviceItem.getChildren().add(methodItem);
            }

            root.getChildren().add(serviceItem);
        }
    }

    @Override
    public TreeView<StubNode> asNode() {
        return treeView;
    }
}
