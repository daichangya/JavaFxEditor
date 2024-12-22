package com.daicy.javafxeditor.desktop;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Desktop utilities
 */
public class DesktopUtils {

    /**
     * Functional interface for handling exceptions
     */
    @FunctionalInterface
    public interface ExceptionCallback {
        void handle(Exception ex);
    }

    private static final ExceptionCallback EMPTY_EXCEPTION_CALLBACK = ex -> {};

    private static void runInThread(Runnable action, ExceptionCallback exceptionCallback) {
        Thread externalThread = new Thread(() -> {
            try {
                Desktop desktop = Desktop.getDesktop();

                if (desktop == null) {
                    throw new UnsupportedOperationException("Desktop not available");
                }

                action.run();
            } catch (Exception ex) {
                exceptionCallback.handle(ex);
            }
        });

        externalThread.start();
    }

    /**
     * Opens the given URL in a browser, without freezing the app.
     *
     * Throws an exception in case of errors.
     *
     * @param url               The url to open
     * @param exceptionCallback Callback invoked in case of exception
     */
    public static void openBrowser(String url, ExceptionCallback exceptionCallback) {
        runInThread(() -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        }, exceptionCallback != null ? exceptionCallback : EMPTY_EXCEPTION_CALLBACK);
    }

    /**
     * Opens the given file using the user's desktop environment settings, without freezing the app.
     *
     * Throws an exception in case of errors.
     *
     * @param file              The file to open
     * @param exceptionCallback Callback invoked in case of exception
     */
    public static void openFile(File file, ExceptionCallback exceptionCallback) {
        runInThread(() -> {
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, exceptionCallback != null ? exceptionCallback : EMPTY_EXCEPTION_CALLBACK);
    }

    /**
     * Returns the user's home directory, if available
     *
     * @return Optional containing the user home directory or empty if not available
     */
    public static Optional<File> homeDirectory() {
        String userHomeProperty = System.getProperty("user.home");

        if (userHomeProperty == null) {
            return Optional.empty();
        } else {
            return Optional.of(new File(userHomeProperty));
        }
    }
}
