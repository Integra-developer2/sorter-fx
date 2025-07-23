package app.objects;

import java.util.ArrayList;

public class objStockFile {
    public String error;
    public ArrayList<String> barcodes;
    public int indexTo;
    public int indexFrom;
    public String groupFrom;
    public String groupTo;
    public objStock obj;
    public Integer row;
    public objStockFile(String error, ArrayList<String> barcodes, int indexFrom, int indexTo, String groupFrom, String groupTo, objStock obj, Integer row) {
        this.error = error;
        this.barcodes = barcodes;
        this.indexTo = indexTo;
        this.indexFrom = indexFrom;
        this.groupFrom = groupFrom;
        this.groupTo = groupTo;
        this.obj = obj;
        this.row = row;
    }
}
