package app.views;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static app.functions.alert;

import app.App;
import app.objects.objGlobals;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class viewWorkingFolder implements Initializable{
    @FXML private VBox workingFolder;
    @FXML private ImageView gifImageView;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        objGlobals.workingFolder="";
        workingFolder.setOnMouseClicked(_->workingFolder());
    }
    @FXML
    public void workingFolder(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("CARTELLA DESTINAZIONE");
        Stage stage = (Stage) workingFolder.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            objGlobals.workingFolder = selectedDirectory.getAbsolutePath();
            gifImageView.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/done.gif")).toExternalForm()));
        }
        btnForward();
    }
    @FXML
    public void btnForward() {
        if(objGlobals.workingFolder.isEmpty()){
            alert("INFORMAZIONI MANCANTI","SELEZIONA LA CARTELLA DOVE I FILE SARANNO COPIATI E LAVORATI");
            gifImageView.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/error.gif")).toExternalForm()));
        }
    }

}
