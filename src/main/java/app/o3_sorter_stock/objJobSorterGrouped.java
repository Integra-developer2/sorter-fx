package app.o3_sorter_stock;

import java.util.HashMap;

public class objJobSorterGrouped {
    public static HashMap<String, HashMap<String, String[]>> list = new HashMap<>();
    public static HashMap<String, String> alternative = new HashMap<>();
    public static HashMap<String, String> parent = new HashMap<>();

    public static void add(String group,String barcode, String[] row){
        if(list.containsKey(group)){
            list.get(group).put(barcode, row);
        }
        else{
            HashMap<String, String[]> _row = new HashMap<>();
            _row.put(barcode, row);
            list.put(group, _row);
        }
    }

    public static void alternative(String a, String b){
        alternative.put(a, b);
        parent.put(b,a);
    }

    public static String getAlternative(String barcode){
        String ret = "";
        if(alternative.containsKey(barcode)){
            ret = alternative.get(barcode);
        }else if(parent.containsKey(barcode)){
            ret = parent.get(barcode);
        }
        return ret;
    }

    public static void clear(){
        list.clear();
        alternative.clear();
        parent.clear();
    }
}
