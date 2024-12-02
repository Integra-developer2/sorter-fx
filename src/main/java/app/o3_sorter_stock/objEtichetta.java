package app.o3_sorter_stock;

import java.util.HashMap;
import java.util.TreeMap;

public class objEtichetta {
    public static HashMap<String, TreeMap<Integer,Integer>> list = new HashMap<>();
    public static HashMap<String, String> entityList = new HashMap<>();
    public static HashMap<String, String> boxNote = new HashMap<>();

    public static void add(String group, String entity, String note, Integer indexFrom, Integer indexTo){
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
        boxNote.put(group,note);
        entityList.put(group, entity);
    }

    public static void clear(){
        list.clear();
    }

}
