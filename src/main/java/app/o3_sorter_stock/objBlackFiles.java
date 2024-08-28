package app.o3_sorter_stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static app.functions.logError;

public class objBlackFiles extends functions{
    public static final ArrayList<String> paths = new ArrayList<>();
    public static final ConcurrentHashMap<String,String> barcodePathMin = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String,String> barcodePathMax = new ConcurrentHashMap<>();
    public static final HashMap<String,String> errors = new HashMap<>();
    public static final HashMap<String,HashMap<Integer,String>> groupIndex = new HashMap<>();

    public static void add(String blackFile){
        if(!paths.contains(blackFile)){
            paths.add(blackFile);
        }
    }

    public static void remove(String file){
        paths.remove(file);
    }

    public static void addBarcodePath(String blackFile){
        String barcode = getBarcodeFromBlackFile(blackFile);
        if(barcodePathMin.containsKey(barcode)||barcodePathMax.containsKey(barcode)){
            String pathMin = barcodePathMin.get(barcode);
            String pathMax = barcodePathMax.get(barcode);
            int newIndex = Integer.parseInt(getIndexFromBlackFile(blackFile).replace("-", ""));
            int minIndex = Integer.parseInt(getIndexFromBlackFile(pathMin).replace("-", ""));
            int maxIndex = Integer.parseInt(getIndexFromBlackFile(pathMax).replace("-", ""));
            if(newIndex < minIndex){
                barcodePathMin.put(barcode, blackFile);
            }
            else if (newIndex > maxIndex) {
                barcodePathMax.put(barcode, blackFile);
            }
        }
        else{
            barcodePathMin.put(barcode, blackFile);
            barcodePathMax.put(barcode, blackFile);
        }
        try {
            String group = getGroup(barcode);
            int index = Integer.parseInt(getIndexFromBlackFile(blackFile).replace("-", ""));
            if(groupIndex.containsKey(group)){
                groupIndex.get(group).put(index, blackFile);
            }
            else{
                HashMap<Integer,String> newParams = new HashMap<>();
                newParams.put(index,blackFile);
                groupIndex.put(group,newParams);
            }
        } catch (NumberFormatException e) {
            logError("objBlackFiles index barcode:"+barcode, e);
        }
    }

    public static void clear(){
        paths.clear();
        barcodePathMin.clear();
        barcodePathMax.clear();
        errors.clear();
        groupIndex.clear();
    }
}
