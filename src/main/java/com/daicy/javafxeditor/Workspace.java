package com.daicy.javafxeditor;

import java.io.File;
import java.util.Optional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public abstract class Workspace {

    private final ObjectProperty<Optional<File>> documentFileOptional = new SimpleObjectProperty<>(Optional.empty());
    private final SimpleBooleanProperty modified = new SimpleBooleanProperty(false);
    private final Stage stage;
    private final FileChooser documentFileChooser;

    public Workspace(Stage stage, FileChooser documentFileChooser) {
        this.stage = stage;
        this.documentFileChooser = documentFileChooser;
    }

    public ReadOnlyBooleanProperty modifiedProperty() {
        return modified;
    }

    public boolean isModified() {
        return modified.get();
    }

    public void setModified(boolean value) {
        modified.set(value);
    }

    public ObjectProperty<Optional<File>> documentFileOptionalProperty() {
        return documentFileOptional;
    }

    public Optional<File> getDocumentFileOptional() {
        return documentFileOptional.get();
    }

    public void setDocumentFileOptional(Optional<File> value) {
        documentFileOptional.set(value);
    }

    public void bindEvents() {
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (!canLeaveDocument()) {
                    event.consume();
                }
            }
        });
    }

    public boolean newDocument() {
        try {
            if (!canLeaveDocument()) {
                return false;
            }

            if (!doNew()) {
                return false;
            }

            setDocumentFileOptional(Optional.empty());
            setModified(false);

            return true;
        } catch (Exception ex) {
            showException(ex);
            return false;
        }
    }

    protected abstract boolean doNew();

    public boolean openDocument() {
        try {
            if (!canLeaveDocument()) {
                return false;
            }

            File selectedFile = documentFileChooser.showOpenDialog(stage);

            if (selectedFile == null) {
                return false;
            }

            if (!doOpen(selectedFile)) {
                return false;
            }

            setDocumentFileOptional(Optional.of(selectedFile));
            setModified(false);

            return true;
        } catch (Exception ex) {
            showException(ex);
            return false;
        }
    }

    protected abstract boolean doOpen(File sourceFile);

    public boolean saveDocument() {
        try {
            Optional<File> fileOptional = getDocumentFileOptional();
            if (fileOptional.isEmpty()) {
                return saveAsDocument();
            } else {
                File file = fileOptional.get();
                boolean saveResult = doSave(file);
                if (saveResult) {
                    setModified(false);
                }
                return saveResult;
            }
        } catch (Exception ex) {
            showException(ex);
            return false;
        }
    }

    public boolean saveAsDocument() {
        try {
            File selectedFile = documentFileChooser.showSaveDialog(stage);

            if (selectedFile == null) {
                return false;
            }

            if (!doSave(selectedFile)) {
                return false;
            }

            setDocumentFileOptional(Optional.of(selectedFile));
            setModified(false);

            return true;
        } catch (Exception ex) {
            showException(ex);
            return false;
        }
    }

    protected abstract boolean doSave(File targetFile);

    public void closeStage() {
        WindowEvent closeEvent = new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST);
        stage.fireEvent(closeEvent);
    }

    private boolean canLeaveDocument() {
        if (!isModified()) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to save your work?");
        alert.setTitle("Unsaved Changes");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        ButtonType cancelButton = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);

        ButtonType result = alert.showAndWait().orElse(cancelButton);

        if (result.equals(yesButton)) {
            return saveDocument();
        } else if (result.equals(noButton)) {
            return true;
        } else {
            return false;
        }
    }

    private void showException(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(ex.getMessage());

        alert.showAndWait();
    }
}
