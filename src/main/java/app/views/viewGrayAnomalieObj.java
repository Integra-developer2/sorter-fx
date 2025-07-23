package app.views;

import app.objects.objError;
import app.objects.objGlobals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static app.functions.mkdir;
import static app.functions.printError;

public class viewGrayAnomalieObj {
    public String filenameAnomalies;
    public String filenameLog;
    public String filenameTarget;
    public String currentlyAt="anomalies";
    public File fileFront;
    public File fileBack;
    public String file;

    public viewGrayAnomalieObj(String filename){
        this.filenameAnomalies = filename;
        for (String code : objGlobals.errorMap.map.keySet()) {
            objError error = objGlobals.errorMap.map.get(code);
            filenameTarget=filename.replace(new File(error.path,code).toString(), objGlobals.targetTiff);
            if(!filenameTarget.equals(filename)){
                this.file = filename.replace(new File(error.path,code).toString(), "");
                break;
            }
        }
        filenameLog=filename.replace(objGlobals.anomalyFolderGray, objGlobals.logAnomalyFolderGray);
        refresh();
    }

    public void move(String moveTo){
        String from=from();
        String to=to(moveTo);
        mkdir(to);
        File fileFrontFrom = new File(from+"-FRONTE.tiff");
        File fileBackFrom = new File(from+"-RETRO.tiff");
        File fileFrontTo = new File(to+"-FRONTE.tiff");
        File fileBackTo = new File(to+"-RETRO.tiff");
        try {
            Files.move(fileFrontFrom.toPath(), fileFrontTo.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.move(fileBackFrom.toPath(), fileBackTo.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.currentlyAt=moveTo;
            refresh();
        }
        catch (IOException e) {
            printError(e,false);
        }
    }

    private void refresh(){
        String name = from();
        this.fileFront = new File(name+"-FRONTE.tiff");
        this.fileBack = new File(name+"-RETRO.tiff");
        assert name != null;
    }

    private String from(){
        return switch (this.currentlyAt) {
            case "anomalies" -> filenameAnomalies;
            case "target" -> filenameTarget;
            case "log" -> filenameLog;
            default ->{
                printError(new Exception("Unexpected value: " + this.currentlyAt),true);
                yield null;
            }
        };
    }

    private String to(String moveTo){
        return switch (moveTo) {
            case "target" -> filenameTarget;
            case "log" -> filenameLog;
            default ->{
                printError(new Exception("Unexpected value: " + moveTo),true);
                yield null;
            }
        };
    }
}
