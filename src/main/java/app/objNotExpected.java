package app;

import java.util.ArrayList;

public class objNotExpected {
    String group;
    Integer indexStart;
    Integer indexEnd;
    String firstBarcode;
    String lastBarcode;
    Integer row1;
    String box1;
    String pallet1;
    Integer row2;
    String box2;
    String pallet2;
    ArrayList<String>barcodeList;
    ArrayList<String>fileList;
    public objNotExpected(String group,Integer row1, String box1, String pallet1, Integer row2, String box2, String pallet2,Integer indexStart, Integer indexEnd, String firstBarcode, String lastBarcode){
        this.group=group;
        this.row1=row1;
        this.box1=box1;
        this.pallet1=pallet1;
        this.row2=row2;
        this.box2=box2;
        this.pallet2=pallet2;
        barcodeList=new ArrayList<>();
        fileList=new ArrayList<>();
        this.indexStart=indexStart;
        this.indexEnd=indexEnd;
        this.firstBarcode=firstBarcode;
        this.lastBarcode=lastBarcode;
    }
}
