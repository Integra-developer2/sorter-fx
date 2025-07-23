package app.tasks;

import app.Routing;
import app.classes.UI;
import app.objects.objGlobals;
import app.objects.objLogTimeline;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static app.functions.readArrayFromFile;
import static app.functions.readStringFromFile;

public class taskWorkingFolder {

    public static void run(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    UI.loadDefault(1,"SELEZIONA LA CARTELLA DOVE I FILE SARANNO LAVORATI");
                    runAfter();
                });
                return null;
            }
        });
        t.setName("taskWorkingFolder");
        t.setDaemon(true);
        t.start();
    }

    public static void runAfter(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleAtFixedRate(()->{
                    if(objGlobals.workingFolder.isEmpty()) {
                        objLogTimeline.add("taskWorkingFolder","[ taskWorkingFolderAfter ] idle");
                    }
                    else{
                        scheduler.shutdown();
                        Platform.runLater(() -> {

                            if (objGlobals.stop) {
                                objLogTimeline.add("taskWorkingFolder","[ taskWorkingFolderAfter ] Stopped by objGlobals.stop");
                                return;
                            }

                            objGlobals.variables();
                            readVariablesFromFiles();
                            Routing.next();
                        });

                    }
                },0,300, TimeUnit.MILLISECONDS);
                return null;
            }
        });
        t.setName("taskWorkingFolderAfter");
        t.setDaemon(true);
        t.start();
    }

    private static void readVariablesFromFiles(){
        if(Files.exists(Paths.get(objGlobals.logSourceGray))){
            objGlobals.sourceGray = readStringFromFile(new File(objGlobals.logSourceGray));
        }
        if(Files.exists(Paths.get(objGlobals.logSourceTiff))){
            objGlobals.sourceTiff = readStringFromFile(new File(objGlobals.logSourceTiff));
        }
        if(Files.exists(Paths.get(objGlobals.logSourceJobSorter))){
            objGlobals.sourceJobSorter = readArrayFromFile(new File(objGlobals.logSourceJobSorter));
        }
        if(Files.exists(Paths.get(objGlobals.logSourceEtichette))){
            objGlobals.sourceEtichette = readStringFromFile(new File(objGlobals.logSourceEtichette));
        }
    }
}
