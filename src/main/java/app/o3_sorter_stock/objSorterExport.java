package app.o3_sorter_stock;

import java.util.ArrayList;
import java.util.HashMap;

public class objSorterExport {
    public static HashMap<String, HashMap<String, ArrayList<String[]>>> list = new HashMap<>();
    public static void add(String extraFolders, String key, String[] value){
        extraFolders=folders(extraFolders);
        if(list.containsKey(extraFolders)){
            HashMap<String, ArrayList<String[]>> innerMap = list.get(extraFolders);
            if(innerMap.containsKey(key)){
                innerMap.get(key).add(value);
            }
            else{
                ArrayList<String[]> newValue = new ArrayList<>();
                newValue.add(value);
                innerMap.put(key, newValue);
            }
        }
        else{
            ArrayList<String[]> newValue = new ArrayList<>();
            newValue.add(value);
            HashMap<String, ArrayList<String[]>> newHash = new HashMap<>();
            newHash.put(key, newValue);
            list.put(extraFolders, newHash);
        }
    }

    public static String folders(String extraFolder){
        String[] folders = extraFolder.split("\\\\");
        String ret = "";
        int size = folders.length;
        for(int i=0;i<size;i++){
            if(i!=0&&i!=(size-1)){
                if(ret.isEmpty()){
                    ret=folders[i].replace(" ", "");
                }else{
                    ret+="_"+folders[i];
                }
            }
        }
        if(ret.isEmpty()){
            ret="unico";
        }
        return ret;
    }

    public static void clear(){
        list.clear();
    }
}
