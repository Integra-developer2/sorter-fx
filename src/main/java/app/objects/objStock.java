package app.objects;

public class objStock {
    public Integer row;
    public String firstBarcode;
    public String lastBarcode;
    public String stockLabel;
    public String obs;
    public String group;
    public String progStart;
    public String progEnd;
    public String logic;
    public String prefix;
    public String stockNumber;
    public String agency;
    public objStock(Integer row, String firstBarcode, String lastBarcode, String stockLabel, String obs){
        this.row = row;
        this.firstBarcode=firstBarcode;
        this.lastBarcode=lastBarcode;
        this.stockLabel = stockLabel;
        this.obs=obs;
        this.group="";
        this.progStart="";
        this.progEnd="";
    }

    public objStock(Integer row, String firstBarcode, String lastBarcode, String stockLabel, String obs, String group, String progStart, String progEnd, String logicStart, String prefixStart, String stockNumber, String agency){
        this.row = row;
        this.firstBarcode=firstBarcode;
        this.lastBarcode=lastBarcode;
        this.stockLabel = stockLabel;
        this.obs=obs;
        this.group=group;
        this.progStart=progStart;
        this.progEnd=progEnd;
        this.logic =logicStart;
        this.prefix =prefixStart;
        this.stockNumber =stockNumber;
        this.agency=agency;
    }

    public void extraFromJobSorter(String group,String progStart, String progEnd){
        this.group = group;
        this.progStart = progStart;
        this.progEnd = progEnd;
    }

    public void extraFromStockFile(String logic,String prefix, String stockNumber, String agency){
        this.logic = logic;
        this.prefix = prefix;
        this.stockNumber = stockNumber;
        this.agency = agency;
    }

}
