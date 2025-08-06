package app.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class modelStockShootNotFound {
    private final StringProperty barcode;
    public modelStockShootNotFound(String barcode){
        this.barcode=new SimpleStringProperty(barcode);
    }

    public StringProperty barcode(){
        return barcode;
    }
}