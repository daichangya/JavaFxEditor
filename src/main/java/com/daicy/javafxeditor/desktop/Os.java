package com.daicy.javafxeditor.desktop;

/**
 * Provides information about the operating system
 */
public class Os {
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    private static Boolean isWindows = null;
    private static Boolean isLinux = null;
    private static Boolean isMac = null;

    public static boolean isWindows() {
        if (isWindows == null) {
            isWindows = OS_NAME.contains("windows");
        }
        return isWindows;
    }

    public static boolean isLinux() {
        if (isLinux == null) {
            isLinux = OS_NAME.contains("linux");
        }
        return isLinux;
    }

    public static boolean isMac() {
        if (isMac == null) {
            isMac = OS_NAME.contains("mac");
        }
        return isMac;
    }
}
