package com.daicy.javafxeditor;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.stream.Collectors;

public class Alerts {

    private static void showAlert(Alert.AlertType alertType, String message, String header) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.setResizable(true);

        fix(alert);

        alert.showAndWait();
    }

    /**
     * Fixes dialogs layout on some Linux window systems
     *
     * @param alert The alert to be fixed
     */
    public static void fix(Alert alert) {
        List<Node> labels = alert.getDialogPane().getChildren().stream()
                .filter(node -> node instanceof Label)
                .collect(Collectors.toList());

        for (Node node : labels) {
            ((Label) node).setMinHeight(Region.USE_PREF_SIZE);
        }
    }

    /**
     * Shows an information box
     *
     * @param message
     * @param header
     */
    public static void showInfo(String message, String header) {
        showAlert(Alert.AlertType.INFORMATION, message, header);
    }

    /**
     * Shows a warning box
     *
     * @param message
     * @param header
     */
    public static void showWarning(String message, String header) {
        showAlert(Alert.AlertType.WARNING, message, header);
    }

    /**
     * Shows an error box
     *
     * @param message
     * @param header
     */
    public static void showError(String message, String header) {
        showAlert(Alert.AlertType.ERROR, message, header);
    }

    /**
     * Shows an exception box. More precisely, its message is:
     * <ul>
     * <li>The exception's <i>localized message</i>, if it is not null or empty</li>
     * <li>The simple name of the exception's class otherwise</li>
     * </ul>
     *
     * Furthermore, if its alertType is set to <i>Error</i> (the default),
     * the exception's stack trace is printed out to stderr.
     *
     * @param exception
     * @param header
     * @param alertType
     */
    public static void showException(Exception exception, String header, Alert.AlertType alertType) {
        if (alertType == Alert.AlertType.ERROR) {
            exception.printStackTrace(System.err);
        }

        String alertText;
        if (exception.getLocalizedMessage() != null && !exception.getLocalizedMessage().isEmpty()) {
            alertText = exception.getLocalizedMessage();
        } else {
            alertText = exception.getClass().getSimpleName();
        }

        showAlert(alertType, alertText, header);
    }

    // Overloaded method without header parameter
    public static void showException(Exception exception, Alert.AlertType alertType) {
        showException(exception, "", alertType);
    }

    // Overloaded method without header and alertType parameters
    public static void showException(Exception exception) {
        showException(exception, Alert.AlertType.NONE);
    }
}
