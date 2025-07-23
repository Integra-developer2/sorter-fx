package app.models;

import app.objects.objValidTiff;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class modelStockShooting {
    private final objValidTiff objValidTiff;
    public modelStockShooting(objValidTiff objValidTiff){
        this.objValidTiff=objValidTiff;
    }
    public StringProperty barcode(){
        return new SimpleStringProperty(objValidTiff.barcode);
    }
}