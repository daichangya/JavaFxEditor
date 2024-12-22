package com.daicy.javafxeditor.about;

import com.daicy.javafxeditor.apps.AppInfo;
import com.daicy.javafxeditor.apps.JavaFxEditorAppInfo;
import com.daicy.javafxeditor.desktop.DesktopUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;


public class AboutBoxController {

    private AppInfo appInfo = new JavaFxEditorAppInfo();

    @FXML
    private Label titleLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private Label copyrightLabel;

    @FXML
    private Label additionalInfoLabel;

    @FXML
    private ImageView mainIconImageView;

    @FXML
    public void initialize() {
        titleLabel.setText(appInfo.getTitle());
        versionLabel.setText("Version " + appInfo.getVersion());
        copyrightLabel.setText("Copyright \u00A9 " + appInfo.getCopyrightYears() + " " + appInfo.getCopyrightHolder() + ".");
        additionalInfoLabel.setText("For further information, please refer to the LICENSE and README files.");
        mainIconImageView.setImage(appInfo.getMainIconImage());

    }

    @FXML
    public void showAuthorPage() {
        DesktopUtils.openBrowser(appInfo.getAuthorPage(),null);
    }


    @FXML
    public void showJavaFxEditorPage() {
        DesktopUtils.openBrowser(appInfo.getWebsite(),null);
    }

}
