package app.views;

import app.classes.Api;
import app.objects.objGlobals;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class viewMain {

    @FXML public Label titleLabel;
    @FXML private AnchorPane contentPane;
    @FXML private VBox titleBar;
    @FXML private Button btnMinimize;
    @FXML private Button btnMaximize;
    @FXML private Button btnClose;
    @FXML private AnchorPane rootPane;
    @FXML private VBox terminalPanel;
    @FXML private Button btnToggleTerminal;
    @FXML private Button btnPauseTerminal;
    @FXML private TextArea terminalOutput;
    @FXML public RadioButton dev;
    @FXML public RadioButton preprod;
    @FXML public RadioButton prod;

    @FXML public RadioButton stepChoice;
    @FXML public RadioButton moveFiles;
    @FXML public RadioButton gray;
    @FXML public RadioButton grayAnomalies;
    @FXML public RadioButton stockAnomalies;
    @FXML public RadioButton stockNumber;
    @FXML public RadioButton stockToShoot;
    @FXML public RadioButton pdf;

    private double xOffset, yOffset = 0;
    private boolean isMaximized = false;
    private double prevW, prevH, prevX, prevY;
    private boolean terminalVisible = false;


    @FXML public void initialize() {
        Platform.runLater(() -> {
            Stage stage = getStage();
            stage.setMinWidth(300);
            stage.setMinHeight(100);

            titleBar.setOnMousePressed(e -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });

            titleBar.setOnMouseDragged(e -> {
                if (!isMaximized) {
                    stage.setX(e.getScreenX() - xOffset);
                    stage.setY(e.getScreenY() - yOffset);
                }
            });

            btnClose.setOnAction(_ -> stage.close());

            btnMinimize.setOnAction(_ -> stage.setIconified(true));

            btnMaximize.setOnAction(_ -> {
                if (isMaximized) {
                    stage.setX(prevX);
                    stage.setY(prevY);
                    stage.setWidth(prevW);
                    stage.setHeight(prevH);
                    isMaximized = false;
                } else {
                    prevX = stage.getX();
                    prevY = stage.getY();
                    prevW = stage.getWidth();
                    prevH = stage.getHeight();

                    var bounds = Screen.getPrimary().getVisualBounds();
                    stage.setX(bounds.getMinX());
                    stage.setY(bounds.getMinY());
                    stage.setWidth(bounds.getWidth());
                    stage.setHeight(bounds.getHeight());
                    isMaximized = true;
                }
            });

            final int RESIZE_MARGIN = 6;

            titleBar.getScene().setOnMouseMoved(event -> {
                double mouseX = event.getSceneX();
                double mouseY = event.getSceneY();
                double width = stage.getWidth();
                double height = stage.getHeight();

                boolean resizeRight = mouseX > width - RESIZE_MARGIN;
                boolean resizeBottom = mouseY > height - RESIZE_MARGIN;

                if (resizeRight && resizeBottom) {
                    titleBar.getScene().setCursor(javafx.scene.Cursor.NW_RESIZE);
                } else if (resizeRight) {
                    titleBar.getScene().setCursor(javafx.scene.Cursor.H_RESIZE);
                } else if (resizeBottom) {
                    titleBar.getScene().setCursor(javafx.scene.Cursor.V_RESIZE);
                } else {
                    titleBar.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
                }
            });

            titleBar.getScene().setOnMouseDragged(event -> {
                double mouseX = event.getSceneX();
                double mouseY = event.getSceneY();

                if (titleBar.getScene().getCursor() == javafx.scene.Cursor.H_RESIZE) {
                    stage.setWidth(mouseX);
                } else if (titleBar.getScene().getCursor() == javafx.scene.Cursor.V_RESIZE) {
                    stage.setHeight(mouseY);
                } else if (titleBar.getScene().getCursor() == javafx.scene.Cursor.NW_RESIZE) {
                    stage.setWidth(mouseX);
                    stage.setHeight(mouseY);
                }
            });

            rootPane.setOnMouseMoved(event -> {
                double mouseX = event.getX();
                double mouseY = event.getY();
                double width = stage.getWidth();
                double height = stage.getHeight();

                Cursor cursorType = Cursor.DEFAULT;

                boolean left = mouseX < RESIZE_MARGIN;
                boolean right = mouseX > width - RESIZE_MARGIN;
                boolean top = mouseY < RESIZE_MARGIN;
                boolean bottom = mouseY > height - RESIZE_MARGIN;

                if (left && top) cursorType = Cursor.NW_RESIZE;
                else if (left && bottom) cursorType = Cursor.SW_RESIZE;
                else if (right && top) cursorType = Cursor.NE_RESIZE;
                else if (right && bottom) cursorType = Cursor.SE_RESIZE;
                else if (left) cursorType = Cursor.W_RESIZE;
                else if (right) cursorType = Cursor.E_RESIZE;
                else if (top) cursorType = Cursor.N_RESIZE;
                else if (bottom) cursorType = Cursor.S_RESIZE;

                rootPane.setCursor(cursorType);
            });

            rootPane.setOnMouseDragged(event -> {
                Cursor cursor = rootPane.getCursor();

                if (cursor == Cursor.DEFAULT) return;

                double mouseX = event.getScreenX();
                double mouseY = event.getScreenY();

                if (cursor == Cursor.E_RESIZE || cursor == Cursor.NE_RESIZE || cursor == Cursor.SE_RESIZE) {
                    stage.setWidth(mouseX - stage.getX());
                }
                if (cursor == Cursor.S_RESIZE || cursor == Cursor.SE_RESIZE || cursor == Cursor.SW_RESIZE) {
                    stage.setHeight(mouseY - stage.getY());
                }
                if (cursor == Cursor.W_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.SW_RESIZE) {
                    double newWidth = stage.getX() + stage.getWidth() - mouseX;
                    if (newWidth > stage.getMinWidth()) {
                        stage.setX(mouseX);
                        stage.setWidth(newWidth);
                    }
                }
                if (cursor == Cursor.N_RESIZE || cursor == Cursor.NE_RESIZE || cursor == Cursor.NW_RESIZE) {
                    double newHeight = stage.getY() + stage.getHeight() - mouseY;
                    if (newHeight > stage.getMinHeight()) {
                        stage.setY(mouseY);
                        stage.setHeight(newHeight);
                    }
                }
            });

            btnToggleTerminal.setOnAction(_ -> toggleTerminal());

            btnPauseTerminal.setOnAction(_ ->btnPauseTerminal());

            dev = new RadioButton("API In Dev");
            preprod = new RadioButton("API In Preprod");
            prod = new RadioButton("API In Prod");

            stepChoice = new RadioButton("stepChoice");
            moveFiles = new RadioButton("moveFiles");
            gray = new RadioButton("gray");
            grayAnomalies = new RadioButton("grayAnomalies");
            stockAnomalies = new RadioButton("stockAnomalies");
            stockNumber = new RadioButton("stockNumber");
            stockToShoot = new RadioButton("stockToShoot");
            pdf = new RadioButton("pdf");

            ToggleGroup group = new ToggleGroup();
            dev.setToggleGroup(group);
            preprod.setToggleGroup(group);
            prod.setToggleGroup(group);

            dev.setOnAction(_ -> Api.setApiSiteUrl("dev"));
            preprod.setOnAction(_ -> Api.setApiSiteUrl("preprod"));
            prod.setOnAction(_ -> Api.setApiSiteUrl("prod"));



            HBox options = new HBox();
            options.setPadding(new Insets(30));
            options.setBackground(
                new Background(
                    new BackgroundFill(
                        Color.web("#FFFFFF"),
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                    )
                )
            );
            options.setSpacing(20);
            options.getChildren().addAll(dev, preprod, prod, stepChoice,moveFiles,gray,grayAnomalies,stockAnomalies,stockNumber,stockToShoot,pdf);

            terminalPanel.getChildren().addAll(options);

            terminalPanel.setSpacing(10);
            terminalPanel.setAlignment(Pos.CENTER_LEFT);
            terminalPanel.setVisible(false);
            terminalPanel.setManaged(false);

            prod.setSelected(true);

            stepChoice.setSelected(true);
            moveFiles.setSelected(true);
            gray.setSelected(true);
            grayAnomalies.setSelected(true);
            stockAnomalies.setSelected(true);
            stockNumber.setSelected(true);
            stockToShoot.setSelected(true);
            pdf.setSelected(true);

        });
    }

    public void setContent(Node node) {

        contentPane.getChildren().setAll(node);

        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }

    private void toggleTerminal() {
        terminalVisible = !terminalVisible;
        terminalPanel.setVisible(terminalVisible);
        terminalPanel.setManaged(terminalVisible);
        btnToggleTerminal.setText(terminalVisible ? "⬆":"⬇");

    }

    private void btnPauseTerminal(){
        objGlobals.terminalPause = !objGlobals.terminalPause;
        btnPauseTerminal.setText(objGlobals.terminalPause?"resume":"pause");
    }

    public void appendLog( String text) {
        if(!objGlobals.terminalPause){
            Platform.runLater(() -> terminalOutput.appendText(LocalDateTime.now()+":"+ text + "\n"));
        }
    }

    private Stage getStage() {
        return (Stage) titleBar.getScene().getWindow();
    }

}
