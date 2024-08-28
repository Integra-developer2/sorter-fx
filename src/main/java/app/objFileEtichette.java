package app;

public class objFileEtichette {
    public Integer row;
    public String firstBarcode;
    public String lastBarcode;
    public String reference;
    public String obs;
    public String group;
    public Integer progStart;
    public Integer progEnd;
    public objFileEtichette(Integer row, String firstBarcode,String lastBarcode,String reference,String obs){
        this.row = row;
        this.firstBarcode=firstBarcode;
        this.lastBarcode=lastBarcode;
        this.reference=reference;
        this.obs=obs;
        this.group="";
        this.progStart=0;
        this.progEnd=0;
    }

    public objFileEtichette(Integer row, String firstBarcode,String lastBarcode,String reference,String obs,String group, Integer progStart, Integer progEnd){
        this.row = row;
        this.firstBarcode=firstBarcode;
        this.lastBarcode=lastBarcode;
        this.reference=reference;
        this.obs=obs;
        this.group=group;
        this.progStart=progStart;
        this.progEnd=progEnd;
    }

    public void extra(String group, Integer progStart, Integer progEnd){
        this.group = group;
        this.progStart = progStart;
        this.progEnd = progEnd;
    }
}
