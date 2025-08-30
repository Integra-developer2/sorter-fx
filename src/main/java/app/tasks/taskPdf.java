package app.tasks;

import app.Routing;
import app.classes.StockFile;
import app.classes.UI;
import javafx.application.Platform;
import javafx.concurrent.Task;
import app.steps.pdf;

public class taskPdf {
    public static void run(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    UI.loadViewStatusBar(4,"CREO I FILE PDF");
                    runAfter();
                });

                return null;
            }
        });
        t.setName("taskPdf");
        t.setDaemon(true);
        t.start();
    }

    public static void runAfter(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {
                StockFile.writeNewFile();
                pdf.start();
                Routing.next();
                return null;
            }
        });
        t.setName("taskPdf runAfter");
        t.setDaemon(true);
        t.start();
    }
}
