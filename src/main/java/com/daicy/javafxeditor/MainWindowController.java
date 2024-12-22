package com.daicy.javafxeditor;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.reactfx.value.Val;


public class MainWindowController {

    private static final Duration OUTPUT_REFRESH_RATE = Duration.ofMillis(300);

    private Stage stage;
    private AppStrategy appStrategy;

    private CodeArea codeEditor;

    private JavaFxWorkspace workspace;

    private final BooleanProperty runningProperty = new SimpleBooleanProperty(false);


    private FileChooser sourceFileChooser;
    private FileChooser outputFileChooser;

    @FXML
    private BorderPane editorPane;

    @FXML
    private MenuItem newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, undoMenuItem, redoMenuItem,
            cutMenuItem, copyMenuItem, pasteMenuItem, saveOutputMenuItem, startMenuItem, stopMenuItem,
            settingsMenuItem, onlineReferenceMenuItem, aboutMenuItem;

    @FXML
    private Button newButton, openButton, saveButton, saveAsButton, undoButton, redoButton, cutButton, copyButton,
            pasteButton, startButton, stopButton, onlineReferenceButton, aboutButton;

    @FXML
    private TextArea outputArea;

    public void init(Stage stage, AppStrategy appStrategy) {
        this.stage = stage;
        this.appStrategy = appStrategy;

        this.sourceFileChooser = appStrategy.createSourceFileChooser();
        this.codeEditor = appStrategy.createCodeEditor();

        this.outputFileChooser = new FileChooser();
        this.outputFileChooser.setTitle(appStrategy.getTitle());
        this.outputFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text file", "*.txt"),
                new FileChooser.ExtensionFilter("Any file", "*.*")
        );

        workspace = new JavaFxWorkspace(stage, sourceFileChooser, codeEditor);
        workspace.bindEvents();

        editorPane.setCenter(codeEditor);

        initBindings();
    }

    private void initBindings() {
        stage.titleProperty().bind(Bindings.createStringBinding(() -> {
            String titleBase = appStrategy.getTitle();

            Optional<String> fileString = workspace.getDocumentFileOptional().map(file -> " - " + file.getName());
            String modifiedString = workspace.isModified() ? " *" : "";

            return titleBase + fileString.orElse("") + modifiedString;
        }, workspace.documentFileOptionalProperty(), workspace.modifiedProperty()));

        setupMenusAndToolbar();

        codeEditor.disableProperty().bind(runningProperty);

        codeEditor.textProperty().addListener((Observable observable) -> {
            Platform.runLater(() -> workspace.setModified(true));
        });
    }

    private void setupMenusAndToolbar() {
        newMenuItem.disableProperty().bind(runningProperty);
        bindButton(newButton, newMenuItem);
        openMenuItem.disableProperty().bind(runningProperty);
        bindButton(openButton, openMenuItem);

        saveMenuItem.disableProperty().bind(runningProperty.or(workspace.modifiedProperty().not()));
        bindButton(saveButton, saveMenuItem);

        saveAsMenuItem.disableProperty().bind(runningProperty);
        bindButton(saveAsButton, saveAsMenuItem);

        undoMenuItem.disableProperty().bind(runningProperty.or(adapt(codeEditor.undoAvailableProperty())));
        bindButton(undoButton, undoMenuItem);

        redoMenuItem.disableProperty().bind(runningProperty.or(adapt(codeEditor.redoAvailableProperty())));
        bindButton(redoButton, redoMenuItem);

        BooleanBinding noSelectedCodeText = Bindings.createBooleanBinding(
                () -> codeEditor.getSelectedText().isEmpty(),
                codeEditor.selectedTextProperty()
        );
        cutMenuItem.disableProperty().bind(noSelectedCodeText.or(runningProperty));
        bindButton(cutButton, cutMenuItem);

        copyMenuItem.disableProperty().bind(noSelectedCodeText.or(runningProperty));
        bindButton(copyButton, copyMenuItem);

        pasteMenuItem.disableProperty().bind(runningProperty);
        bindButton(pasteButton, pasteMenuItem);


        settingsMenuItem.setVisible(appStrategy.settingsSupported());
//        disableWhenRunning(settingsMenuItem, null);
        settingsMenuItem.disableProperty().bind(runningProperty);

        onlineReferenceMenuItem.disableProperty().bind(runningProperty);
        bindButton(onlineReferenceButton, onlineReferenceMenuItem);
        aboutMenuItem.disableProperty().bind(runningProperty);
        bindButton(aboutButton, aboutMenuItem);
    }

    public static ReadOnlyBooleanProperty adapt(Val<Boolean> val) {
        SimpleBooleanProperty property = new SimpleBooleanProperty();
        property.set(val.getValue());
        return property;
    }

//    private void disableWhenRunning(MenuItem menuItem, Button button) {
//        menuItem.disableProperty().bind(runningProperty);
//        if (button != null) {
//            button.disableProperty().bind(runningProperty);
//        }
//    }

    private void bindButton(Button button, MenuItem menuItem) {
        String buttonId = button.getId();
        String actionName = buttonId.substring(0, buttonId.lastIndexOf("Button"));
        String expectedMenuItemId = actionName + "MenuItem";

        if (!menuItem.getId().equals(expectedMenuItemId)) {
            throw new IllegalArgumentException("'" + menuItem.getId() + "' should be named '" + expectedMenuItemId + "' instead");
        }

        Image actionImage = new Image(getClass().getResourceAsStream("actionIcons/" + actionName + ".png"));

        menuItem.setGraphic(new ImageView(actionImage));
        button.setGraphic(new ImageView(actionImage));

        button.setOnAction(event -> Event.fireEvent(menuItem,new ActionEvent()));

        Tooltip tooltip = new Tooltip(menuItem.getText());
        button.setTooltip(tooltip);
    }

    @FXML
    private void newDocument(ActionEvent event) {
        workspace.newDocument();
    }

    @FXML
    private void openDocument(ActionEvent event) {
        workspace.openDocument();
    }

    @FXML
    private void saveDocument(ActionEvent event) {
        workspace.saveDocument();
    }

    @FXML
    private void saveAsDocument(ActionEvent event) {
        workspace.saveAsDocument();
    }

    @FXML
    private void exitProgram(ActionEvent event) {
        workspace.closeStage();
    }

    @FXML
    private void undo(ActionEvent event) {
        codeEditor.undo();
    }

    @FXML
    private void redo(ActionEvent event) {
        codeEditor.redo();
    }

    @FXML
    private void cut(ActionEvent event) {
        codeEditor.cut();
    }

    @FXML
    private void copy(ActionEvent event) {
        codeEditor.copy();
    }

    @FXML
    private void paste(ActionEvent event) {
        codeEditor.paste();
    }

    @FXML
    private void selectAll(ActionEvent event) {
        codeEditor.selectAll();
    }

    @FXML
    private void saveOutput(ActionEvent event) {
        java.io.File chosenFile = outputFileChooser.showSaveDialog(stage);

        if (chosenFile == null) {
            return;
        }

        try {
            Files.write(chosenFile.toPath(), outputArea.getText().getBytes());
        } catch (IOException e) {
            Alerts.showException(e);
        }
    }

    @FXML
    private void showSettings(ActionEvent event) {
        appStrategy.showSettings();
    }

    @FXML
    private void showOnlineReference(ActionEvent event) {
        appStrategy.showOnlineReference();
    }

    @FXML
    private void showAboutWindow(ActionEvent event) {
        appStrategy.showAboutWindow();
    }
}
