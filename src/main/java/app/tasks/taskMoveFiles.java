package app.tasks;

import app.Routing;
import app.classes.UI;
import app.objects.objGlobals;
import app.objects.objLogTimeline;
import app.steps.moveFiles;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static app.functions.confirm;
import static app.functions.printError;

public class taskMoveFiles {
    public static void run(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call()  {
                Platform.runLater(() -> {
                    UI.loadViewStatusBar(4,"SPOSTA E CONTROLLA FILES");
                    runAfter();
                });
                return null;
            }
        });
        t.setName("taskMoveFiles");
        t.setDaemon(true);
        t.start();
    }

    private static void runAfter(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {

                moveFiles.start();

                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

                scheduler.scheduleAtFixedRate(() -> {
                    if(Routing.moveFiles.isEmpty()){
                        objLogTimeline.add("taskMoveFiles","[ taskMoveFiles runAfter ] idle");
                    }
                    else{
                        scheduler.shutdown();

                        Platform.runLater(() -> {
                            if (objGlobals.stop) {
                                objLogTimeline.add("taskMoveFiles","[ taskMoveFiles runAfter ] Stopped by objGlobals.stop");
                                return;
                            }
                            if(Routing.moveFiles.equals("outOfSpace")){
                                boolean[] start = {true};
                                CountDownLatch latch = new CountDownLatch(1);
                                Platform.runLater(() -> {
                                    try {
                                        start[0] = confirm("SPAZIO INSUFFICIENTE", "SPAZIO INSUFFICIENTE ! CONTINUA LO STESSO ?");
                                    }
                                    finally {
                                        latch.countDown();
                                    }
                                });
                                try {
                                    latch.await();
                                } catch (InterruptedException e) {
                                    printError(e,true);
                                }
                                if(start[0]){
                                    taskMoveFiles.run();
                                }
                                else{
                                    Platform.exit();
                                }
                            }
                            Routing.next();
                        });
                    }

                }, 0, 300, TimeUnit.MILLISECONDS);
                return null;
            }
        });
        t.setName("taskMoveFilesAfter");
        t.setDaemon(true);
        t.start();
    }
}
