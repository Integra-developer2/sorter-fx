package app.views;

import app.Routing;
import app.classes.StockFile;
import app.classes.ValidTiffs;
import app.models.modelStockFile;
import app.objects.objStock;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.converter.DefaultStringConverter;
import javafx.scene.control.TableRow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import java.net.URL;
import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class viewStockFile implements Initializable {
    private static final double DND_SCROLL_MARGIN = 28;

    @FXML private TableView<modelStockFile> tableView;
    @FXML private TableColumn<modelStockFile, String> firstBarcode;
    @FXML private TableColumn<modelStockFile, String> lastBarcode;
    @FXML private TableColumn<modelStockFile, String> stockLabel;
    @FXML private TableColumn<modelStockFile, String> obs;
    @FXML private TableColumn<modelStockFile, String> group;
    @FXML private TableColumn<modelStockFile, String> progStart;
    @FXML private TableColumn<modelStockFile, String> progEnd;
    @FXML private TableColumn<modelStockFile, String> logic;
    @FXML private TableColumn<modelStockFile, String> prefix;
    @FXML private TableColumn<modelStockFile, String> stockNumber;
    @FXML private TableColumn<modelStockFile, String> agency;
    @FXML private TableColumn<modelStockFile, String> cassetto;
    @FXML private TableColumn<modelStockFile, String> pacco;
    @FXML private TableColumn<modelStockFile, String> error;
    @FXML private TableColumn<modelStockFile, Void> deleteColumn;
    private final ArrayList<Integer> deletedRows = new ArrayList<>();
    @FXML private Button btnForward;
    public String colorDefault = "group-color-1";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StockFile.writeNewFile();
        addDeleteButtonToTable();
        btnForward.setOnAction(_ -> btnForward());

        firstBarcode.setCellValueFactory(cellData -> cellData.getValue().firstBarcode());
        lastBarcode.setCellValueFactory(cellData -> cellData.getValue().lastBarcode());
        stockLabel.setCellValueFactory(cellData -> cellData.getValue().stockLabel());
        obs.setCellValueFactory(cellData -> cellData.getValue().obs());
        group.setCellValueFactory(cellData -> cellData.getValue().group());
        progStart.setCellValueFactory(cellData -> cellData.getValue().progStart());
        progEnd.setCellValueFactory(cellData -> cellData.getValue().progEnd());
        logic.setCellValueFactory(cellData -> cellData.getValue().logic());
        prefix.setCellValueFactory(cellData -> cellData.getValue().prefix());
        stockNumber.setCellValueFactory(cellData -> cellData.getValue().stockNumber());
        agency.setCellValueFactory(cellData -> cellData.getValue().agency());
        cassetto.setCellValueFactory(cellData -> cellData.getValue().cassetto());
        pacco.setCellValueFactory(cellData -> cellData.getValue().pacco());
        error.setCellValueFactory(cellData -> cellData.getValue().error());

        firstBarcode.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        lastBarcode.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        stockLabel.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        obs.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        group.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        progStart.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        progEnd.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        logic.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        prefix.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        stockNumber.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        agency.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        cassetto.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        pacco.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        error.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        toggleStripedTableView();
        colorDefault = "group-color-1";

        tableView.setEditable(true);
        tableView.setItems(StockFile.stockFileFXCollections);
        tableView.getSelectionModel().clearSelection();

    }

    @FXML private void btnForward() {
        for(Integer row:deletedRows){
            StockFile.rowObject.remove(row);
        }

        for(modelStockFile modelStockFile : StockFile.stockFileFXCollections){
            int row = Integer.parseInt(modelStockFile.row.get());
            objStock objStock = new objStock(
                    row,
                    modelStockFile.firstBarcode.get(),
                    modelStockFile.lastBarcode.get(),
                    modelStockFile.stockLabel.get(),
                    modelStockFile.obs.get(),
                    modelStockFile.cassetto.get(),
                    modelStockFile.pacco.get(),
                    modelStockFile.group.get(),
                    modelStockFile.progStart.get(),
                    modelStockFile.progEnd.get(),
                    modelStockFile.logic.get(),
                    modelStockFile.prefix.get(),
                    modelStockFile.stockNumber.get(),
                    modelStockFile.agency.get(),
                    modelStockFile.agencyID.get(),
                    modelStockFile.cppCode.get(),
                    modelStockFile.customer.get()
            );
            StockFile.rowObject.put(row,objStock);
        }
        Routing.stockNumber="end";
    }

    private void toggleStripedTableView() {
        tableView.setRowFactory(tv -> {
            TableRow<modelStockFile> row = new TableRow<>() {
                @Override
                protected void updateItem(modelStockFile item, boolean empty) {
                    super.updateItem(item, empty);
                    getStyleClass().removeAll("group-color-1", "group-color-2");
                    if (empty || item == null) return;
                    int index = getIndex();
                    ObservableList<modelStockFile> items = tv.getItems();
                    boolean toggle = false;
                    if (index > 0 && index < items.size()) {
                        String currentE = item.group().get();
                        String prevE = items.get(index - 1).group().get();
                        if (!currentE.equals(prevE)) {
                            toggle = true;
                        }
                    }
                    if (toggle){
                        colorDefault = colorDefault.equals("group-color-1") ? "group-color-2" : "group-color-1";
                    }
                    getStyleClass().add(colorDefault);
                }
            };

            row.setOnDragDetected(e -> {
                if (!row.isEmpty()) {
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(Integer.toString(row.getIndex()));
                    db.setContent(content);
                    e.consume();
                }
            });

            row.setOnDragOver(e -> {
                Dragboard db = e.getDragboard();
                if (db.hasString() && !row.isEmpty() && row.getIndex() != Integer.parseInt(db.getString())) {
                    e.acceptTransferModes(TransferMode.MOVE);
                    tableView.getFocusModel().focus(row.getIndex());
                    autoScrollRow(row, e.getSceneY());
                    e.consume();
                }
            });

            row.setOnDragDropped(e -> {
                Dragboard db = e.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    int draggedIndex = Integer.parseInt(db.getString());
                    modelStockFile draggedItem = tableView.getItems().remove(draggedIndex);
                    int dropIndex = row.isEmpty() ? tableView.getItems().size() : row.getIndex();
                    if (dropIndex > draggedIndex) dropIndex--;
                    tableView.getItems().add(dropIndex, draggedItem);
                    tableView.getSelectionModel().select(dropIndex);
                    modelStockFile prev = dropIndex - 1 >= 0 ? tableView.getItems().get(dropIndex - 1) : null;
                    modelStockFile curr = tableView.getItems().get(dropIndex);
                    modelStockFile next = dropIndex + 1 < tableView.getItems().size() ? tableView.getItems().get(dropIndex + 1) : null;

                    String group = curr.group.get();

                    setStart(prev, curr, group);

                    setEnd(next, curr, group);

                    success = true;
                }
                e.setDropCompleted(success);
                e.consume();
            });

            return row;
        });

        tableView.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
                autoScrollTable(e.getSceneY());
                e.consume();
            }
        });

        tableView.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                int draggedIndex = Integer.parseInt(db.getString());
                modelStockFile draggedItem = tableView.getItems().remove(draggedIndex);
                int dropIndex = tableView.getItems().size();
                tableView.getItems().add(dropIndex, draggedItem);
                tableView.getSelectionModel().select(dropIndex);
                modelStockFile prev = dropIndex - 1 >= 0 ? tableView.getItems().get(dropIndex - 1) : null;
                modelStockFile curr = tableView.getItems().get(dropIndex);
                modelStockFile next = dropIndex + 1 < tableView.getItems().size() ? tableView.getItems().get(dropIndex + 1) : null;
                String prevP = prev != null ? prev.progStart().get() : "null";
                String currP = curr != null ? curr.progStart().get() : "null";
                String nextP = next != null ? next.progStart().get() : "null";
                System.out.println(prevP + " | " + currP + " | " + nextP);
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
        });
    }

    private void setStart(modelStockFile prev, modelStockFile curr, String group){

        int startingFrom = 1001;

        if (prev != null) {
            startingFrom = Integer.parseInt(prev.progEnd.get()) + 1;
        }

        int progStart = startingFrom;

        for(int i = startingFrom; i <= Integer.parseInt(curr.progEnd.get()); i++){

            if(ValidTiffs.groupIndexObject.containsKey(group+"-"+i)){
                curr.firstBarcode.set(ValidTiffs.groupIndexObject.get(group+"-"+i).barcode);
                progStart = i;
                break;
            }

        }

        curr.progStart.set(Integer.toString(progStart));
    }

    private void setEnd(modelStockFile next, modelStockFile curr, String group){

        if (next != null) {
            int startingFrom = Integer.parseInt(next.progStart.get()) - 1;

            int progEnd = 0;

            for(int i = startingFrom; i >= Integer.parseInt(curr.progStart.get()); i--){

                if(ValidTiffs.groupIndexObject.containsKey(group+"-"+i)){
                    curr.lastBarcode.set(ValidTiffs.groupIndexObject.get(group+"-"+i).barcode);
                    progEnd = i;
                    break;
                }

            }

            if(progEnd>0){
                curr.progEnd.set(Integer.toString(progEnd));
            }
            else{
                curr.progEnd.set(Integer.toString(startingFrom));
            }

        }
    }

    private void autoScrollRow(TableRow<modelStockFile> row, double sceneY) {
        Bounds b = tableView.localToScene(tableView.getBoundsInLocal());
        if (sceneY > b.getMaxY() - DND_SCROLL_MARGIN) {
            int base = Math.max(tableView.getFocusModel().getFocusedIndex(), row.getIndex());
            tableView.scrollTo(Math.min(base + 1, tableView.getItems().size() - 1));
        } else if (sceneY < b.getMinY() + DND_SCROLL_MARGIN) {
            int base = Math.min(tableView.getFocusModel().getFocusedIndex(), row.getIndex());
            tableView.scrollTo(Math.max(base - 1, 0));
        }
    }

    private void autoScrollTable(double sceneY) {
        Bounds b = tableView.localToScene(tableView.getBoundsInLocal());
        int base = Math.max(0, tableView.getFocusModel().getFocusedIndex());
        if (sceneY > b.getMaxY() - DND_SCROLL_MARGIN) {
            tableView.scrollTo(Math.min(base + 1, tableView.getItems().size() - 1));
        } else if (sceneY < b.getMinY() + DND_SCROLL_MARGIN) {
            tableView.scrollTo(Math.max(base - 1, 0));
        }
    }

    private void addDeleteButtonToTable() {
        deleteColumn.setCellFactory(_ -> new TableCell<>() {
            private final HBox deleteButtonContainer = new HBox();
            private final Button deleteButton = new Button();

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
                    modelStockFile currentItem = getTableView().getItems().get(getIndex());
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
