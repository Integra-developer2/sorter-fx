package app.o3_sorter_stock;

import java.util.concurrent.ConcurrentHashMap;

public class objToPdf {
    public static ConcurrentHashMap<String, String> list = new ConcurrentHashMap<>();
    public static void add(String from, String to){
        if(!list.contains(from)){
            list.put(from, to);
        }
    }

    public static void clear(){
        list.clear();
    }
}
