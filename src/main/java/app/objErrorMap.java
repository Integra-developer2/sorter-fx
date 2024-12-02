package app;

import java.util.ArrayList;
import java.util.HashMap;

import app.o2_sorter_gray.functions;

public class objErrorMap extends functions{
    public HashMap<String,objError> map;
    public ArrayList<String> success = new ArrayList<>();
    public objErrorMap(){
        map = new HashMap<>();
        map.put("BBNE", new objError(objGlobals.anomalyFolderGray,true));//Black barcode not equal
        map.put("CGNF", new objError(objGlobals.anomalyFolderGray, false));//Couple file not found
        map.put("ISMT", new objError(objGlobals.anomalyFolderGray, false));//Is Multi
        map.put("GFNF", new objError(objGlobals.anomalyFolderGray, true));//Gray file not found
        map.put("FSEC", new objError(objGlobals.anomalyFolderGray, true));//File is error or corrupted
        map.put("ISDB", new objError(objGlobals.anomalyLog, true));//Many files, same barcode
        map.put("PDFE", new objError(objGlobals.anomalyLog, true));//Error generating pdf
        map.put("NONP", new objError(objGlobals.notExpectedFolder, true));//Not expected by file etichette
        map.put("CBNF", new objError(objGlobals.anomalyLog, true));//Coupe black not found
        map.put("TMPS", new objError(objGlobals.anomalyFolderStock2, true));//Temporary stock error
        success.add("FOUND");
        success.add("FOUND_ACTCG");
    }
    public objError get(String code){
        return map.get(code);
    }
}