package app;

import app.classes.Api;
import app.classes.Pc;
import app.classes.UI;
import app.objects.objLogTimeline;
import app.objects.objProgressBar;
import app.objects.objProgressItem;
import app.tasks.taskWorkingFolder;
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
                if (!objProgressBar.objProgressItems.isEmpty()) {
                    for(objProgressItem pi:objProgressBar.objProgressItems.keySet()) {
                        Integer count = objProgressBar.objProgressItems.get(pi);
                        Platform.runLater(() -> {
                            pi.progressBar().setProgress( (double) count / pi.total());
                            pi.label().setText(pi.labelText()+ " : (" + count + "/" + pi.total()+")");
                        });
                        objProgressBar.objProgressItems.remove(pi);
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
