package app.tasks;

import app.classes.UI;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class taskEnd {
    public static void run(){
        Thread t = new Thread( new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> UI.loadDefault(10,"LAVORAZIONE FINITA"));

                return null;
            }
        });
        t.setName("taskEnd");
        t.setDaemon(true);
        t.start();
    }
}
