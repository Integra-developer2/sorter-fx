package app;

import java.net.URL;
import java.util.ResourceBundle;

import static app.functions.load;
import static app.functions.writeStockFile;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
public class ctrlStockFile implements Initializable {
    @FXML
    private Button btnFoward;
    @FXML
    private TableView<modelStockFile> tableView;
    @FXML
    private TableColumn<modelStockFile, String> r;
    @FXML
    private TableColumn<modelStockFile, String> A;
    @FXML
    private TableColumn<modelStockFile, String> B;
    @FXML
    private TableColumn<modelStockFile, String> C;
    @FXML
    private TableColumn<modelStockFile, String> D;
    @FXML
    private TableColumn<modelStockFile, String> E;
    @SuppressWarnings("deprecation")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setEditable(true);
        btnFoward.setOnAction(_-> btnFoward());
        r.setCellValueFactory(cellData -> cellData.getValue().row());
        A.setCellValueFactory(cellData -> cellData.getValue().A());
        B.setCellValueFactory(cellData -> cellData.getValue().B());
        C.setCellValueFactory(cellData -> cellData.getValue().C());
        D.setCellValueFactory(cellData -> cellData.getValue().D());
        E.setCellValueFactory(cellData -> cellData.getValue().E());
        D.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        E.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        D.setOnEditCommit(event -> {
            String newValue = event.getNewValue().toUpperCase();
            modelStockFile row = event.getRowValue();
            row.D().set(newValue);
            tableView.refresh();
        });

        E.setOnEditCommit(event -> {
            String newValue = event.getNewValue().toUpperCase();
            modelStockFile row = event.getRowValue();
            row.E().set(newValue);
            tableView.refresh();
        });
        tableView.setItems(objAnomalies.stockFile);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        centerAlignColumn(r);
        centerAlignColumn(A);
        centerAlignColumn(B);
        centerAlignColumn(C);
    }

    private void centerAlignColumn(TableColumn<modelStockFile, String> column) {
        column.setCellFactory(_-> {
            TableCell<modelStockFile, String> cell = new TableCell<modelStockFile, String>() {
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

    @FXML
    private void btnFoward() {
        writeStockFile("log");
        load("viewStatusBar");
    }

}
