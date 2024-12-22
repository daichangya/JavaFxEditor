package com.daicy.javafxeditor;

public class Style {
    private final String cssClass;
    private final String pattern;

    public Style(String cssClass, String pattern) {
        this.cssClass = cssClass;
        this.pattern = pattern;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Style style = (Style) o;

        if (!cssClass.equals(style.cssClass)) return false;
        return pattern.equals(style.pattern);
    }

    @Override
    public int hashCode() {
        int result = cssClass.hashCode();
        result = 31 * result + pattern.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Style{" +
                "cssClass='" + cssClass + '\'' +
                ", pattern='" + pattern + '\'' +
                '}';
    }
}
