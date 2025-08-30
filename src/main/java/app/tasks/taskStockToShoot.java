package app.tasks;

import app.Routing;
import app.classes.StockFile;
import app.classes.UI;
import app.classes.ValidTiffs;
import app.objects.objLogTimeline;
import app.steps.stockNumber;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("DuplicatedCode")
public class taskStockToShoot {

    public static void run(){
        Thread t = new Thread(
            new Task<>() {
                @Override protected Void call() {
                    Platform.runLater(() -> {
                        UI.loadViewStatusBar(4,"VERIFICO SE CI SONO ANOMALIE");
                        UI.controller.addSpinner("VERIFICO SE CI SONO ANOMALIE");
                        runAfter();
                    });
                    return null;
                }
            }
        );
        t.setName("taskStockToShoot");
        t.setDaemon(true);
        t.start();
    }

    private static void runAfter() {
        Thread t = new Thread(new Task<>() {
            @Override protected Void call() {
                stockNumber.start();

                if(ValidTiffs.modelStockToShootFXCollections.isEmpty()){
                    ValidTiffs.writeToFile();
                    Routing.stockToShoot = "end";
                    Routing.next();
                }
                else{
                    scheduleNext();
                    Platform.runLater(() -> UI.loadDefault(8,"SISTEMA LE ANOMALIE"));
                }

                return null;
            }
        });
        t.setName("taskStockNumber runAfter");
        t.setDaemon(true);
        t.start();
    }

    private static void scheduleNext(){
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(()->{
            if(Routing.stockToShoot.isEmpty()){
                objLogTimeline.add("taskStockToShoot","taskStockToShoot idle");
            }
            else{
                scheduler.shutdown();

                switch (Routing.stockToShoot){
                    case "shoot":{
                        Routing.stockToShoot = "";
                        scheduleNext();
                        Platform.runLater(() -> UI.loadDefault(9,"SISTEMA LE ANOMALIE"));
                        break;
                    }
                    case "back":{
                        Routing.stockToShoot = "";
                        scheduleNext();
                        Platform.runLater(() -> UI.loadDefault(8,"SISTEMA LE ANOMALIE"));
                        break;
                    }
                    case "end":{
                        Routing.next();
                    }
                }
            }


        },0,300, TimeUnit.MILLISECONDS);
    }

}
