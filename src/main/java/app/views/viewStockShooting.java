package app.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import app.Routing;
import app.classes.JobSorter;
import app.classes.StockFile;
import app.classes.ValidTiffs;
import app.models.modelStockShootNotFound;
import app.models.modelStockToShoot;
import app.objects.objLogTimeline;
import app.objects.objValidTiff;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import static app.functions.*;

public class viewStockShooting implements Initializable{
    @FXML private TableView<modelStockShootNotFound> tableView;
    @FXML private TableColumn<modelStockShootNotFound, String> findBarcodes;
    @FXML private TableView<modelStockShootNotFound> tableView1;
    @FXML private TableColumn<modelStockShootNotFound, String> notFound;
    @FXML private TextArea searchBarcodes;
    @FXML private Button btnForward;
    @FXML private Button btnBackwards;
    private final ObservableList<modelStockShootNotFound> notFoundList = FXCollections.observableArrayList();
    private final ObservableList<modelStockShootNotFound> toFindList = FXCollections.observableArrayList();
    private final ArrayList<String> find = new ArrayList<>();
    private String firstBarcode;
    private int firstIndex;
    private String lastBarcode;
    private int lastIndex;
    private modelStockToShoot modelStockToShoot;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modelStockToShoot = ValidTiffs.modelStockShootingFXCollections.getFirst();
        List<objValidTiff> objValidTiffs = modelStockToShoot.objValidTiff();
        for(objValidTiff objValidTiff : objValidTiffs){
            if(firstBarcode==null||firstIndex>objValidTiff.index){
                firstBarcode = objValidTiff.barcode;
                firstIndex = objValidTiff.index;
            }
            if(lastBarcode==null||lastIndex<objValidTiff.index){
                lastBarcode = objValidTiff.barcode;
                lastIndex = objValidTiff.index;
            }
            toFindList.add(new modelStockShootNotFound(objValidTiff.barcode));
            find.add(objValidTiff.barcode);
            String alternative = JobSorter.barcodeAlternative(objValidTiff.barcode);
            if(alternative != null && !alternative.isEmpty()){
                toFindList.add(new modelStockShootNotFound(alternative));
                find.add(alternative);
            }
            String reverse = JobSorter.alternativeBarcode(objValidTiff.barcode);
            if(reverse != null && !reverse.isEmpty()){
                toFindList.add(new modelStockShootNotFound(reverse));
                find.add(reverse);
            }
        }
        tableView.setItems(toFindList);

        btnForward.setOnAction(_->btnForward());
        btnBackwards.setOnAction(_->btnBackwards());
        findBarcodes.setCellValueFactory(cellData -> cellData.getValue().barcode());

        searchBarcodes.textProperty().addListener((_, _, newValue) -> {
            if (newValue.contains("\n")) {
                String[] lines = newValue.split("\n");
                String lastLine = lines[lines.length - 1];
                searchBarcodes(lastLine);
                Platform.runLater(() -> searchBarcodes.clear());
            }
        });

        tableView1.setItems(notFoundList);
        notFound.setCellValueFactory(data -> data.getValue().barcode());

        findBarcodes.prefWidthProperty().bind(tableView.widthProperty());
        notFound.prefWidthProperty().bind(tableView1.widthProperty());

    }

    private void searchBarcodes(String barcode){
        String cleanBarcode = barcode.replace("\n", "").replace(" ", "");

        if(find.contains(cleanBarcode)){
            alert("TROVATO","IL BARCODE "+cleanBarcode+" E STATO TROVATO");
            signToRow(ValidTiffs.shootingAt);
        }
        else{
            notFoundList.add(new modelStockShootNotFound(cleanBarcode));
        }
    }

    private void btnForward(){
        AtomicReference<Boolean> wasConfirmed = new AtomicReference<>();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(()->{
            if(wasConfirmed.get() == null){
                objLogTimeline.add("viewStockShooting","viewStockShooting idle");
            }
            else{
                scheduler.shutdown();

                if(wasConfirmed.get()){
                    String rowToSign = ValidTiffs.shootingAt.equals("row1") ? "row2" : "row1";
                    signToRow(rowToSign);
                }

            }
        },0,300, TimeUnit.MILLISECONDS);

        Platform.runLater(() -> wasConfirmed.set(confirm("AZIONE IRREVERSIBLE", "CONFERMI DI AVER SPARATO TUTTI I BARCODE DI QUESTO SCATOLO ?")));

    }

    private void signToRow(String rowToSign){

        Integer row = null;

        switch (rowToSign){
            case "row1":{
                row = modelStockToShoot.row1();
                StockFile.rowObject(row).lastBarcode = lastBarcode;
                StockFile.rowObject(row).progEnd = String.valueOf(lastIndex);
                break;
            }
            case "row2":{
                row = modelStockToShoot.row2();
                StockFile.rowObject(row).firstBarcode = firstBarcode;
                StockFile.rowObject(row).progStart = String.valueOf(firstIndex);
                break;
            }
        }

        if(row != null){
            for(modelStockShootNotFound barcodeInModel : toFindList){
                String barcode = barcodeInModel.barcode().getValue();
                objValidTiff objValidTiff = ValidTiffs.barcodeObject.get(barcode);
                if(objValidTiff!=null){
                    ValidTiffs.assignToStockRow(objValidTiff,StockFile.rowObject(row));
                }
            }
            ValidTiffs.modelStockToShootFXCollections.remove(ValidTiffs.shootingIndex);

            if(ValidTiffs.modelStockToShootFXCollections.isEmpty()){
                Routing.stockToShoot = "end";
            }
            else{
                Routing.stockToShoot = "back";
            }
        }
        else{
            printError(new Exception("row non puo essere null"),true);
        }
    }

    private void btnBackwards(){
        Routing.stockToShoot = "back";
    }

}
