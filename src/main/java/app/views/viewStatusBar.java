package app.views;

import app.objects.objProgressBar;
import app.objects.objProgressItem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import static app.functions.logError;

import java.net.URL;
import java.util.ResourceBundle;

public class viewStatusBar implements Initializable {
    @FXML public VBox body;
    private ProgressIndicator spinner;
    private Label spinnerLabel;

    @FXML public void initialize(URL location, ResourceBundle resources) {}

    public void refresh(objProgressItem pb, Integer count) {
        objProgressBar.add(pb,count);
    }

    public objProgressItem addProgress(String labelText,Integer total) {
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(600);
        progressBar.setPrefHeight(40);

        Label label = new Label(labelText);

        Platform.runLater(() -> {
            body.getChildren().add(progressBar);
            body.getChildren().add(label);
        });

        return new objProgressItem(progressBar,label,labelText,total);
    }

    public void addSpinner(String labelText) {
        try{
            Thread.sleep(300);
            spinner = new ProgressIndicator();
            spinner.setPrefSize(40, 40);
            spinner.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

            spinnerLabel = new Label(labelText);

            Platform.runLater(() -> {
                body.getChildren().add(spinner);
                body.getChildren().add(spinnerLabel);
            });
        }
        catch(Exception e){
            logError("addSpinner fail",e);
        }

    }

    public void removeSpinner() {
        Platform.runLater(() -> {
            body.getChildren().remove(spinner);
            body.getChildren().remove(spinnerLabel);
        });
    }

}
