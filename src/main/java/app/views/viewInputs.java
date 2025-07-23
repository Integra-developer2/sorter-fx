package app.views;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import app.App;
import app.objects.objGlobals;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class viewInputs implements Initializable {
    @FXML private HBox etichette;
    @FXML private HBox jobSorter;
    @FXML private HBox gray;
    @FXML private HBox tiff;
    @FXML private ImageView gifEtichette;
    @FXML private ImageView gifJobSorter;
    @FXML private ImageView gifGray;
    @FXML private ImageView gifTiff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        etichette.setOnMouseClicked(_->etichette());
        jobSorter.setOnMouseClicked(_->jobSorter());
        gray.setOnMouseClicked(_->gray());
        tiff.setOnMouseClicked(_->tiff());
    }
    @FXML public void etichette(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Excel Files", "*.csv", "*.csv")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            objGlobals.sourceEtichette=selectedFile.getAbsolutePath();
            gifEtichette.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/done.gif")).toExternalForm()));
        }
    }
    @FXML public void jobSorter(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Excel Files", "*.csv")
        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                objGlobals.sourceJobSorter.add(file.getAbsolutePath());
            }
            gifJobSorter.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/done.gif")).toExternalForm()));
        }
    }
    @FXML public void gray(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("CARTELLA FILE GRIGI");

        Stage stage = (Stage) gray.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            objGlobals.sourceGray = selectedDirectory.getAbsolutePath();
            gifGray.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/done.gif")).toExternalForm()));
        }
    }
    @FXML public void tiff(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("CARTELLA FILE TIFF");
        Stage stage = (Stage) tiff.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            objGlobals.sourceTiff = selectedDirectory.getAbsolutePath();
            gifTiff.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/done.gif")).toExternalForm()));
        }
    }

}
