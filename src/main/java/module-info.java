module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;
    requires org.apache.pdfbox;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires com.opencsv;
    requires javafx.swing;
    requires com.twelvemonkeys.imageio.tiff;
    requires javafx.graphics;
    requires java.sql;
    requires jdk.management;
    requires com.github.oshi;
    opens app to javafx.fxml;
    exports app;
    exports app.views;
    opens app.views to javafx.fxml;
    exports app.objects;
    opens app.objects to javafx.fxml;
    exports app.models;
    opens app.models to javafx.fxml;
    exports app.classes;
    opens app.classes to javafx.fxml;
}