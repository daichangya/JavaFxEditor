module com.daicy.javafxidea {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires org.fxmisc.richtext;
    requires org.fxmisc.undo;
    requires reactfx;

    opens com.daicy.javafxeditor to javafx.fxml;
    opens com.daicy.javafxeditor.apps to javafx.fxml;
    opens com.daicy.javafxeditor.about to javafx.fxml;

    exports com.daicy.javafxeditor;
    exports com.daicy.javafxeditor.apps;
    exports com.daicy.javafxeditor.about;
}