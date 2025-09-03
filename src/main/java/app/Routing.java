package app;

import app.classes.GrayFiles;
import app.classes.UI;
import app.objects.objGlobals;
import app.objects.objLogTimeline;
import app.tasks.*;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static app.functions.*;
import static java.lang.Thread.sleep;

public class Routing {
    public static String stepChoice = "";
    public static String moveFiles = "";
    public static String gray = "";
    public static String grayAnomalies="";
    public static String stockAnomalies = "";
    public static String stockNumber = "";
    public static String stockToShoot = "";
    public static String pdf = "";

    public static LinkedHashMap<String, File> steps = new LinkedHashMap<>();

    public static void steps() {
        steps.put("stepChoice",new File(objGlobals.logStep,"stepChoice_end"));
        steps.put("moveFiles",new File(objGlobals.logStep,"moveFiles_end"));
        steps.put("gray",new File(objGlobals.logStep,"gray_end"));
        steps.put("grayAnomalies",new File(objGlobals.logStep,"grayAnomalies_end"));
        steps.put("stockAnomalies",new File(objGlobals.logStep,"stockAnomalies_end"));
        steps.put("stockNumber",new File(objGlobals.logStep,"stockNumber_end"));
        steps.put("stockToShoot",new File(objGlobals.logStep,"stockToShoot_end"));
        steps.put("pdf",new File(objGlobals.logStep,"pdf_end"));
    }

    public static void reset(){
        for(String step : steps.keySet()){
            File stepFile = steps.get(step);
            if(stepFile.exists()){
                if(!stepFile.delete()){
                    printError(new Exception("file step not deleted"),true);
                }
            }
        }
    }

    public static void end(String step) {
        if(steps.isEmpty()) {
            steps();
        }
        String file = steps.get(step).toString();
        mkdir(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            LocalDateTime now = LocalDateTime.now();
            writer.write(now.toString());
        } catch (Exception e) {
            printError(e,true);
        }
    }

    public static String currentStep() {
        if(steps.isEmpty()) {
            steps();
        }
        for (String step : steps.keySet()) {
            File stepFile = steps.get(step);
            if(!stepFile.exists()) {
                return step;
            }
        }
        return "end";
    }

    public static void next() {
        if(!objGlobals.stop){
            String currentStep = currentStep();
            switch (currentStep) {
                case "stepChoice" -> {
                    if(!UI.main.stepChoice.isSelected()){
                        waitPermission();
                    }
                    else{
                        if(stepChoice.isEmpty()){
                            if(inputsAreDone()){
                                end("stepChoice");
                                next();
                            }
                            else{
                                taskStepChoice.run();
                            }
                        }
                        else if(stepChoice.equals("end")) {
                            end("stepChoice");
                            next();
                        }
                    }
                }
                case "moveFiles" -> {
                    if(!UI.main.moveFiles.isSelected()){
                        waitPermission();
                    }
                    else {
                        if (moveFiles.isEmpty()) {
                            taskMoveFiles.run();
                        } else if (moveFiles.equals("end")) {
                            end("moveFiles");
                            next();
                        }
                    }
                }
                case "gray" -> {
                    if(!UI.main.gray.isSelected()){
                        waitPermission();
                    }
                    else{
                        if(gray.isEmpty()){
                            taskGray.run();
                        }
                        else if(gray.equals("end")){
                            end("gray");
                            next();
                        }
                    }
                }
                case "grayAnomalies" -> {
                    if(!UI.main.grayAnomalies.isSelected()){
                        waitPermission();
                    }
                    else {
                        if (grayAnomalies.isEmpty()) {
                            taskGrayAnomalies.run();
                        } else if (grayAnomalies.equals("end")) {
                            GrayFiles.removeFolder();
                            end("grayAnomalies");
                            next();
                        }
                    }
                }
                case "stockAnomalies" ->{
                    if(!UI.main.stockAnomalies.isSelected()){
                        waitPermission();
                    }
                    else {
                        if (stockAnomalies.isEmpty()) {
                            taskStockAnomalies.run();
                        } else if (stockAnomalies.equals("end")) {
                            end("stockAnomalies");
                            next();
                        }
                    }
                }
                case "stockNumber" ->{
                    if(!UI.main.stockNumber.isSelected()){
                        waitPermission();
                    }
                    else {
                        if (stockNumber.isEmpty()) {
                            taskStockNumber.run();
                        }
                        else if (stockNumber.equals("end")) {
                            end("stockNumber");
                            next();
                        }
                    }
                }
                case "stockToShoot" ->{
                    if(!UI.main.stockToShoot.isSelected()){
                        waitPermission();
                    }
                    else {
                        if (stockToShoot.isEmpty()) {
                            taskStockToShoot.run();
                        } else if (stockToShoot.equals("end")) {
                            end("stockToShoot");
                            next();
                        }
                    }
                }
                case "pdf" ->{
                    if(!UI.main.pdf.isSelected()){
                        waitPermission();
                    }
                    else {
                        if (pdf.isEmpty()) {
                            taskPdf.run();
                        } else if (pdf.equals("end")) {
                            end("pdf");
                            next();
                        }
                    }
                }
                default -> Platform.runLater(taskEnd::run);
            }
        }
    }

    public static boolean inputsAreDone(){
        boolean stockFile = !objGlobals.sourceEtichette.isEmpty() && Files.exists(Paths.get(objGlobals.sourceEtichette));
        boolean sourceGray = !objGlobals.sourceGray.isEmpty() && Files.exists(Paths.get(objGlobals.sourceGray));
        boolean sourceTiff = !objGlobals.sourceTiff.isEmpty() && Files.exists(Paths.get(objGlobals.sourceTiff));
        boolean jobSorter = true;
        if(objGlobals.sourceJobSorter.isEmpty()){
            jobSorter=false;
        }
        else{
            for(String path:objGlobals.sourceJobSorter){
                if(!Files.exists(Paths.get(path))){
                    jobSorter=false;
                }
            }
        }
        objLogTimeline.add("inputsAreDone","[taskInputsAfter] stockFile:"+stockFile+" jobSorter: "+jobSorter+" sourceGray:"+sourceGray+" sourceTiff:"+sourceTiff);
        if(!stockFile || !jobSorter || !sourceGray || !sourceTiff){
            return false;
        }
        stepChoice = "end";
        return true;
    }

    public static void goBackTo(String step){
        if(!steps.get(step).delete()){
            printError(new Exception("Could not delete "+step),true);
        }
    }

    public static void waitPermission(){
        Thread thread = new Thread(new Task<>() {
            @Override
            public Void call() {
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleAtFixedRate(()->{
                    scheduler.shutdown();
                    objLogTimeline.add("routing","waiting for permission");
                    try {
                        sleep(300);
                    }
                    catch (InterruptedException e) {
                        logError("waitPermission",e);
                    }
                    next();
                },0,300, TimeUnit.MILLISECONDS);
                return null;
            }
        });
        thread.setDaemon(true);
        thread.start();

    }

}
