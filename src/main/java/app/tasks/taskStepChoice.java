package app.tasks;

import app.Routing;
import app.classes.JobSorter;
import app.classes.StockFile;
import app.classes.UI;
import app.objects.objGlobals;
import app.objects.objLogTimeline;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static app.functions.*;

public class taskStepChoice {

    public static void run(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    UI.loadDefault(2,"SCEGLI LO STEP DA CUI INIZIARE");
                    runAfter();
                });
                return null;
            }
        });
        t.setName("taskStepChoice");
        t.setDaemon(true);
        t.start();
    }

    public static void runAfter(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleAtFixedRate(()->{
                    if(Routing.stepChoice.isEmpty()) {
                        objLogTimeline.add("taskStepChoice","[ taskStepChoiceAfter ] idle");
                    }
                    else{
                        scheduler.shutdown();
                        Platform.runLater(() -> {
                            if (objGlobals.stop) {
                                objLogTimeline.add("taskStepChoice","[ taskStepChoiceAfter ] Stopped by objGlobals.stop");
                                return;
                            }

                            switch (Routing.stepChoice) {
                                case "moveFiles" -> taskInputs.run();
                                case "gray" -> {
                                    if (!fileExists()) {
                                        alert("ERRORE", "SE VUOI SALTARE IL PRIMO STEP DEVI IMPOSTARE I FILE CONFORME CARTELLA __MODELLO__");
                                        objGlobals.runStep = "";
                                        taskStepChoice.run();
                                    }
                                    else {
                                        boolean canGoNext=true;
                                        File latestFileEtichette = StockFile.LatestFileEtichette();
                                        if(latestFileEtichette==null) {
                                            canGoNext=false;
                                            objGlobals.runStep = "";
                                            alert("ERRORE", "MANCA IL FILE ETICHETTE");
                                            taskStepChoice.run();
                                        }
                                        else{
                                            objGlobals.sourceEtichette = latestFileEtichette.toString();
                                            objGlobals.targetEtichette = latestFileEtichette.toString();
                                        }
                                        JobSorter.getData();
                                        if(JobSorter.barcodeRow.isEmpty()||objGlobals.sourceJobSorter.isEmpty()){
                                            JobSorter.hasData = false;
                                            canGoNext=false;
                                            objGlobals.runStep = "";
                                            alert("ERRORE", "MANCANO I FILE JOB SORTER");
                                            taskStepChoice.run();
                                        }
                                        if(canGoNext){
                                            taskInputs.writeSource(objGlobals.logSourceTiff,objGlobals.targetTiff);
                                            taskInputs.writeSource(objGlobals.logSourceGray,objGlobals.targetGray);
                                            taskInputs.writeSource(objGlobals.logSourceEtichette,objGlobals.sourceEtichette);
                                            taskInputs.writeSource(objGlobals.logSourceJobSorter,objGlobals.sourceJobSorter);
                                            Routing.end("moveFiles");
                                            Routing.stepChoice = "end";
                                            Routing.next();
                                        }
                                    }
                                }
                            }
                        });

                    }
                },0,300, TimeUnit.MILLISECONDS);

                return null;
            }
        });
        t.setName("taskStepChoiceAfter");
        t.setDaemon(true);
        t.start();
    }

    private static boolean fileExists() {

        if( !(new File(objGlobals.etichetteFolder)).exists()){
            return false;
        }
        if( !(new File(objGlobals.jobSorterFolder)).exists()){
            return false;
        }
        if (!(new File(objGlobals.targetGray)).exists()) {
            return false;
        }
        return (new File(objGlobals.targetTiff)).exists();

    }
}
