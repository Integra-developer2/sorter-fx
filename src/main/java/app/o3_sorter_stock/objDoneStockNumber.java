package app.o3_sorter_stock;

import java.util.ArrayList;

public class objDoneStockNumber {
    public static ArrayList<String> list = new ArrayList<>();
    public static ArrayList<String> isdb = new ArrayList<>();

    public static void add(String barcode){
        list.add(barcode);
    }

    public static void isdb(String file){
        isdb.add(file+"-FRONTE.tiff");
        isdb.add(file+"-RETRO.tiff");
    }

    public static void clear(){
        list.clear();
        isdb.clear();
    }
}
