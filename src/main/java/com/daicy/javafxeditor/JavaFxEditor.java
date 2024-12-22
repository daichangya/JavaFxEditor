package com.daicy.javafxeditor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class JavaFxEditor {

    private final AppStrategy appStrategy;

    public JavaFxEditor(AppStrategy appStrategy) {
        this.appStrategy = appStrategy;
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        BorderPane root = (BorderPane) loader.load();
        MainWindowController mainWindowController = loader.getController();

        Scene scene = new Scene(root);

        String syntaxCssUrl = appStrategy.getSyntaxCss().toExternalForm();
        scene.getStylesheets().add(syntaxCssUrl);

        mainWindowController.init(primaryStage, appStrategy);

        javafx.application.Platform.runLater(() -> {
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }
}
