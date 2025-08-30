package app.tasks;

import app.Routing;
import app.classes.*;
import app.objects.objLogTimeline;
import app.steps.stockNumber;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class taskStockNumber {

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
        t.setName("taskStockNumber");
        t.setDaemon(true);
        t.start();
    }

    private static void runAfter() {
        Thread t = new Thread(new Task<>() {
            @Override protected Void call() {
                stockNumber.start();
                if(StockFile.stockNumberFXCollections.isEmpty()){
                    Routing.stockNumber = "end";
                    Routing.next();
                }
                else{
                    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                    scheduler.scheduleAtFixedRate(()->{
                        if(Routing.stockNumber.isEmpty()){
                            objLogTimeline.add("taskStockNumber","taskStockNumber idle");
                        }
                        else{
                            scheduler.shutdown();

                            Routing.stockAnomalies = "";
                            Routing.goBackTo("stockAnomalies");

                            Routing.next();
                        }
                    },0,300, TimeUnit.MILLISECONDS);

                    Platform.runLater(() -> UI.loadDefault(7,"VERIFICA SE CI SONO ANOMALIE DA SISTEMARE"));
                }


                return null;
            }
        });
        t.setName("taskStockNumber runAfter");
        t.setDaemon(true);
        t.start();
    }

}
