package app;

import app.classes.Api;
import app.classes.Pc;
import app.classes.UI;
import app.objects.objLogTimeline;
import app.objects.objProgressItem;
import app.tasks.taskWorkingFolder;
import app.views.viewStatusBar;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        UI.setPages();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(UI.page(0)));
        AnchorPane root = loader.load();
        UI.main = loader.getController();
        setupFlusher();
        setupFlusherOneSec();
        Scene scene = new Scene(root);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.show();

        taskWorkingFolder.run();

    }

    private void setupFlusher() {
        Timeline logFlusher = new Timeline(
            new KeyFrame(Duration.millis(300), _ -> {
                if (!viewStatusBar.objProgressItems.isEmpty()) {
                    for (Integer id : viewStatusBar.objProgressItems.keySet()) {
                        objProgressItem pi = viewStatusBar.objProgressItems.get(id);
                        int count = pi.count;
                        Platform.runLater(() -> {
                            pi.progressBar.setProgress((double) count / pi.total);
                            pi.label.setText(pi.labelText + " : (" + count + "/" + pi.total + ")");
                        });
                    }
                }

                if(!objLogTimeline.logQueue.isEmpty()){
                    for(String page:objLogTimeline.logQueue.keySet()){
                        UI.main.appendLog(objLogTimeline.logQueue.get(page));
                        objLogTimeline.logQueue.remove(page);
                    }
                }
                Api.updateApiFile();
                Pc.cpu();
                Pc.disk();
            })
        );
        logFlusher.setCycleCount(Animation.INDEFINITE);
        logFlusher.play();
    }

    private void setupFlusherOneSec() {
        Timeline logFlusher = new Timeline(
            new KeyFrame(Duration.millis(1000), _ -> Pc.disk())
        );
        logFlusher.setCycleCount(Animation.INDEFINITE);
        logFlusher.play();
    }

}
