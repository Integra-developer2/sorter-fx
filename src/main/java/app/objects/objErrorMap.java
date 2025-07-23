package app.objects;

import java.util.ArrayList;
import java.util.HashMap;

public class objErrorMap {
    public HashMap<String,objError> map;
    public ArrayList<String> success = new ArrayList<>();
    public objErrorMap(){
        map = new HashMap<>();
        map.put("notInJobSorter", new objError(objGlobals.anomalyFolderGray, false));
        map.put("foundNothing", new objError(objGlobals.anomalyFolderGray, false));
        map.put("multipleFoundsInJobSorter", new objError(objGlobals.anomalyFolderGray, false));

        map.put("foundNotEqual", new objError(objGlobals.anomalyFolderGray,true));
        map.put("grayFileNotFound", new objError(objGlobals.anomalyFolderGray, true));

        map.put("coupleNotFound", new objError(objGlobals.logAnomalyFolderBlack, true));
        map.put("isMultiple", new objError(objGlobals.logAnomalyFolderBlack, true));

        success.add("foundAndEqual");
        success.add("foundActUnclaimed");
    }
    public objError get(String code){
        return map.get(code);
    }
}