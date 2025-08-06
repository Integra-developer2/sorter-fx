package app.tasks;

import app.Routing;
import app.classes.UI;
import app.steps.gray;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class taskGray {
    public static void run(){
        Thread t = new Thread(
            new Task<>() {
                @Override protected Void call() {
                    Platform.runLater(() -> {
                        UI.loadViewStatusBar(4,"LAVORAZIONE DEI FILE GRIGI");
                        runAfter();
                    });

                    return null;
                }
            }
        );
        t.setName("taskGray");
        t.setDaemon(true);
        t.start();
    }

    private static void runAfter(){
        Thread t = new Thread(new Task<>() {
            @Override
            protected Void call() {
                gray.start();
                Routing.next();
                return null;
            }
        });
        t.setName("taskGrayAfter");
        t.setDaemon(true);
        t.start();
    }

}
