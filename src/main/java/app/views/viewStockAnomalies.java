package app.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import app.Routing;
import app.classes.*;
import app.models.modelStock;
import app.objects.objStock;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
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
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

import static app.functions.*;

@SuppressWarnings("DuplicatedCode")
public class viewStockAnomalies implements Initializable {
    @FXML private TableView<modelStock> tableView;
    @FXML private TableColumn<modelStock, String> r;
    @FXML private TableColumn<modelStock, String> A;
    @FXML private TableColumn<modelStock, String> B;
    @FXML private TableColumn<modelStock, String> C;
    @FXML private TableColumn<modelStock, String> D;
    @FXML private TableColumn<modelStock, String> E;
    @FXML private TableColumn<modelStock, String> F;
    @FXML private TableColumn<modelStock, String> G;
    @FXML private TableColumn<modelStock, String> H;
    @FXML private TableColumn<modelStock, String> I;
    @FXML private TableColumn<modelStock, Void> deleteColumn;
    @FXML private HBox printPane;
    @FXML private Button btnForward;
    private final ArrayList<Integer> deletedRows = new ArrayList<>();
    @SuppressWarnings("deprecation")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Callback<TableColumn<modelStock, String>, TableCell<modelStock, String>> copyableReadOnlyCell = _ -> new TableCell<>() {
            final TextField tf = new TextField();
            {
                tf.setEditable(false);
                tf.setFocusTraversable(true);
                tf.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else { tf.setText(item); setGraphic(tf); }
            }
            @Override public void startEdit() {}
        };

        tableView.setEditable(true);
        addDeleteButtonToTable();
        btnForward.setOnAction(_ -> btnForward());
        printPane.setOnMouseClicked(_ -> printPane());
        r.setCellValueFactory(cellData -> cellData.getValue().row());
        A.setCellValueFactory(cellData -> cellData.getValue().A());
        B.setCellValueFactory(cellData -> cellData.getValue().B());
        C.setCellValueFactory(cellData -> cellData.getValue().C());
        D.setCellValueFactory(cellData -> cellData.getValue().D());
        E.setCellValueFactory(cellData -> cellData.getValue().E());
        F.setCellValueFactory(cellData -> cellData.getValue().F());
        G.setCellValueFactory(cellData -> cellData.getValue().G());
        H.setCellValueFactory(cellData -> cellData.getValue().H());
        I.setCellValueFactory(cellData -> cellData.getValue().I());

        A.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        B.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        A.setOnEditCommit(event -> {
            String newValue = event.getNewValue().toUpperCase();
            if(AllBlackFiles.barcodesFromFiles.contains(newValue)){
                modelStock etichetta = event.getRowValue();
                etichetta.A().set(newValue);
                alert("TROVATO","TROVATO");
            }
            else{
                alert("NON TROVATO","NON TROVATO, PRENDI UN ALTRO");
            }
        });
        B.setOnEditCommit(event -> {
            String newValue = event.getNewValue().toUpperCase();
            if(AllBlackFiles.barcodesFromFiles.contains(newValue)){
                modelStock etichetta = event.getRowValue();
                etichetta.B().set(newValue);
                alert("TROVATO","TROVATO");
            }
            else{
                alert("NON TROVATO","NON TROVATO, PRENDI UN ALTRO");
            }
        });

        C.setCellFactory(copyableReadOnlyCell);
        D.setCellFactory(copyableReadOnlyCell);
        E.setCellFactory(copyableReadOnlyCell);
        F.setCellFactory(copyableReadOnlyCell);
        G.setCellFactory(copyableReadOnlyCell);
        H.setCellFactory(copyableReadOnlyCell);
        I.setCellFactory(copyableReadOnlyCell);

