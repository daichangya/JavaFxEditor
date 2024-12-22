package com.daicy.javafxeditor;

import javafx.application.Application;
import javafx.stage.Stage;
import org.fxmisc.richtext.StyleClassedTextArea;

public class TestEditor extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        EditorStrategy appStrategy = new EditorStrategy();
        JavaFxEditor javaFxEditor = new JavaFxEditor(appStrategy);
        javaFxEditor.start(primaryStage);
    }

}

