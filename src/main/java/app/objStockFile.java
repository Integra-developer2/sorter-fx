package app;

public class objStockFile {
    public String ID;
    public String AGENZIA;
    public String CLIENTE;
    public String PREFISSO;
    public String NUMERO;
    public Integer stockNumber;
    public Integer countStock;

    public objStockFile(String id, String agenzia, String cliente, String prefisso, String numero){
        ID=id;
        AGENZIA=agenzia;
        CLIENTE=cliente;
        PREFISSO=prefisso;
        NUMERO=numero;
        stockNumber=Integer.valueOf(numero);
        countStock=1;
    }

}
