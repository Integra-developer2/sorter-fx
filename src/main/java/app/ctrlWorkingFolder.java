package app;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static app.functions.alert;
import static app.functions.load;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ctrlWorkingFolder implements Initializable {
    @FXML
    private HBox workingFolder;
    @FXML
    private ImageView gifImageView;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        objGlobals.workingFolder="";
        workingFolder.setOnMouseClicked(event->workingFolder());
    }
    @FXML
    public void workingFolder(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("CARTELLA DESTINAZIONE");
        Stage stage = (Stage) workingFolder.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            objGlobals.workingFolder = selectedDirectory.getAbsolutePath();
            gifImageView.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
        }
        btnFoward();
    }
    @FXML
    public void btnFoward() {
        if(objGlobals.workingFolder.isEmpty()){
            alert("INFORMAZIONI MANCANTI","SELEZIONA LA CARTELLA DOVE I FILE SARANNO COPIATI E LAVORATI");
            gifImageView.setImage(new Image(App.class.getResource("img/error.gif").toExternalForm()));
        }else{
            if(objGlobals.hasInputs()){
                load("viewStatusBar");
            }
            else{
                load("viewInputs",450,500);
            }
        }
    }

}
