package com.daicy.javafxeditor;

import javafx.stage.FileChooser;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;



/**
 * Strategy employed to define customized IDE behavior
 */
public interface AppStrategy {

    /**
     * Application title, shown in windows and dialogs.
     *
     * @return The application title
     */
    String getTitle();

    /**
     * Creates the file chooser employed when opening and saving source files.
     *
     * @return The file chooser
     */
    FileChooser createSourceFileChooser();

    /**
     * Creates the code editor for source files.
     *
     * @return The code editor
     */
    CodeArea createCodeEditor();

    /**
     * Computes the actual file after the user has confirmed a file when saving the source code.
     * For example, the function can add a default file extension if the user did not.
     *
     * @param sourceFileChooser The file chooser used to save the file
     * @param selectedFile      The file originally saved by the user
     * @return The actual target file
     */
    File getSavedSourceFile(FileChooser sourceFileChooser, File selectedFile);

    /**
     * Tells the IDE whether to show the "Settings" menu item
     *
     * @return true if the "Settings" menu item should be shown
     */
    boolean settingsSupported();

    /**
     * Shows a settings dialog. Can do nothing if settingsSupported() returns false
     */
    void showSettings();


    /**
     * Retrieves the URL of the CSS file employed to style the code editor
     *
     * @return The CSS url
     */
    URL getSyntaxCss();

    /**
     * Shows online reference (for example, opens a web page)
     */
    void showOnlineReference();

    /**
     * Shows the "About..." window
     */
    void showAboutWindow();
}
