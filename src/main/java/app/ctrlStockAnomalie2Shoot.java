package app;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import static app.ctrlStockAnomalie2.findList;
import static app.ctrlStockAnomalie2.shootList;
import static app.ctrlStockAnomalie2.shootingId;
import static app.ctrlStockAnomalie2.shootingRow;
import static app.functions.alert;
import static app.functions.confirm;
import static app.functions.load;
import static app.functions.moveStock2;
import static app.functions.printError;
import static app.functions.writeEtichette;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class ctrlStockAnomalie2Shoot implements Initializable{
    @FXML
    private TableView<modelEtichetteShoot> tableView;
    @FXML
    private TableColumn<modelEtichetteShoot, String> findBarcodes;
    @FXML
    private TableView<modelEtichetteShootNotFound> tableView1;
    @FXML
    private TableColumn<modelEtichetteShootNotFound, String> notFound;
    @FXML
    private TextArea searchBarcodes;
    @FXML
    private Button btnFoward;
    @FXML
    private Button btnBackwards;
    private final ObservableList<modelEtichetteShootNotFound> notFoundList = FXCollections.observableArrayList();

    @SuppressWarnings({ "deprecation", "static-access" })
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setItems(shootList);
        btnFoward.setOnAction(_->btnFoward());
        btnBackwards.setOnAction(_->btnBackwards());
        findBarcodes.setCellValueFactory(cellData -> cellData.getValue().barcode());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        searchBarcodes.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("\n")) {
                String[] lines = newValue.split("\n");
                String lastLine = lines[lines.length - 1];
                searchBarcodes(lastLine);
                javafx.application.Platform.runLater(() -> searchBarcodes.clear());
            }
        });
        centerAlignColumn(findBarcodes);
        tableView1.setItems(notFoundList);
        notFound.setCellValueFactory(data -> data.getValue().barcode());
        tableView1.setColumnResizePolicy(tableView1.CONSTRAINED_RESIZE_POLICY);
    }

    private void centerAlignColumn(TableColumn<modelEtichetteShoot, String> column) {
        column.setCellFactory(_ -> {
            TableCell<modelEtichetteShoot, String> cell = new TableCell<modelEtichetteShoot, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER);
                    }
                }
            };
            return cell;
        });
    }

    private void centerAlignColumnNotFound(TableColumn<modelEtichetteShootNotFound, String> column) {
        column.setCellFactory(_ -> {
            TableCell<modelEtichetteShootNotFound, String> cell = new TableCell<modelEtichetteShootNotFound, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER);
                    }
                }
            };
            return cell;
        });
    }

    private void searchBarcodes(String barcode){
        String cleanBarcode = barcode.replace("\n", "").replace(" ", "");
        if(findList.contains(cleanBarcode)){
            alert("TROVATO","IL BARCODE "+cleanBarcode+" E STATO TROVATO");
            modelEtichette2 obj=null;
            for (modelEtichette2 modelEtichette2 : objAnomalies.stock2) {
                if (modelEtichette2.intId().equals(shootingId)) {
                    obj = modelEtichette2;
                    break;
                }
            }
            if(obj==null){
                printError(new Exception("FAILED TO FIND shootingID "+shootingId),true);
            }
            else{
                objFileEtichette fileEtichette = objGlobals.fileEtichette.get(shootingRow);
                if(Objects.equals(shootingRow, obj.row1())){
                    fileEtichette.lastBarcode = obj.lastBarcode();
                    fileEtichette.progEnd = obj.indexEnd();
                }
                else{
                    fileEtichette.firstBarcode = obj.firstBarcode();
                    fileEtichette.progStart = obj.indexStart();
                }
                objGlobals.fileEtichette.put(shootingRow,fileEtichette);
                moveStock2(obj.group(), obj.fileList(), "target");
                objAnomalies.stock2.remove(obj);
                finish();
            }
        }
        else{
            notFoundList.add(new modelEtichetteShootNotFound(cleanBarcode));
            centerAlignColumnNotFound(notFound);
        }
    }

    private void btnFoward(){
        boolean start[] = {true};
        CountDownLatch latch = new CountDownLatch(1);
        try {
            try {
                start[0] = confirm("ATENZIONE","CONFERMI DI AVER SPARATO TUTTI I BARCODE ? QUESTA AZIONE è IRREVERSIBILE");
            }
            finally {
                latch.countDown();
            }
            latch.await();
        } catch (InterruptedException e) {
            printError(e,false);
        }
        if(start[0]){
            modelEtichette2 obj=null;
            for (modelEtichette2 modelEtichette2 : objAnomalies.stock2) {
                if (modelEtichette2.intId().equals(shootingId)) {
                    obj = modelEtichette2;
                    break;
                }
            }
            if(obj==null){
                printError(new Exception("FAILED TO FIND shootingID "+shootingId),true);
            }
            else{
                objFileEtichette fileEtichette;
                Integer fixRow;
                if(Objects.equals(shootingRow, obj.row1())){
                    fixRow=obj.row2();
                    fileEtichette = objGlobals.fileEtichette.get(fixRow);
                    fileEtichette.firstBarcode = obj.firstBarcode();
                    fileEtichette.progStart = obj.indexStart();
                }
                else{
                    fixRow=obj.row1();
                    fileEtichette = objGlobals.fileEtichette.get(fixRow);
                    fileEtichette.lastBarcode = obj.lastBarcode();
                    fileEtichette.progEnd = obj.indexEnd();
                }
                objGlobals.fileEtichette.put(fixRow,fileEtichette);
                moveStock2(obj.group(), obj.fileList(), "target");
                objAnomalies.stock2.remove(obj);
                finish();
            }
        }

    }

    private void finish(){
        if(objAnomalies.stock2.isEmpty()){
            writeEtichette();
            objGlobals.skipAnomalies=true;
            load("viewStatusBar");
        }
        else{
            btnBackwards();
        }
    }

    private void btnBackwards(){
        load("viewStockAnomalie2",1000,600);
    }

}
