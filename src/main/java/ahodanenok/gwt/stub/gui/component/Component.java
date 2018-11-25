package ahodanenok.gwt.stub.gui.component;

import javafx.scene.Node;

public interface Component<T extends Node> {

    T asNode();
}
