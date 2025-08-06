package app.tasks;

import app.Routing;
import app.classes.UI;
import app.objects.objGlobals;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static app.functions.printError;

public class taskInputs {
    public static void run(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    UI.loadDefault(3,"SELEZIONA I PERCORSI INDICATI");
                    runAfter();
                });
                return null;
            }
        });
        t.setName("taskInputs");
        t.setDaemon(true);
        t.start();
    }

    private static void runAfter(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleAtFixedRate(()->{
                    if(!Routing.inputsAreDone()) {
                        UI.main.appendLog("[ taskStepChoice ] idle");
                    }
                    else{
                        scheduler.shutdown();
                        Platform.runLater(() -> {

                            if (objGlobals.stop) {
                                UI.main.appendLog("[ taskStepChoice ] Stopped by objGlobals.stop");
                                return;
                            }

                            writeSource(objGlobals.logSourceTiff,objGlobals.sourceTiff);
                            writeSource(objGlobals.logSourceGray,objGlobals.sourceGray);
                            writeSource(objGlobals.logSourceEtichette,objGlobals.sourceEtichette);
                            writeSource(objGlobals.logSourceJobSorter,objGlobals.sourceJobSorter);

                            Routing.end("stepChoice");
                            Routing.next();

                        });

                    }
                },0,300, TimeUnit.MILLISECONDS);
                return null;
            }
        });
        t.setName("taskInputsAfter");
        t.setDaemon(true);
        t.start();
    }

    public static void writeSource(String path, String source){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(source);
        }
        catch (IOException e) {
            printError(e,true);
        }
    }

    public static void writeSource(String path, ArrayList<String> source){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for(String to : source){
                writer.write(to);
                writer.newLine();
            }
        }
        catch (IOException e) {
            printError(e,true);
        }
    }


}
