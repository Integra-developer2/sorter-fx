package app.o3_sorter_stock;
import java.util.HashMap;
public class objJobSorter {
    public static String[] titles;
    public static HashMap<String, String[]> rows = new HashMap<>();
    public static void add(String key, String[] value){
        rows.put(key, value);
    }

    public static void addTitles(String[] value){
        titles = value;
    }

    public static void clear(){
        rows.clear();
    }
}