        tableView.setItems(StockFile.stockAnomaliesFXCollections);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        centerAlignColumn(r);
        centerAlignColumn(C);
        centerAlignColumn(D);
        centerAlignColumn(E);
        centerAlignColumn(F);
        centerAlignColumn(G);
        centerAlignColumn(H);
    }

    @FXML private void btnForward() {
        for (modelStock item : tableView.getItems()) {
            Integer rowValue = Integer.valueOf(item.row().get());
            String firstBarcode = item.A().get();
            String lastBarcode = item.B().get();
            String reference = item.C().get();
            String obs = item.D().get();
            String cassetto = item.E().get();
            int indexFrom = Optional.ofNullable(AllBlackFiles.barcodeIndex(firstBarcode)).orElse(0) ;
            int indexTo = Optional.ofNullable(AllBlackFiles.barcodeIndex(lastBarcode)).orElse(0) ;
            if(indexFrom == 0 ){
                logError("ERROR",new Exception("Non trovato nei tiff:"+firstBarcode));
            }
            else if(indexTo == 0){
                logError("ERROR",new Exception("Non trovato nei tiff:"+lastBarcode));
            }
            else{
                int min = Math.min(indexFrom, indexTo);
                int max = Math.max(indexFrom, indexTo);
                objStock objStock = new objStock(rowValue,firstBarcode,lastBarcode,reference,obs,cassetto);
                objStock.extraFromJobSorter(JobSorter.barcodeGroup(firstBarcode), String.valueOf(min), String.valueOf(max));
                StockFile.rowObject.put(rowValue, objStock);
            }
        }
        for (Integer deletedRow : deletedRows) {
            StockFile.rowObject.remove(deletedRow);
        }

        Routing.stockAnomalies = "end";
    }

    @FXML private void printPane() {
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
                    gridPane.add(createPaneWithBorder(tableView.getColumns().get(3).getText(), border, 300), columnIndex++, rowIndex);
                    gridPane.add(createPaneWithBorder(tableView.getColumns().get(4).getText(), border, 130), columnIndex, rowIndex);
                    rowIndex++;
                }

                int startIndex = page * rowsPerPage;
                int endIndex = Math.min(startIndex + rowsPerPage, totalRows);

                for (int i = startIndex; i < endIndex; i++) {
                    modelStock item = tableView.getItems().get(i);
                    columnIndex = 0;
                    gridPane.add(createPaneWithBorder(item.C().get(), border, 300), columnIndex++, rowIndex);
                    gridPane.add(createPaneWithBorder(item.D().get(), border, 130), columnIndex, rowIndex);
                    rowIndex++;
                }

                if (page == 0) {
                    if (printerJob.showPrintDialog(null)) {
                        boolean printed = printerJob.printPage(gridPane);
                        if (printed) {
                            alert("STAMPA","STAMPA INVIATA");
                        } else {
                            success = false;
                            printError(new Exception("NON RIESCO A STAMPARE"),false);
                            break;
                        }
                    } else {
                        success = false;
                        printError(new Exception("NON RIESCO A STAMPARE"),false);
                        break;
                    }
                } else {
                    boolean printed = printerJob.printPage(gridPane);
                    if (!printed) {
                        success = false;
                        printError(new Exception("NON RIESCO A STAMPARE"),false);
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

    private void centerAlignColumn(TableColumn<modelStock, String> column) {
        column.setCellFactory(_ -> new TableCell<>() {
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
        });
    }

    private void addDeleteButtonToTable() {
        deleteColumn.setCellFactory(_ -> new TableCell<>() {
            private final HBox deleteButtonContainer = new HBox();
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button();

            {
                deleteButtonContainer.setAlignment(Pos.CENTER);
                deleteButtonContainer.getChildren().add(deleteButton);
                deleteButton.setStyle("-fx-background-color: transparent;");

                Image deleteIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/app/img/error.gif")));
                ImageView imageView = new ImageView(deleteIcon);
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
                deleteButton.setGraphic(imageView);

                deleteButton.setOnAction(_ -> {
                    modelStock currentItem = getTableView().getItems().get(getIndex());
                    deletedRows.add(Integer.valueOf(currentItem.row().get()));
                    tableView.getItems().remove(currentItem);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButtonContainer);
                }
            }
        });
    }

}
