package app.objects;

public class objStockNumber {
    public Integer row;
    public String firstBarcode;
    public String lastBarcode;
    public String reference;
    public String obs;
    public String group;
    public String progStart;
    public String progEnd;
    public String logic;
    public String prefix;
    public String stockNumber;
    public objStockNumber(Integer row, String firstBarcode, String lastBarcode, String reference, String obs,String logicStart,String prefixStart,String stockNumber){
        this.row = row;
        this.firstBarcode=firstBarcode;
        this.lastBarcode=lastBarcode;
        this.reference=reference;
        this.obs=obs;
        this.group="";
        this.progStart="";
        this.progEnd="";
        this.logic =logicStart;
        this.prefix =prefixStart;
        this.stockNumber =stockNumber;
    }

}
