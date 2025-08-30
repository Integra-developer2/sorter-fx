package app.views;

import app.Routing;
import app.classes.StockFile;
import app.classes.ValidTiffs;
import app.models.modelStockAnomalies;
import app.models.modelStockNumber;
import app.objects.objStock;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.util.converter.DefaultStringConverter;

import java.net.URL;
import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class viewStockNumber implements Initializable {

    @FXML private TableView<modelStockAnomalies> tableView2;
    @FXML private TableColumn<modelStockAnomalies, String> A1;
    @FXML private TableColumn<modelStockAnomalies, String> B1;
    @FXML private TableColumn<modelStockAnomalies, String> C1;
    @FXML private TableColumn<modelStockAnomalies, String> D1;
    @FXML private TableColumn<modelStockAnomalies, String> E1;
    @FXML private TableColumn<modelStockAnomalies, String> F1;
    @FXML private TableColumn<modelStockAnomalies, String> G1;


    @FXML private TableView<modelStockNumber> tableView;
    @FXML private TableColumn<modelStockNumber, String> r;
    @FXML private TableColumn<modelStockNumber, String> A;
    @FXML private TableColumn<modelStockNumber, String> B;
    @FXML private TableColumn<modelStockNumber, String> C;
    @FXML private TableColumn<modelStockNumber, String> D;
    @FXML private TableColumn<modelStockNumber, String> E;
    @FXML private TableColumn<modelStockNumber, String> F;
    @FXML private TableColumn<modelStockNumber, String> G;
    @FXML private TableColumn<modelStockNumber, String> H;
    @FXML private TableColumn<modelStockNumber, String> I;
    @FXML private TableColumn<modelStockNumber, String> J;
    @FXML private TableColumn<modelStockNumber, String> K;
    @FXML private TableColumn<modelStockNumber, String> L;
    @FXML private TableColumn<modelStockNumber, Void> deleteColumn;
    @FXML private Button btnForward;
    public String colorDefault = "group-color-1";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addDeleteButtonToTable();
        btnForward.setOnAction(_ -> btnForward());

        A1.setCellValueFactory(cellData -> cellData.getValue().A1());
        B1.setCellValueFactory(cellData -> cellData.getValue().B1());
        C1.setCellValueFactory(cellData -> cellData.getValue().C1());
        D1.setCellValueFactory(cellData -> cellData.getValue().D1());
        E1.setCellValueFactory(cellData -> cellData.getValue().E1());
        F1.setCellValueFactory(cellData -> cellData.getValue().F1());
        G1.setCellValueFactory(cellData -> cellData.getValue().G1());

        A1.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        B1.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        C1.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        D1.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        E1.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        F1.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        G1.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

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
        J.setCellValueFactory(cellData -> cellData.getValue().J());
        K.setCellValueFactory(cellData -> cellData.getValue().K());
        L.setCellValueFactory(cellData -> cellData.getValue().L());

        r.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        A.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        B.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        C.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        D.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        E.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        setupTableView();
        setupTableView2();
        toggleStripedTableView();
        colorDefault = "group-color-1";
        toggleStripedTableView2();

        tableView.getSelectionModel().clearSelection();
        tableView2.getSelectionModel().clearSelection();
    }

    @FXML private void btnForward() {
        int count = 0;
        StockFile.rowObject.clear();
        for(modelStockNumber modelStockNumber : StockFile.stockNumberFXCollections){
            StockFile.rowObject.put(++count,new objStock(
                count,
                modelStockNumber.A().get(),
                modelStockNumber.B().get(),
                modelStockNumber.C().get(),
                modelStockNumber.D().get(),
                modelStockNumber.E().get(),
                modelStockNumber.F().get(),
                modelStockNumber.G().get(),
                modelStockNumber.H().get(),
                modelStockNumber.I().get(),
                modelStockNumber.J().get(),
                modelStockNumber.K().get(),
                modelStockNumber.L().get(),
                "",
                "",
                ""
            ));
        }

        Routing.stockNumber="end";
    }

    private void addDeleteButtonToTable() {
        deleteColumn.setCellFactory(_ -> new TableCell<>() {
            private final HBox delContainer = new HBox();
            private final Button deleteButton = new Button();

            {
                delContainer.setAlignment(Pos.CENTER);
                delContainer.getChildren().add(deleteButton);
                deleteButton.setStyle("-fx-background-color: transparent;");
                Image delIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/app/img/error.gif")));
                ImageView imgView = new ImageView(delIcon);
                imgView.setFitWidth(20);
                imgView.setFitHeight(20);
                deleteButton.setGraphic(imgView);
                deleteButton.setOnAction(_ -> {
                    modelStockNumber currentItem = getTableView().getItems().get(getIndex());
                    tableView.getItems().remove(currentItem);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(delContainer);
                }
            }
        });
    }

    private void setupTableView() {
        tableView.setEditable(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setItems(StockFile.stockNumberFXCollections);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        E.setSortType(TableColumn.SortType.ASCENDING);
        F.setSortType(TableColumn.SortType.ASCENDING);
        Collections.addAll(tableView.getSortOrder(), E, F);
        tableView.getSelectionModel().clearSelection();
        tableView.sort();

        tableView.setOnDragOver(event -> {
            if (event.getGestureSource() != tableView && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        tableView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString() && "STOCK_ANOMALIES_DRAG".equals(db.getString())) {
                ObservableList<modelStockAnomalies> selected = tableView2.getSelectionModel().getSelectedItems();
                String firstBarcode = "";
                String lastBarcode = "";
                String reference = "";
                String obs = "";
                String cassetto = "";
                String group = "";
                String progStart = "";
                String progEnd = "";
                String logicStart = "";
                String prefixStart = "";
                String stockNumber = "";
                String agency = "";
                for(modelStockAnomalies modelStockAnomalies: selected){
                    if(firstBarcode.isEmpty()){
                        firstBarcode = modelStockAnomalies.C1().get();
                        progStart = modelStockAnomalies.A1().get();
                    }
                    if(group.isEmpty()){
                        group = modelStockAnomalies.B1().get();
                    }
                    lastBarcode = modelStockAnomalies.C1().get();
                    progEnd = modelStockAnomalies.A1().get();

                }
                StockFile.stockNumberFXCollections.add(new modelStockNumber(new objStock(
                    0,
                    firstBarcode,
                    lastBarcode,
                    reference,
                    obs,
                    cassetto,
                    group,
                    progStart,
                    progEnd,
                    logicStart,
                    prefixStart,
                    stockNumber,
                    agency,
                    "",
                    "",
                    ""
                )));
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void setupTableView2() {

        tableView2.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView2.setItems(ValidTiffs.stockAnomaliesFXCollections);
        tableView2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        tableView2.setOnDragDetected(event -> {
            ObservableList<modelStockAnomalies> selectedItems = tableView2.getSelectionModel().getSelectedItems();
            if (!selectedItems.isEmpty()) {
                Dragboard db = tableView2.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString("STOCK_ANOMALIES_DRAG");
                db.setContent(content);
                event.consume();
            }
        });

        B1.setSortType(TableColumn.SortType.ASCENDING);
        A1.setSortType(TableColumn.SortType.ASCENDING);
        Collections.addAll(tableView2.getSortOrder(), B1, A1);
        tableView2.getSelectionModel().clearSelection();
        tableView2.sort();
    }

    private void toggleStripedTableView() {
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(modelStockNumber item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().removeAll("group-color-1", "group-color-2");

                if (empty || item == null) return;

                int index = getIndex();
                ObservableList<modelStockNumber> items = tv.getItems();

                boolean toggle = false;
                if (index > 0 && index < items.size()) {
                    String currentE = item.E().get();
                    String prevE = items.get(index - 1).E().get();

                    if (!currentE.equals(prevE)) {
                        toggle = true;
                    }
                }

                if (toggle) {
                    colorDefault = colorDefault.equals("group-color-1") ? "group-color-2" : "group-color-1";
                }

                getStyleClass().add(colorDefault);
            }
        });
    }

    private void toggleStripedTableView2() {
        tableView2.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(modelStockAnomalies item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().removeAll("group-color-1", "group-color-2");

                if (empty || item == null) return;

                int index = getIndex();
                ObservableList<modelStockAnomalies> items = tv.getItems();

                boolean toggle = false;
                if (index > 0 && index < items.size()) {
                    String currentE = item.B1().get();
                    String prevE = items.get(index - 1).B1().get();

                    if (!currentE.equals(prevE)) {
                        toggle = true;
                    }
                }

                if (toggle) {
                    colorDefault = colorDefault.equals("group-color-1") ? "group-color-2" : "group-color-1";
                }

                getStyleClass().add(colorDefault);
            }
        });
    }


}
