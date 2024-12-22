package com.daicy.javafxeditor.apps;

import java.io.InputStream;
import javafx.scene.image.Image;

/**
 * Information about a generic application
 */
public interface AppInfo {
    String getName();

    Version getVersion();

    String getTitle();

    String getCopyrightYears();

    String getCopyrightHolder();

    String getWebsite();

    String getAuthorPage();

    InputStream getMainIcon();

    default Image getMainIconImage() {
        return new Image(getMainIcon());
    }
}
