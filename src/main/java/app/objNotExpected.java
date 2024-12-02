package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import static app.functions.mkdir;
import static app.functions.printError;

public class objNotExpected implements Serializable{
    private static final long serialVersionUID = 1L;
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
    @Override
    public String toString() {
        return "objNotExpected{" +
            "group='" + group + '\'' +
            ", indexStart=" + indexStart +
            ", indexEnd=" + indexEnd +
            ", firstBarcode='" + firstBarcode + '\'' +
            ", lastBarcode='" + lastBarcode + '\'' +
            ", row1=" + row1 +
            ", box1='" + box1 + '\'' +
            ", pallet1='" + pallet1 + '\'' +
            ", row2=" + row2 +
            ", box2='" + box2 + '\'' +
            ", pallet2='" + pallet2 + '\'' +
            ", barcodeList=" + barcodeList +
            ", fileList=" + fileList +
        '}';
    }

    public void save(Integer id) {
        File to = new File(objGlobals.objNotExpectedFolder,id.toString());
        mkdir(to.toString());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(to))) {
            oos.writeObject(this);
        } catch (IOException e) {
            printError(e, true);
        }
    }

}
