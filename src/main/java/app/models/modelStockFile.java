package app.models;

import app.objects.objStock;

public class modelStockFile {
    public Integer row;
    public String firstBarcode;
    public String lastBarcode;
    public String stockLabel;
    public String obs;
    public String cassetto;
    public String pacco;
    public String group;
    public String progStart;
    public String progEnd;
    public String logic;
    public String prefix;
    public String stockNumber;
    public String agency;
    public String agencyID;
    public String cppCode;
    public String customer;

    public modelStockFile(objStock objStock){
        this.row = objStock.row;
        this.firstBarcode=objStock.firstBarcode;
        this.lastBarcode=objStock.lastBarcode;
        this.stockLabel = objStock.stockLabel;
        this.obs=objStock.obs;
        this.cassetto=objStock.cassetto;
        this.pacco=objStock.pacco;
        this.group=objStock.group;
        this.logic =objStock.logic;
        this.prefix =objStock.prefix;
        this.stockNumber =objStock.stockNumber;
        this.agency=objStock.agency;
        this.agencyID=objStock.agencyID;
        this.cppCode=objStock.cppCode;
        this.customer=objStock.customer;
        this.progStart=objStock.progStart;
        this.progEnd=objStock.progEnd;
    }
}
