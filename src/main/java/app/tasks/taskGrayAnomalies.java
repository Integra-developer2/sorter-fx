package app.tasks;

import app.Routing;
import app.classes.GrayFiles;
import app.classes.UI;
import app.objects.objLogTimeline;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class taskGrayAnomalies {

    public static void run(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {
            Platform.runLater(() -> {
                UI.loadViewStatusBar(4,"VERIFICO SE CI SONO ANOMALIE");
                UI.controller.addSpinner("VERIFICO SE CI SONO ANOMALIE");
                runAfter();
            });
            return null;
            }
        });
        t.setName("taskGrayAnomalies");
        t.setDaemon(true);
        t.start();
    }

    public static void runAfter(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {
                if(!GrayFiles.hasGrayAnomalies()){
                    Routing.grayAnomalies = "end";
                    Routing.next();
                }
                else{
                    Routing.grayAnomalies = "";

                    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                    scheduler.scheduleAtFixedRate(()->{
                        if(Routing.grayAnomalies.isEmpty()){
                            objLogTimeline.add("taskGrayAnomalies","[ taskGrayAnomalies ] workAnomalies idle");
                        }
                        else{
                            scheduler.shutdown();
                            Routing.next();
                        }
                    },0,300, TimeUnit.MILLISECONDS);

                    Platform.runLater(()->{
                        UI.controller.removeSpinner();
                        UI.loadDefault(5,"SISTEMA LE ANOMALIE PRIMA DI CONTINUARE");
                    });

                }
                return null;
            }
        });
        t.setName("taskGrayAnomalies");
        t.setDaemon(true);
        t.start();
    }

}
