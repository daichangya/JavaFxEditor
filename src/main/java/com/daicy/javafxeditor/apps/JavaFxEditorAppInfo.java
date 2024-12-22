package com.daicy.javafxeditor.apps;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;


public class JavaFxEditorAppInfo implements AppInfo {

    @Override
    public String getName() {
        return "JavaFxEditor";
    }

    @Override
    public Version getVersion() {
        return Version.parse("1.0.0-SNAPSHOT");
    }

    @Override
    public String getTitle() {
        return "JavaFxEditor";
    }

    @Override
    public String getCopyrightYears() {
        return String.valueOf(LocalDate.now().getYear());
    }

    @Override
    public String getCopyrightHolder() {
        return "代长亚";
    }


    @Override
    public String getWebsite() {
        return "";
    }

    @Override
    public String getAuthorPage() {
        return "https://zthinker.com/";
    }


    @Override
    public InputStream getMainIcon() {
        // 从类路径中读取图片资源
        InputStream inputStream = getClass().getResourceAsStream("zthinker.png");
        if (inputStream == null) {
            System.err.println("图片资源未找到: zthinker.png");
        }
        return inputStream;
    }
}
