package app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static app.functions.alert;
import static app.functions.load;
import static app.functions.printError;
import static app.functions.writeOnce;
import app.o3_sorter_stock.objDoneStock;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
    private TextField stockNumber;
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
        etichette.setOnMouseClicked(event->etichette());
        jobSorter.setOnMouseClicked(event->jobSorter());
        gray.setOnMouseClicked(event->gray());
        tiff.setOnMouseClicked(event->tiff());
        btnBackwards.setOnAction(event->btnBackwards());
        btnFoward.setOnAction(event->btnFoward());
        stockNumber.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validateStockNumber(newValue);
        });
        if(!objGlobals.startingFrom.equals("move_files")){
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
    private void validateStockNumber(String stock) {
        if (!stock.isEmpty()) {
            Pattern prefixPattern = Pattern.compile("[A-Za-z]+");
            Matcher prefix = prefixPattern.matcher(stock);
            Pattern numberPattern = Pattern.compile("\\d+");
            Matcher number = numberPattern.matcher(stock);
            if (prefix.find() && number.find()) {
                gifStock.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
            }
            else{
                gifStock.setImage(new Image(App.class.getResource("img/edit.gif").toExternalForm()));
            }
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
        String stockText = stockNumber.getText();
        if(stockText.isEmpty()){
            errors.add("INSERIRE NUMERO PACCO");
            gifStock.setImage(new Image(App.class.getResource("img/error.gif").toExternalForm()));
        }
        else{
            Pattern prefixPattern = Pattern.compile("[A-Za-z]+");
            Matcher prefix = prefixPattern.matcher(stockText);
            Pattern numberPattern = Pattern.compile("\\d+");
            Matcher number = numberPattern.matcher(stockText);
            if(!prefix.find()||!number.find()){
                errors.add("NUMERO PACCO DEVI COMINCIARE CON LETTERE E FINIRE CON NUMERI");
                gifStock.setImage(new Image(App.class.getResource("img/error.gif").toExternalForm()));
            }else{
                objGlobals.stockPrefix=prefix.group();
                objGlobals.stockNumber=Integer.parseInt(number.group());
            }
        }
        if(objGlobals.startingFrom.equals("move_files")){
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
                writeOnce(objGlobals.stockPrefixFile, objGlobals.stockPrefix);
                writeOnce(objGlobals.stockNumberFile, String.valueOf(objGlobals.stockNumber));
                objGlobals.variables();
            }
            else{
                writeOnce(objGlobals.sourceEtichetteFile, objGlobals.sourceEtichette);
                writeOnce(objGlobals.sourceJobSorterFile, objGlobals.sourceJobSorter);
                writeOnce(objGlobals.stockPrefixFile, objGlobals.stockPrefix);
                writeOnce(objGlobals.stockNumberFile, String.valueOf(objGlobals.stockNumber));
                objGlobals.stockPrefixFile=objGlobals.stockPrefixFile();
                objGlobals.stockNumberFile=objGlobals.stockNumberFile();
                objGlobals.stockNumber=objGlobals.stockNumber();
                objDoneStock.add(String.valueOf(objGlobals.stockNumber));
            }

            load("viewStatusBar");
        }
    }

}
