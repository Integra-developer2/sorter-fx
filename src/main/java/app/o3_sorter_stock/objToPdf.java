package app.o3_sorter_stock;

import java.util.HashMap;

public class objToPdf {
    public static HashMap<String, String> list = new HashMap<>();
    public static void add(String from, String to){
        if(!list.containsKey(from)){
            list.put(from, to);
        }
    }

    public static void clear(){
        list.clear();
    }
}
