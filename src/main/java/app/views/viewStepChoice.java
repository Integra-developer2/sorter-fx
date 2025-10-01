package app.views;

import java.net.URL;
import java.util.ResourceBundle;

import app.Routing;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
public class viewStepChoice implements Initializable{
    @FXML private HBox stepOne;
    @FXML private HBox stepTwo;
    @FXML private HBox stepThree;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        stepOne.setOnMouseClicked(_ -> Routing.stepChoice = "moveFiles");

        stepTwo.setOnMouseClicked(_ -> Routing.stepChoice = "gray");

        stepThree.setOnMouseClicked(_ -> Routing.stepChoice = "stockAndPdf");

    }

}
