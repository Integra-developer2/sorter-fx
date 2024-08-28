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
    opens app to javafx.fxml;
    exports app;
}
