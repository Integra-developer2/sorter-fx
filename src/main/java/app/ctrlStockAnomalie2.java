package app;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import static app.functions.load;
import static app.functions.ls;
import static app.functions.moveStock2;
import static app.functions.alert;
import static app.functions.confirm;
import static app.functions.deleteFolder;
import static app.functions.printError;
import static app.functions.writeEtichette;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ctrlStockAnomalie2 implements Initializable {
    @FXML
    private TableView<modelEtichette2> tableView;
    @FXML
    private TableColumn<modelEtichette2, String> id;
    @FXML
    private TableColumn<modelEtichette2, String> box1;
    @FXML
    private TableColumn<modelEtichette2, String> pallet1;
    @FXML
    private TableColumn<modelEtichette2, String> box2;
    @FXML
    private TableColumn<modelEtichette2, String> pallet2;
    @FXML
    private TableColumn<modelEtichette2, Void> shoot1;
    @FXML
    private TableColumn<modelEtichette2, Void> shoot2;
    @FXML
    private HBox printPane;
    @FXML
    private Button btnFoward;
    public static ObservableList<modelEtichetteSoot> shootList = FXCollections.observableArrayList(); 
    public static ArrayList<String> findList = new ArrayList<>();
    public static Integer shootingRow;
    public static Integer shootingId;
    @SuppressWarnings("deprecation")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boolean start[] = {true};
        if(!objAnomalies.unExpectedGroups.isEmpty()){
            CountDownLatch latch = new CountDownLatch(1);
            try {
                try {
                    String message="CONFERMI CHE QUESTI COMUNI NON DEVONO ESSERE PREVISTI ?";
                    start[0] = confirm("ATENZIONE",message,objAnomalies.unExpectedGroups);
                }
                finally {
                    latch.countDown();
                }
                latch.await();
            } catch (InterruptedException e) {
                printError(e,false);
            }

            for (String group : objAnomalies.unExpectedGroups.keySet()) {
                moveStock2(group, objAnomalies.unExpectedGroups.get(group), "log");
            }

        }
        objAnomalies.unExpectedGroups.clear();
        if(objAnomalies.stock2.isEmpty()){
            writeEtichette();
            if(ls(objGlobals.anomalyFolderStock2, ".tiff").isEmpty()){
                deleteFolder(objGlobals.anomalyFolderStock2);
                objAnomalies.clear();
            }
            else{
                printError(new Exception(" ANOMALA ctrlStockAnomalie2: 94"),true);
            }
        }
        else if(start[0]){
            tableView.setEditable(true);
            addShootButtonToTable();
            btnFoward.setOnAction(event -> btnFoward());
            printPane.setOnMouseClicked(event -> printPane());
            id.setCellValueFactory(cellData -> cellData.getValue().id());
            box1.setCellValueFactory(cellData -> cellData.getValue().box1());
            pallet1.setCellValueFactory(cellData -> cellData.getValue().pallet1());
            box2.setCellValueFactory(cellData -> cellData.getValue().box2());
            pallet2.setCellValueFactory(cellData -> cellData.getValue().pallet2());

            tableView.setItems(objAnomalies.stock2);
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            centerAlignColumn(id);
            centerAlignColumn(box1);
            centerAlignColumn(pallet1);
            centerAlignColumn(box2);
            centerAlignColumn(pallet2);
        }
        else{
            objGlobals.stop=true;
            alert("NON POSSO CONTINUARE","SISTEMA IL FILE ETICHETTE AGGIUNGENDO I COMUNI MANCANTI E RIAPRI IL PROGRAMA");
        }

    }

    @FXML
    private void btnFoward() {
        boolean start[] = {true};
        CountDownLatch latch = new CountDownLatch(1);
        try {
            try {
                start[0] = confirm("ATENZIONE","CONFERMI DI VOLER IGNORARE QUESTE ANOMALIE ?");
            }
            finally {
                latch.countDown();
            }
            latch.await();
        } catch (InterruptedException e) {
            printError(e,false);
        }

        if(start[0]){
            for (modelEtichette2 obj : objAnomalies.stock2) {
                moveStock2(obj.group(), obj.fileList(), "log");
            }
            objAnomalies.clear();
            writeEtichette();
            objGlobals.skipAnomalies=true;
            load("viewStatusBar");
        }
    }

    @FXML
    private void printPane() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null) {
            double pageHeight = 800;
            double rowHeight = 40;
            double padding = 10;
            int rowsPerPage = (int) ((pageHeight - 2 * padding) / rowHeight);
            int totalRows = tableView.getItems().size();
            int totalPages = (int) Math.ceil((double) totalRows / rowsPerPage);
            boolean success = true;
            for (int page = 0; page < totalPages; page++) {
                GridPane gridPane = new GridPane();
                gridPane.setPadding(new Insets(padding));
                gridPane.setHgap(5);
                gridPane.setVgap(5);
                BorderStroke borderStroke = new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, null, new BorderWidths(1));
                Border border = new Border(borderStroke);

                int columnIndex = 0;
                int rowIndex = 0;

                if (page == 0) {
                    gridPane.add(createPaneWithBorder("PRENDI QUESTO", border, 215), columnIndex++, rowIndex);
                    gridPane.add(createPaneWithBorder("O QUESTO", border, 215), columnIndex++, rowIndex);
                    rowIndex++;
                }

                int startIndex = page * rowsPerPage;
                int endIndex = Math.min(startIndex + rowsPerPage, totalRows);

                for (int i = startIndex; i < endIndex; i++) {
                    modelEtichette2 item = tableView.getItems().get(i);
                    columnIndex = 0;
                    gridPane.add(createPaneWithBorder(item.box1().get()+": "+item.pallet1().get(), border, 215), columnIndex++, rowIndex);
                    gridPane.add(createPaneWithBorder(item.box2().get()+": "+item.pallet2().get(), border, 215), columnIndex++, rowIndex);
                    rowIndex++;
                }

                if (page == 0) {
                    if (printerJob.showPrintDialog(null)) {
                        boolean printed = printerJob.printPage(gridPane);
                        if (printed) {
                            success = true;
                            alert("STAMPA","STAMPA INVIATA");
                        } else {
                            success = false;
                            printError(new Exception("NON RIESCO A STAMPARE"),false);
                            break;
                        }
                    } else {
                        success = false;
                        printError(new Exception("Non riesco a stampare"),false);
                        break;
                    }
                } else {
                    boolean printed = printerJob.printPage(gridPane);
                    if (!printed) {
                        success = false;
                        printError(new Exception("Non riesco a stampare"),false);
                        break;
                    }
                }
            }
            if (success) {
                printerJob.endJob();
            }
        }
    }

    private StackPane createPaneWithBorder(String textContent, Border border, double maxWidth) {
        Text text = new Text(textContent);
        StackPane pane = new StackPane(text);
        text.setWrappingWidth(maxWidth);
        pane.setBorder(border);
        pane.setPadding(new Insets(5));
        return pane;
    }

    private void centerAlignColumn(TableColumn<modelEtichette2, String> column) {
        column.setCellFactory(tc -> {
            TableCell<modelEtichette2, String> cell = new TableCell<modelEtichette2, String>() {
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

    private void addShootButtonToTable() {
        shoot1.setCellFactory(param -> {
            return new TableCell<modelEtichette2, Void>() {
                private final HBox shootButtonContainer = new HBox();
                private final javafx.scene.control.Button shootButton = new javafx.scene.control.Button();
                {
                    shootButtonContainer.setAlignment(Pos.CENTER);
                    shootButtonContainer.getChildren().add(shootButton);
                    shootButton.setStyle("-fx-background-color: transparent;");

                    Image deleteIcon = new Image(getClass().getResourceAsStream("/app/img/shoot.png"));
                    ImageView imageView = new ImageView(deleteIcon);
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
                    shootButton.setGraphic(imageView);

                    shootButton.setOnAction(event -> {
                        modelEtichette2 currentItem = getTableView().getItems().get(getIndex());
                        shootList.clear();
                        for (String barcode : currentItem.barcodeList()) {
                            shootList.add(new modelEtichetteSoot(barcode));
                            findList.add(barcode);
                        }
                        shootingRow=currentItem.row1();
                        shootingId=currentItem.intId();
                        load("viewStockAnomalie2Shoot");
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(shootButtonContainer);
                    }
                }
            };
        });

        shoot2.setCellFactory(param ->  {
            return new TableCell<modelEtichette2, Void>() {
                private final HBox shootButtonContainer = new HBox();
                private final javafx.scene.control.Button shootButton = new javafx.scene.control.Button();
                {
                    shootButtonContainer.setAlignment(Pos.CENTER);
                    shootButtonContainer.getChildren().add(shootButton);
                    shootButton.setStyle("-fx-background-color: transparent;");

                    Image deleteIcon = new Image(getClass().getResourceAsStream("/app/img/shoot.png"));
                    ImageView imageView = new ImageView(deleteIcon);
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
                    shootButton.setGraphic(imageView);

                    shootButton.setOnAction(event -> {
                        modelEtichette2 currentItem = getTableView().getItems().get(getIndex());
                        shootList.clear();
                        for (String barcode : currentItem.barcodeList()) {
                            shootList.add(new modelEtichetteSoot(barcode));
                            findList.add(barcode);
                        }
                        shootingRow=currentItem.row2();
                        shootingId=currentItem.intId();
                        load("viewStockAnomalie2Shoot");
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(shootButtonContainer);
                    }
                }
            };
        });
    }

}
