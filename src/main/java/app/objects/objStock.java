package app.objects;

public class objStock {
    public Integer row;
    public String firstBarcode;
    public String lastBarcode;
    public String stockLabel;
    public String obs;
    public String cassetto;
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
    public objStock(Integer row, String firstBarcode, String lastBarcode, String stockLabel, String obs, String cassetto){
        this.row = row;
        this.firstBarcode=firstBarcode;
        this.lastBarcode=lastBarcode;
        this.stockLabel = stockLabel;
        this.obs=obs;
        this.cassetto=cassetto;
        this.group="";
        this.progStart="";
        this.progEnd="";
    }

    public objStock(Integer row, String firstBarcode, String lastBarcode, String stockLabel, String obs, String cassetto, String group, String progStart, String progEnd, String logicStart, String prefixStart, String stockNumber, String agency, String agencyID, String cppCode, String customer){
        this.row = row;
        this.firstBarcode=firstBarcode;
        this.lastBarcode=lastBarcode;
        this.stockLabel = stockLabel;
        this.obs=obs;
        this.cassetto=cassetto;
        this.group=group;
        this.logic =logicStart;
        this.prefix =prefixStart;
        this.stockNumber =stockNumber;
        this.agency=agency;
        this.agencyID=agencyID;
        this.cppCode=cppCode;
        this.customer=customer;

        this.firstAndEnd(progStart,progEnd);
    }

    public void extraFromJobSorter(String group,String progStart, String progEnd){
        this.group = group;
        this.firstAndEnd(progStart,progEnd);
    }

    public void extraFromStockFile(String logic,String prefix, String stockNumber, String agency, String agencyID, String cppCode, String customer){
        this.logic = logic;
        this.prefix = prefix;
        this.stockNumber = stockNumber;
        this.agency = agency;
        this.agencyID = agencyID;
        this.cppCode = cppCode;
        this.customer = customer;
    }

    private void firstAndEnd(String progStart, String progEnd){
        if(!progStart.isEmpty() && !progEnd.isEmpty()){
            String firstBarcode = this.firstBarcode;
            String lastBarcode = this.lastBarcode;
            int start = Integer.parseInt(progStart);
            int end = Integer.parseInt(progEnd);
            if(end<start){
                this.firstBarcode=lastBarcode;
                this.lastBarcode=firstBarcode;
                this.progStart=progEnd;
                this.progEnd=progStart;
            }
            else{
                this.progStart=progStart;
                this.progEnd=progEnd;
            }
        }
    }

}
