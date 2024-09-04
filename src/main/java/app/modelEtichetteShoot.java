package app;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class modelEtichetteShoot {
    private final StringProperty barcode;
    public modelEtichetteShoot(String barcode){
        this.barcode=new SimpleStringProperty(barcode);
    }
    @SuppressWarnings("exports")
    public StringProperty barcode(){return barcode;}
}