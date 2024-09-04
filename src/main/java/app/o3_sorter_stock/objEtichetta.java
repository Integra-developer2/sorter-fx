package app.o3_sorter_stock;

import java.util.HashMap;
import java.util.TreeMap;

public class objEtichetta {
    public static HashMap<String, TreeMap<Integer,Integer>> list = new HashMap<>();
    public static HashMap<String, TreeMap<Integer,String>> boxNote = new HashMap<>();

    public static void add(String group, Integer indexFrom, Integer indexTo, String note){
        list.computeIfAbsent(group, k -> new TreeMap<>()).computeIfAbsent(indexFrom, k -> indexTo);
        boxNote.computeIfAbsent(group, k -> new TreeMap<>()).computeIfAbsent(indexFrom, k -> note);
    }

    public static void clear(){
        list.clear();
        boxNote.clear();
    }

}
