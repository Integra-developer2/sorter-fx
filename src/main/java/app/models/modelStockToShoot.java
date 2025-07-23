package app.models;

import java.util.List;

import app.objects.objStockToShoot;
import app.objects.objValidTiff;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class modelStockToShoot {
    private final String group;
    private final Integer row1;
    private final StringProperty box1;
    private final StringProperty pallet1;
    private final Integer row2;
    private final StringProperty box2;
    private final StringProperty pallet2;
    private final List<objValidTiff> objValidTiff;

    public modelStockToShoot(objStockToShoot objStockToShoot) {
        this.group = objStockToShoot.group;
        this.row1 = objStockToShoot.row1;
        this.box1 = new SimpleStringProperty(objStockToShoot.box1);
        this.pallet1 =  new SimpleStringProperty(objStockToShoot.pallet1);
        this.row2 = objStockToShoot.row2;
        this.box2 =  new SimpleStringProperty(objStockToShoot.box2);
        this.pallet2 =  new SimpleStringProperty(objStockToShoot.pallet2);
        this.objValidTiff = objStockToShoot.objValidTiff;
    }

    public StringProperty box1() { return box1;}
    public StringProperty pallet1() { return pallet1;}
    public StringProperty box2() { return box2;}
    public StringProperty pallet2() { return pallet2;}
    public List<objValidTiff> objValidTiff() { return objValidTiff;}
    public Integer row1(){ return row1;}
    public Integer row2(){ return row2;}
    public String group(){ return group;}
}
