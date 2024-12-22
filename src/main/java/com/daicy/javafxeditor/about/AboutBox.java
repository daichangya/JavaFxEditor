package com.daicy.javafxeditor.about;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.net.URL;

public class AboutBox extends Alert {

    public AboutBox() {
        super(AlertType.NONE);
        setTitle("About JavaFxEditor");

        try {
            URL resource = AboutBox.class.getResource("AboutBox.fxml");
            if (resource != null) {
                FXMLLoader loader = new FXMLLoader(resource);
                Parent content = loader.load();
                getDialogPane().setContent(content);
            } else {
                throw new IOException("FXML resource not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
    }
}
