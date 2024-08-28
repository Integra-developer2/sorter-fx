package app;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class modelEtichette2 {
    private final String group;
    private final Integer indexStart;
    private final Integer indexEnd;
    private final String firstBarcode;
    private final String lastBarcode;
    private final Integer row1;
    private final Integer id;
    private final StringProperty box1;
    private final StringProperty pallet1;
    private final Integer row2;
    private final StringProperty box2;
    private final StringProperty pallet2;
    private final ArrayList<String>barcodeList;
    private final ArrayList<String>fileList;

    public modelEtichette2(Integer id,objNotExpected obj) {
        this.group=obj.group;
        this.id = id;
        this.row1=obj.row1;
        this.box1 = new SimpleStringProperty(obj.box1);
        this.pallet1 = new SimpleStringProperty(obj.pallet1);
        this.row2=obj.row2;
        this.box2 = new SimpleStringProperty(obj.box2);
        this.pallet2 = new SimpleStringProperty(obj.pallet2);
        this.barcodeList = obj.barcodeList;
        this.fileList = obj.fileList;
        this.indexStart=obj.indexStart;
        this.indexEnd=obj.indexEnd;
        this.firstBarcode=obj.firstBarcode;
        this.lastBarcode=obj.lastBarcode;
    }
    @SuppressWarnings("exports")
    public StringProperty id() { return new SimpleStringProperty(String.valueOf(id));}
    @SuppressWarnings("exports")
    public StringProperty box1() { return box1;}
    @SuppressWarnings("exports")
    public StringProperty pallet1() { return pallet1;}
    @SuppressWarnings("exports")
    public StringProperty box2() { return box2;}
    @SuppressWarnings("exports")
    public StringProperty pallet2() { return pallet2;}
    public ArrayList<String> barcodeList() { return barcodeList;}
    public Integer row1(){ return row1;}
    public Integer row2(){ return row2;}
    public Integer intId(){ return id;}
    public ArrayList<String>fileList(){ return fileList;}
    public Integer indexStart(){ return indexStart;}
    public Integer indexEnd(){ return indexEnd;}
    public String firstBarcode(){ return firstBarcode;}
    public String lastBarcode(){ return lastBarcode;}
    public String group(){ return group;}
}
