package app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;

import static app.functions.alert;
import static app.functions.load;
import static app.functions.printError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ctrlMoveFilesAnomalie implements Initializable{
    @FXML
    private Label title;
    @FXML
    private ListView<String> list;
    @FXML
    private Button btnDownload;
    @FXML
    private Button btnFoward;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(objGlobals.version);
        btnDownload.setOnAction(_->donwloadTxt());
        btnFoward.setOnAction(_->foward());
        ObservableList<String> items = FXCollections.observableArrayList (objAnomalies.moveFiles);
        list.setItems(items);
        list.setCellFactory(_ -> {
            var cell = new javafx.scene.control.ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                    showContextMenu(cell, event);
                }
            });

            return cell;
        });        
    }

    private void showContextMenu(javafx.scene.control.ListCell<String> cell, MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem copyPath = new MenuItem("Copia Percorso");
        copyPath.setOnAction(_ -> {
            String text = cell.getItem();
            Path path = Paths.get(text);
            copyToClipboard(path.getParent().toString());
        });

        MenuItem copyFilename = new MenuItem("Copia Nome File");
        copyFilename.setOnAction(_ -> {
            String text = cell.getItem();
            Path path = Paths.get(text);
            copyToClipboard(path.getFileName().toString()); 
        });

        contextMenu.getItems().add(copyPath);
        contextMenu.getItems().add(copyFilename);
        contextMenu.show(cell, event.getScreenX(), event.getScreenY());
    }

    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    private void donwloadTxt() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("TXT", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String line : objAnomalies.moveFiles) {
                    writer.append(line);
                }
            } catch (IOException e) {
                printError(e,false);
            }
        }
    }

    private void foward(){
        if(hasAnomaly()){
            alert("ANOMALIA","SISTEMA LE ANOMALIE PRIMA DI CONTINUARE");
        }
        else{
            load("viewStatusBar");
        }
    }

    private static Boolean hasAnomaly() {
        ConcurrentLinkedQueue<String> moveFiles = new ConcurrentLinkedQueue<>();
        for (String from : objAnomalies.moveFiles) {
            if(from.endsWith(".csv")){
                if (from.equals(objGlobals.sourceEtichette)) {
                    File fileTo = new File(objGlobals.targetEtichette);
                    if(!fileTo.exists()){
                        moveFiles.add(from);
                    }
                }
                else{
                    File fileFrom = new File(from);
                    File folderTo = new File(objGlobals.jogSorterFolder);
                    String to = from.replace(fileFrom.getParent(), folderTo.getPath());
                    File fileTo = new File(to);
                    if(!fileTo.exists()){
                        moveFiles.add(from);
                    }
                }
            }
            else if(from.endsWith(".tiff")){
                Path sourceTiffPath = Paths.get(objGlobals.sourceTiff);
                String strSourceTiff = sourceTiffPath.toString();
                Path targetTiffPath = Paths.get(objGlobals.targetTiff);
                String strTargetTiff = targetTiffPath.toString();
                String to = from.replace(strSourceTiff, strTargetTiff);
                File fileTo = new File(to);
                if(!fileTo.exists()){
                    moveFiles.add(from);
                }
            }
            else if(from.endsWith(".bmp")){
                Path sourceGrayPath = Paths.get(objGlobals.sourceGray);
                String strSourceGray = sourceGrayPath.toString();
                Path targetGrayPath = Paths.get(objGlobals.targetGray);
                String strTargetGray = targetGrayPath.toString();
                String to = from.replace(strSourceGray, strTargetGray);
                File fileTo = new File(to);
                if(!fileTo.exists()){
                    moveFiles.add(from);
                }
            }
        }
        objAnomalies.moveFiles = moveFiles;
        return !objAnomalies.moveFiles.isEmpty();
    }

}
