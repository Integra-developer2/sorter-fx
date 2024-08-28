package app.o3_sorter_stock;

import java.util.HashMap;
import java.util.TreeMap;

public class objEtichetta {
    public static HashMap<String, TreeMap<Integer,Integer>> list = new HashMap<>();

    public static void add(String group, Integer indexFrom, Integer indexTo){
        if(list.containsKey(group)){
            if(!list.get(group).containsKey(indexFrom)){
                list.get(group).put(indexFrom, indexTo);
            }
        }
        else{
            TreeMap<Integer, Integer> newParams = new TreeMap<>();
            newParams.put(indexFrom, indexTo);
            list.put(group, newParams);
        }
    }

    public static void clear(){
        list.clear();
    }

}
