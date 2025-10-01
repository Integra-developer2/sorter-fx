package app.models;

import app.objects.objStock;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@SuppressWarnings("unused")
public class modelStockFile {
    public StringProperty row;
    public StringProperty firstBarcode;
    public StringProperty lastBarcode;
    public StringProperty stockLabel;
    public StringProperty obs;
    public StringProperty cassetto;
    public StringProperty pacco;
    public StringProperty group;
    public StringProperty progStart;
    public StringProperty progEnd;
    public StringProperty logic;
    public StringProperty prefix;
    public StringProperty stockNumber;
    public StringProperty agency;
    public StringProperty agencyID;
    public StringProperty cppCode;
    public StringProperty customer;
    public StringProperty error;

    public modelStockFile(objStock objStock,String error){
        this.row=new SimpleStringProperty(String.valueOf(objStock.row));
        this.firstBarcode=new SimpleStringProperty(objStock.firstBarcode);
        this.lastBarcode=new SimpleStringProperty(objStock.lastBarcode);
        this.stockLabel = new SimpleStringProperty(objStock.stockLabel);
        this.obs=new SimpleStringProperty(objStock.obs);
        this.cassetto=new SimpleStringProperty(objStock.cassetto);
        this.pacco=new SimpleStringProperty(objStock.pacco);
        this.group=new SimpleStringProperty(objStock.group);
        this.logic =new SimpleStringProperty(objStock.logic);
        this.prefix =new SimpleStringProperty(objStock.prefix);
        this.stockNumber =new SimpleStringProperty(objStock.stockNumber);
        this.agency=new SimpleStringProperty(objStock.agency);
        this.agencyID=new SimpleStringProperty(objStock.agencyID);
        this.cppCode=new SimpleStringProperty(objStock.cppCode);
        this.customer=new SimpleStringProperty(objStock.customer);
        this.progStart=new SimpleStringProperty(objStock.progStart);
        this.progEnd=new SimpleStringProperty(objStock.progEnd);
        this.error=new SimpleStringProperty(error);
    }

    public StringProperty row() { return this.row;}
    public StringProperty firstBarcode() { return this.firstBarcode;}
    public StringProperty lastBarcode() { return this.lastBarcode;}
    public StringProperty stockLabel() { return this.stockLabel;}
    public StringProperty obs() { return this.obs;}
    public StringProperty cassetto() { return this.cassetto;}
    public StringProperty pacco() { return this.pacco;}
    public StringProperty group() { return this.group;}
    public StringProperty logic () { return this.logic;}
    public StringProperty prefix() { return this.prefix;}
    public StringProperty stockNumber() { return this.stockNumber;}
    public StringProperty agency() { return this.agency;}
    public StringProperty agencyID() { return this.agencyID;}
    public StringProperty cppCode() { return this.cppCode;}
    public StringProperty customer() { return this.customer;}
    public StringProperty progStart() { return this.progStart;}
    public StringProperty progEnd() { return this.progEnd;}
    public StringProperty error() { return this.error;}

}
