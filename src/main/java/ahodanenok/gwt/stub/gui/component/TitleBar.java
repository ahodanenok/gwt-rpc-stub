package ahodanenok.gwt.stub.gui.component;

import ahodanenok.gwt.stub.core.Profile;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import ahodanenok.gwt.stub.gui.action.SelectProfileAction;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class TitleBar implements Component<Node> {

    private BorderPane bar;

    private Label profileTitleLabel;
    private Label profileLabel;
    private Button changeProfileBtn;

    public TitleBar(Stubs stubs,
                    StubsExceptionHandler exceptionHandler) {

        Label titleLabel = new Label("Service stubs");
        titleLabel.getStyleClass().add("title-bar-title");

        profileTitleLabel = ComponentUtils.createItemTitleLabel("Profile:");

        profileLabel = ComponentUtils.createItemValueLabel();

        changeProfileBtn = new Button();
        changeProfileBtn.setOnAction(new SelectProfileAction(stubs, exceptionHandler));

        updateProfileInfo(stubs.getActiveProfile());

        stubs.addProfileChangeListener(new Stubs.ProfileChangeListener() {
            @Override
            public void onChange(Profile newProfile, Profile oldProfile) {
                updateProfileInfo(newProfile);
            }
        });

        stubs.addProfileSaveListener(new Stubs.ProfileSaveListener() {
            @Override
            public void onSave(Profile profile) {
                if (stubs.getActiveProfile() != null && stubs.getActiveProfile().equals(profile)) {
                    profileLabel.setText(profile.getDisplayName());
                }
            }
        });

        HBox titleRightBox = new HBox(5, profileTitleLabel, profileLabel, changeProfileBtn);
        titleRightBox.setAlignment(Pos.CENTER_RIGHT);

        bar = new BorderPane();
        bar.getStyleClass().add("title-bar");
        bar.setLeft(titleLabel);
        bar.setRight(titleRightBox);
    }

    private void updateProfileInfo(Profile profile) {
        if (profile != null) {
            profileTitleLabel.setVisible(true);
            profileLabel.setText(profile.getDisplayName());
            profileLabel.setVisible(true);
            changeProfileBtn.setText("Change");
        } else {
            profileTitleLabel.setVisible(false);
            profileLabel.setText(null);
            profileLabel.setVisible(false);
            changeProfileBtn.setText("Select profile");
        }
    }

    @Override
    public Node asNode() {
        return bar;
    }
}
