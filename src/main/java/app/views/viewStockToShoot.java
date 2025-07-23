package app.views;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static app.functions.alert;
import static app.functions.printError;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN;

import app.Routing;
import app.classes.ValidTiffs;
import app.models.modelStockToShoot;
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

@SuppressWarnings("DuplicatedCode")
public class viewStockToShoot implements Initializable {
    @FXML private TableView<modelStockToShoot> tableView;
    @FXML private TableColumn<modelStockToShoot, String> box1;
    @FXML private TableColumn<modelStockToShoot, String> pallet1;
    @FXML private TableColumn<modelStockToShoot, String> box2;
    @FXML private TableColumn<modelStockToShoot, String> pallet2;
    @FXML private TableColumn<modelStockToShoot, Void> shoot1;
    @FXML private TableColumn<modelStockToShoot, Void> shoot2;
    @FXML private HBox printPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setEditable(true);
        addShootButtonToTable();
        printPane.setOnMouseClicked(_ -> printPane());
        box1.setCellValueFactory(cellData -> cellData.getValue().box1());
        pallet1.setCellValueFactory(cellData -> cellData.getValue().pallet1());
        box2.setCellValueFactory(cellData -> cellData.getValue().box2());
        pallet2.setCellValueFactory(cellData -> cellData.getValue().pallet2());

        tableView.setItems(ValidTiffs.modelStockToShootFXCollections);
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
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
                    gridPane.add(createPaneWithBorder("PRENDI QUESTO", border), columnIndex++, rowIndex);
                    gridPane.add(createPaneWithBorder("O QUESTO", border), columnIndex, rowIndex);
                    rowIndex++;
                }

                int startIndex = page * rowsPerPage;
                int endIndex = Math.min(startIndex + rowsPerPage, totalRows);

                for (int i = startIndex; i < endIndex; i++) {
                    modelStockToShoot item = tableView.getItems().get(i);
                    columnIndex = 0;
                    gridPane.add(createPaneWithBorder(item.box1().get()+": "+item.pallet1().get(), border), columnIndex++, rowIndex);
                    gridPane.add(createPaneWithBorder(item.box2().get()+": "+item.pallet2().get(), border), columnIndex, rowIndex);
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

    private StackPane createPaneWithBorder(String textContent, Border border) {
        Text text = new Text(textContent);
        StackPane pane = new StackPane(text);
        text.setWrappingWidth(215);
        pane.setBorder(border);
        pane.setPadding(new Insets(5));
        return pane;
    }

    private void addShootButtonToTable() {
        bindBtnShoot(shoot1,"row1");
        bindBtnShoot(shoot2,"row2");
    }

    private void bindBtnShoot(TableColumn<modelStockToShoot, Void> btn, String row){
        btn.setCellFactory(_ -> new TableCell<>() {
            private final HBox shootButtonContainer = new HBox();
            private final Button shootButton = new Button();
            {
                shootButtonContainer.setAlignment(Pos.CENTER);
                shootButtonContainer.getChildren().add(shootButton);
                shootButton.setStyle("-fx-background-color: transparent;");

                Image deleteIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/app/img/shoot.png")));
                ImageView imageView = new ImageView(deleteIcon);
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
                shootButton.setGraphic(imageView);

                shootButton.setOnAction(_ -> {
                    ValidTiffs.modelStockShootingFXCollections.clear();
                    int index = getIndex();
                    ValidTiffs.shootingIndex = index;
                    modelStockToShoot currentItem = getTableView().getItems().get(index);
                    ValidTiffs.modelStockShootingFXCollections.add(currentItem);
                    ValidTiffs.shootingAt = row;
                    Routing.stockToShoot = "shoot";
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
        });
    }

}
