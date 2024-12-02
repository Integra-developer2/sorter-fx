package app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static app.functions.alert;
import static app.functions.load;
import static app.functions.printError;
import static app.functions.writeOnce;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ctrlInputs implements Initializable {
    @FXML
    public ArrayList<String>filesEtichete=new ArrayList<>();
    @FXML
    private HBox etichette;
    @FXML
    private HBox jobSorter;
    @FXML
    private HBox gray;
    @FXML
    private HBox tiff;
    @FXML
    private HBox stock;
    @FXML
    private Button btnFoward;
    @FXML
    private Button btnBackwards;
    @FXML
    private ImageView gifEtichette;
    @FXML
    private ImageView gifJobSorter;
    @FXML
    private ImageView gifGray;
    @FXML
    private ImageView gifTiff;
    @FXML
    private ImageView gifStock;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        etichette.setOnMouseClicked(_->etichette());
        jobSorter.setOnMouseClicked(_->jobSorter());
        gray.setOnMouseClicked(_->gray());
        tiff.setOnMouseClicked(_->tiff());
        stock.setOnMouseClicked(_->stock());
        btnBackwards.setOnAction(_->btnBackwards());
        btnFoward.setOnAction(_->btnFoward());
        if(!objGlobals.startingFrom.equals("move_files")){
            stock.setVisible(false);
            etichette.setVisible(false);
            jobSorter.setVisible(false);
            gray.setVisible(false);
            tiff.setVisible(false);
        }
    }
    @FXML
    public void etichette(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Excel Files", "*.csv", "*.csv")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            objGlobals.sourceEtichette=selectedFile.getAbsolutePath();
            gifEtichette.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
        }
    }
    @FXML
    public void jobSorter(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Excel Files", "*.csv")
        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                objGlobals.sourceJobSorter.add(file.getAbsolutePath());
            }
            gifJobSorter.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
        }
    }
    @FXML
    public void gray(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("CARTELLA FILE GRIGI");

        Stage stage = (Stage) gray.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            objGlobals.sourceGray = selectedDirectory.getAbsolutePath();
            gifGray.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
        }
    }
    @FXML
    public void tiff(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("CARTELLA FILE TIFF");
        Stage stage = (Stage) tiff.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            objGlobals.sourceTiff = selectedDirectory.getAbsolutePath();
            gifTiff.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
        }
    }
    @FXML
    public void stock(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Excel Files", "*.csv", "*.csv")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            objGlobals.sourceStock=selectedFile.getAbsolutePath();
            gifStock.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
        }
    }
    @FXML
    public void btnBackwards() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("viewWorkingFolder.fxml"));
            Parent root = loader.load();
            Scene newScene = new Scene(root);
            newScene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
            Stage stage = (Stage) btnFoward.getScene().getWindow();
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {printError(e,false);}
    }
    @FXML
    public void btnFoward() {
        ArrayList<String> errors=new ArrayList<>();
        if(objGlobals.startingFrom.equals("move_files")){
            if(objGlobals.sourceStock.isEmpty()){
                errors.add("INSERIRE IL FILE PACCO INIZIALE");
                gifStock.setImage(new Image(App.class.getResource("img/error.gif").toExternalForm()));
            }
            if(objGlobals.sourceEtichette.isEmpty()){
                errors.add("INSERIRE IL FILE ETICHETTE");
                gifEtichette.setImage(new Image(App.class.getResource("img/error.gif").toExternalForm()));
            }
            if(objGlobals.sourceJobSorter.isEmpty()){
                errors.add("INSERIRE ALMENO UN FILE JOBSORTER");
                gifJobSorter.setImage(new Image(App.class.getResource("img/error.gif").toExternalForm()));
            }
            if(objGlobals.sourceGray.isEmpty()){
                errors.add("INSERIRE IL PERCORSO PER I FILE GRIGI");
                gifGray.setImage(new Image(App.class.getResource("img/error.gif").toExternalForm()));
            }
            if(objGlobals.sourceTiff.isEmpty()){
                errors.add("INSERIRE IL PERCORSO PER I FILE TIFF");
                gifTiff.setImage(new Image(App.class.getResource("img/error.gif").toExternalForm()));
            }
        }
        if(!errors.isEmpty()){
            alert("INFORMAZIONI MANCANTI",errors);
        }else{
            if(objGlobals.startingFrom.equals("move_files")){
                writeOnce(objGlobals.sourceEtichetteFile, objGlobals.sourceEtichette);
                writeOnce(objGlobals.sourceJobSorterFile, objGlobals.sourceJobSorter);
                writeOnce(objGlobals.sourceGrayFile, objGlobals.sourceGray);
                writeOnce(objGlobals.sourceTiffFile, objGlobals.sourceTiff);
                writeOnce(objGlobals.sourceStockFile, objGlobals.sourceStock);
                objGlobals.variables();
            }
            else{
                writeOnce(objGlobals.sourceEtichetteFile, objGlobals.sourceEtichette);
                writeOnce(objGlobals.sourceJobSorterFile, objGlobals.sourceJobSorter);
                writeOnce(objGlobals.sourceStockFile, objGlobals.sourceStock);
            }

            load("viewStatusBar");
        }
    }

}
