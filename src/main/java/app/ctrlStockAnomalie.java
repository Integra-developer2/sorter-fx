package app;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
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
import javafx.util.converter.DefaultStringConverter;

import static app.functions.alert;
import static app.functions.load;
import static app.functions.printError;
import static app.functions.writeEtichette;
import static app.o3_sorter_stock.functions.getGroup;
import static app.o3_sorter_stock.functions.indexMax;
import static app.o3_sorter_stock.functions.indexMin;
public class ctrlStockAnomalie implements Initializable {
    @FXML
    private TableView<modelEtichette> tableView;
    @FXML
    private TableColumn<modelEtichette, String> r;
    @FXML
    private TableColumn<modelEtichette, String> A;
    @FXML
    private TableColumn<modelEtichette, String> B;
    @FXML
    private TableColumn<modelEtichette, String> C;
    @FXML
    private TableColumn<modelEtichette, String> D;
    @FXML
    private TableColumn<modelEtichette, String> E;
    @FXML
    private TableColumn<modelEtichette, Void> deleteColumn;
    @FXML
    private HBox printPane;
    @FXML
    private Button btnFoward;
    private final ArrayList<Integer> deletedRows = new ArrayList<>();
    @SuppressWarnings("deprecation")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setEditable(true);
        addDeleteButtonToTable();
        btnFoward.setOnAction(event -> btnFoward());
        printPane.setOnMouseClicked(event -> printPane());
        r.setCellValueFactory(cellData -> cellData.getValue().row());
        A.setCellValueFactory(cellData -> cellData.getValue().A());
        B.setCellValueFactory(cellData -> cellData.getValue().B());
        C.setCellValueFactory(cellData -> cellData.getValue().C());
        D.setCellValueFactory(cellData -> cellData.getValue().D());
        E.setCellValueFactory(cellData -> cellData.getValue().E());
        A.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        B.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        A.setOnEditCommit(event -> {
            String newValue = event.getNewValue().toUpperCase();
            if(objGlobals.barcodesFromFiles.contains(newValue)){
                modelEtichette etichetta = event.getRowValue();
                etichetta.A().set(newValue);
                alert("TROVATO","TROVATO, VAI AVVANTI");
            }
            else{
                alert("NON TROVATO","NON TROVATO, PRENDI UN'ALTRO");
            }
        });
        B.setOnEditCommit(event -> {
            String newValue = event.getNewValue().toUpperCase();
            if(objGlobals.barcodesFromFiles.contains(newValue)){
                modelEtichette etichetta = event.getRowValue();
                etichetta.B().set(newValue);
                alert("TROVATO","TROVATO, VAI AVVANTI");
            }
            else{
                alert("NON TROVATO","NON TROVATO, PRENDI UN'ALTRO");
            }
        });

        tableView.setItems(objAnomalies.stock);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        centerAlignColumn(r);
        centerAlignColumn(C);
        centerAlignColumn(D);
        centerAlignColumn(E);
    }

    @FXML
    private void btnFoward() {
        for (modelEtichette item : tableView.getItems()) {
            Integer rowValue = Integer.valueOf(item.row().get());
            String firstBarcode = item.A().get();
            String lastBarcode = item.B().get();
            String reference = item.C().get();
            String obs = item.D().get();
            int indexFrom = indexMin(firstBarcode);
            int indexTo = indexMax(lastBarcode);
            int min = (indexFrom<indexTo)?indexFrom:indexTo;
            int max = (indexFrom>indexTo)?indexFrom:indexTo;
            objFileEtichette objFileEtichette = new objFileEtichette(rowValue,firstBarcode,lastBarcode,reference,obs);
            objFileEtichette.extra(getGroup(firstBarcode), min, max);
            objGlobals.fileEtichette.put(rowValue,objFileEtichette);
        }
        for (Integer deletedRow : deletedRows) {
            objGlobals.fileEtichette.remove(deletedRow);
        }
        writeEtichette();
        load("viewStatusBar");
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
                    gridPane.add(createPaneWithBorder(tableView.getColumns().get(3).getText(), border, 300), columnIndex++, rowIndex);
                    gridPane.add(createPaneWithBorder(tableView.getColumns().get(4).getText(), border, 130), columnIndex++, rowIndex);
                    rowIndex++;
                }

                int startIndex = page * rowsPerPage;
                int endIndex = Math.min(startIndex + rowsPerPage, totalRows);

                for (int i = startIndex; i < endIndex; i++) {
                    modelEtichette item = tableView.getItems().get(i);
                    columnIndex = 0;
                    gridPane.add(createPaneWithBorder(item.C().get(), border, 300), columnIndex++, rowIndex);
                    gridPane.add(createPaneWithBorder(item.D().get(), border, 130), columnIndex++, rowIndex);
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
    
    private void centerAlignColumn(TableColumn<modelEtichette, String> column) {
        column.setCellFactory(tc -> {
            TableCell<modelEtichette, String> cell = new TableCell<modelEtichette, String>() {
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

    private void addDeleteButtonToTable() {
        deleteColumn.setCellFactory(param -> new TableCell<modelEtichette, Void>() {
            private final HBox deleteButtonContainer = new HBox();
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button();
            {
                deleteButtonContainer.setAlignment(Pos.CENTER);
                deleteButtonContainer.getChildren().add(deleteButton);
                deleteButton.setStyle("-fx-background-color: transparent;");

                Image deleteIcon = new Image(getClass().getResourceAsStream("/app/img/error.gif"));
                ImageView imageView = new ImageView(deleteIcon);
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
                deleteButton.setGraphic(imageView);

                deleteButton.setOnAction(event -> {
                    modelEtichette currentItem = getTableView().getItems().get(getIndex());
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
