package app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;

import static app.functions.alert;
import static app.functions.load;
import static app.functions.printError;
import static app.functions.step;
import static app.functions.stepFile;
import static app.o3_sorter_stock.functions.fileExists;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
public class ctrlStepChoice implements Initializable{
    @FXML
    private HBox stepOne;
    @FXML
    private HBox stepTwo;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stepOne.setOnMouseClicked(event -> { objGlobals.startingFrom="move_files";});
        stepTwo.setOnMouseClicked(event -> {
            objGlobals.startingFrom="gray";
            if(!fileExists(objGlobals.etichetteFolder)||!fileExists(objGlobals.jogSorterFolder)||!fileExists(objGlobals.targetGray)||!fileExists(objGlobals.targetTiff)){
                alert("ERRORE","SE VUOI SALTARE IL PRIMO STEP DEVI IMPOSTARE I FILE CONFORME CARTELLA __MODELLO__");
            }
            else{
                try {
                    Files.walkFileTree(Paths.get(objGlobals.etichetteFolder), new SimpleFileVisitor<Path>()  {
                        @Override
                        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                            if(path.toString().endsWith(".csv")){
                                objGlobals.sourceEtichette = path.toString();
                                objGlobals.targetEtichette = path.toString();
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                    File stepMoveFiles=stepFile("moveFilesEnd");
                    step(stepMoveFiles);
                    load("viewInputs",450,500);                
                } catch (IOException e) {
                    printError(e, true);
                }                
            }
        });
    }

}
