package app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static app.functions.mkdir;
import static app.functions.printError;

public class ctrlGrayAnomalieObj {
    public String filename;
    public String filenameLog;
    public String filenameTarget;
    public String currentlyAt;
    public File fileFront;
    public File fileBack;
    public ctrlGrayAnomalieObj(String filename){
        this.filename = filename;
        currentlyAt="anomaly";
        filenameTarget=filename;
        for (String code : objGlobals.errorMap.map.keySet()) {
            objError error = objGlobals.errorMap.map.get(code);
            filenameTarget=filenameTarget.replace(error.path, objGlobals.targetTiff);
        }
        filenameLog=filename.replace(objGlobals.anomalyFolderGray, objGlobals.anomalyLogFolder);
        refresh();
    }
    public void move(String moveTo){
        String from="";
        String to="";
        switch (currentlyAt) {
            case "anomaly" -> from=filename;
            case "target" -> from=filenameTarget;
            case "log" -> from=filenameLog;
        }
        switch (moveTo) {
            case "target" -> to=filenameTarget;
            case "log" -> to=filenameLog;
        }
        mkdir(to);
        File fileFrontFrom = new File(from+"-FRONTE.tiff");
        File fileBackFrom = new File(from+"-RETRO.tiff");
        File fileFrontTo = new File(to+"-FRONTE.tiff");
        File fileBackTo = new File(to+"-RETRO.tiff");
        try {
            Files.move(fileFrontFrom.toPath(), fileFrontTo.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.move(fileBackFrom.toPath(), fileBackTo.toPath(), StandardCopyOption.REPLACE_EXISTING);
            currentlyAt=moveTo;
            refresh();
        } catch (IOException e) {
            printError(e,false);
        }
    }
    private void refresh(){
        String name="";
        switch (currentlyAt) {
            case "anomaly" -> name=filename;
            case "target" -> name=filenameTarget;
            case "log" -> name=filenameLog;
        }
        fileFront = new File(name+"-FRONTE.tiff");
        fileBack = new File(name+"-RETRO.tiff");
    }
}
