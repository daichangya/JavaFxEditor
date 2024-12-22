package com.daicy.javafxeditor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
//import info.gianlucacosta.helios.fx.workspace.Workspace;
//import scalafx.stage.FileChooser;
//import scalafx.scene.control.TextArea;
//import scalafx.scene.input.KeyCodeCombination;
//import scalafx.scene.input.KeyCombination.ModifierValue;
//import scalafx.scene.input.KeyEvent;
//import scalafx.scene.input.KeyCode;

public class JavaFxWorkspace extends Workspace {

    private final Stage stage;
    private final FileChooser documentFileChooser;
    private final CodeArea codeEditor;

    public JavaFxWorkspace(Stage stage, FileChooser documentFileChooser, CodeArea codeEditor) {
        super(stage, documentFileChooser);
        this.stage = stage;
        this.documentFileChooser = documentFileChooser;
        this.codeEditor = codeEditor;
    }

    @Override
    protected boolean doNew() {
        codeEditor.clear();
        codeEditor.getUndoManager().forgetHistory();
        codeEditor.requestFocus();
        return true;
    }

    @Override
    protected boolean doOpen(File sourceFile) {
        try {
            String fileContent = new String(Files.readAllBytes(sourceFile.toPath()), StandardCharsets.UTF_8);
//            codeEditor.setText(fileContent);
            codeEditor.replaceText(0, 0, fileContent);
            codeEditor.getUndoManager().forgetHistory();
            codeEditor.requestFocus();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean doSave(File targetFile) {
        try {
            Files.write(targetFile.toPath(), codeEditor.getText().getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
