package app.views;

import app.objects.objGlobals;
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
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class viewStatusBar implements Initializable {
    @FXML public VBox body;
    private ProgressIndicator spinner;
    private Label spinnerLabel;

    public static ConcurrentHashMap<Integer,objProgressItem> objProgressItems = new ConcurrentHashMap<>();

    @FXML public void initialize(URL location, ResourceBundle resources) {}


    public static void addProgressItems(objProgressItem pi,int count){
        for(int id : objProgressItems.keySet()){
            objProgressItem objProgressItem  = objProgressItems.get(id);
            if(Objects.equals(id, pi.id) && objProgressItem.count<count){
                pi.count = count;
                objProgressItems.put(id, pi);
            }
        }
    }

    public void refresh(objProgressItem pb, Integer count) {
        addProgressItems(pb,count);
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

        int id = objGlobals.lastProgressBar.incrementAndGet();
        objProgressItem pi = new objProgressItem(progressBar,label,labelText,total,0, id);
        objProgressItems.put(id,pi);
        return pi;
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
