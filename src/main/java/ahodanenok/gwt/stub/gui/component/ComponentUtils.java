package ahodanenok.gwt.stub.gui.component;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow;

public class ComponentUtils {

    public static Label createItemTitleLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("item-title-label");
        return label;
    }

    public static Label createMandatoryItemTitleLabel(String text, String sep) {
        Label label = new Label(text + "*" + sep);
        label.getStyleClass().add("item-title-label");
        return label;
    }

    public static Label createItemValueLabel() {
        return createItemValueLabel(null);
    }

    public static Label createItemValueLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("item-value-label");
        return label;
    }

    public static void markInvalid(Control control, String msg) {
        control.getStyleClass().add("invalid");
        Tooltip t = new Tooltip(msg);
        t.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
//        t.getStyleClass().add("invalid");
        control.setTooltip(t);
    }

    public static void clearInvalid(Control control) {
        control.getStyleClass().remove("invalid");
        control.setTooltip(null);
    }
}
