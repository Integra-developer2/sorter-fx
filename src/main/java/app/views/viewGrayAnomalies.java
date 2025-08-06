package app.views;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import app.App;
import app.Routing;
import app.classes.GrayFiles;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import static app.functions.*;
import static app.functions.getBufferedImage;

public class viewGrayAnomalies implements Initializable{
    @FXML private StackPane parentFront;
    @FXML private StackPane parentBack;
    @FXML private ImageView front;
    @FXML private ImageView back;
    @FXML private ImageView frontLeft;
    @FXML private ImageView frontRight;
    @FXML private ImageView backLeft;
    @FXML private ImageView backRight;
    @FXML private ImageView progressImg;
    @FXML private Label progressLabel;
    @FXML private Label fileName;
    @FXML private Button btnBackwards;
    @FXML private Button btnForward;
    @FXML private Button btnEqual;
    @FXML private Button btnNotEqual;
    @FXML private VBox appBox;
    @FXML private VBox header;
    @FXML private HBox footer;
    private AtomicInteger count;
    private AtomicInteger total;
    private HashMap<Integer, viewGrayAnomalieObj> files;
    private double frontRotationAngle = 0;
    private double backRotationAngle = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        count = new AtomicInteger(0);
        total = new AtomicInteger(0);
        btnEqual.setOnAction(_ -> btnEqual());
        btnNotEqual.setOnAction(_ -> btnNotEqual());
        btnBackwards.setOnAction(_ -> btnBackwards());
        btnForward.setOnAction(_ -> btnForward());
        frontLeft.setOnMouseClicked(_ -> frontLeft());
        frontRight.setOnMouseClicked(_ -> frontRight());
        backLeft.setOnMouseClicked(_ -> backLeft());
        backRight.setOnMouseClicked(_ -> backRight());
        files = new HashMap<>();
        for (String file : GrayFiles.anomalies) {
            String filename = file.replace("-FRONTE.tiff", "").replace("-RETRO.tiff", "");
            File checkFileFront = new File(filename+"-FRONTE.tiff");
            File checkFileBack = new File(filename+"-RETRO.tiff");
            if(checkFileFront.exists()&&checkFileBack.exists()){
                boolean fileExists = files.values().stream().anyMatch(obj -> obj.filenameAnomalies.equals(filename));
                if (!fileExists) {
                    files.put(total.getAndIncrement(), new viewGrayAnomalieObj(filename));
                }
            }
            else{
                if(checkFileFront.exists()){
                    if(!checkFileFront.delete()){
                        logError("error",new Exception("File "+checkFileFront+" not found"));
                    }
                }
                if(checkFileBack.exists()){
                    if(!checkFileBack.delete()){
                        logError("error",new Exception("File "+checkFileBack+" not found"));
                    }
                }
            }
        }
        loadFile();
        applyClipping(parentFront);
        applyClipping(parentBack);
        addZoomFunctionality(front);
        addZoomFunctionality(back);
        refreshProgressBar();
    }

    private Image loadTiffAsImage(File tiffFile) throws Exception{
        BufferedImage bufferedImage = readTiffImage(tiffFile);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    private static BufferedImage readTiffImage(File tiffFile) throws IOException {
        return getBufferedImage(tiffFile);
    }

    private void shakeButton() {
        Button button = btnForward;
        button.setDisable(false);
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), button);
        shake.setFromX(0f);
        shake.setByX(10f);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();

        btnEqual.setDisable(true);
        btnNotEqual.setDisable(true);
    }

    private void bindImageViewToParent(ImageView imageView, boolean isHorizontal, StackPane parent) {
        if(isHorizontal){
            imageView.fitWidthProperty().bind(Bindings.createDoubleBinding(
                () -> appBox.getWidth()/3,
                appBox.widthProperty()
            ));
        }
        else{
            imageView.fitHeightProperty().bind(Bindings.createDoubleBinding(
                () -> (appBox.getHeight()*0.8) - header.getHeight() - footer.getHeight(),
                appBox.heightProperty(),
                header.heightProperty(),
                footer.heightProperty()
            ));
        }
        applyClipping(parent);
    }

    private void frontLeft() {
        frontRotationAngle -= 90;
        rotateImageView(front, frontRotationAngle);
    }

    private void frontRight() {
        frontRotationAngle += 90;
        rotateImageView(front, frontRotationAngle);
    }

    private void backLeft() {
        backRotationAngle -= 90;
        rotateImageView(back, backRotationAngle);
    }

    private void backRight() {
        backRotationAngle += 90;
        rotateImageView(back, backRotationAngle);
    }

    private void rotateImageView(ImageView imageView, double angle) {
        resetImageView(imageView);
        imageView.getTransforms().clear();
        double pivotX = imageView.getBoundsInParent().getWidth() / 2;
        double pivotY = imageView.getBoundsInParent().getHeight() / 2;
        imageView.getTransforms().add(new Rotate(angle, pivotX, pivotY));
    }

    private void applyClipping(StackPane parent) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(parent.widthProperty());
        clip.heightProperty().bind(parent.heightProperty());
        parent.setClip(clip);
    }

    private void addZoomFunctionality(ImageView imageView) {
        final double MAX_SCALE = 3.0;
        final double MIN_SCALE = 0.5;
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();

        imageView.setOnScroll(event -> {
            double delta = 1.2;
            double scale = imageView.getScaleX();

            if (event.getDeltaY() < 0) {
                scale /= delta;
            } else {
                scale *= delta;
            }
            scale = clamp(scale, MIN_SCALE, MAX_SCALE);

            imageView.setScaleX(scale);
            imageView.setScaleY(scale);

            Bounds imageBounds = imageView.getBoundsInParent();
            Bounds parentBounds = imageView.getParent().getLayoutBounds();

            double translateX = clamp(imageView.getTranslateX(),parentBounds.getWidth() - imageBounds.getWidth(), 0);
            double translateY = clamp(imageView.getTranslateY(),parentBounds.getHeight() - imageBounds.getHeight(), 0);

            imageView.setTranslateX(translateX);
            imageView.setTranslateY(translateY);

            event.consume();
        });
        imageView.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()||event.isPrimaryButtonDown()) {
                mouseAnchor.set(new Point2D(event.getSceneX(), event.getSceneY()));
            }
        });
        imageView.setOnMouseDragged(event -> {
            if (event.isSecondaryButtonDown()||event.isPrimaryButtonDown()) {
                Point2D dragDelta = new Point2D(event.getSceneX() - mouseAnchor.get().getX(),event.getSceneY() - mouseAnchor.get().getY());
                imageView.setTranslateX(imageView.getTranslateX() + dragDelta.getX());
                imageView.setTranslateY(imageView.getTranslateY() + dragDelta.getY());
                mouseAnchor.set(new Point2D(event.getSceneX(), event.getSceneY()));
            }
        });
    }

    private double clamp(double value, double min, double max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    private void resetImageView(ImageView imageView) {
        imageView.setScaleX(1.0);
        imageView.setScaleY(1.0);
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
        imageView.getTransforms().clear();
    }

    private void loadFile(){
        try {
            viewGrayAnomalieObj obj = files.get(count.get());
            File fileFront = obj.fileFront;
            File fileBack = obj.fileBack;
            resetImageView(front);
            resetImageView(back);
            bindImageViewToParent(front, Boolean.TRUE.equals(isHorizontal(fileFront.toString())),parentFront);
            bindImageViewToParent(back, Boolean.TRUE.equals(isHorizontal(fileBack.toString())),parentBack);
            front.setImage(loadTiffAsImage(fileFront));
            back.setImage(loadTiffAsImage(fileBack));
            fileName.setText(obj.file);
        }
        catch (Exception e) {
            printError(e,false);
        }

    }

    private void refreshProgressBar(){
        Platform.runLater(()->{
            double percent = (double)count.get()/(double)total.get();
            if(percent<0.1){progressImg.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/progress-00.gif")).toExternalForm()));}
            else if(percent<0.2){progressImg.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/progress-01.gif")).toExternalForm()));}
            else if(percent<0.3){progressImg.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/progress-02.gif")).toExternalForm()));}
            else if(percent<0.4){progressImg.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/progress-03.gif")).toExternalForm()));}
            else if(percent<0.5){progressImg.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/progress-04.gif")).toExternalForm()));}
            else if(percent<0.6){progressImg.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/progress-05.gif")).toExternalForm()));}
            else if(percent<0.7){progressImg.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/progress-06.gif")).toExternalForm()));}
            else if(percent<0.8){progressImg.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/progress-07.gif")).toExternalForm()));}
            else if(percent<0.9){progressImg.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/progress-08.gif")).toExternalForm()));}
            else if(percent<1.0){progressImg.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/progress-09.gif")).toExternalForm()));}
            else{
                progressImg.setImage(new Image(Objects.requireNonNull(App.class.getResource("img/progress-10.gif")).toExternalForm()));
            }
            progressLabel.setText(count.get()+"/"+total.get());
        });
    }
    private void btnEqual(){
        moveFile("target");
    }

    private void btnNotEqual(){
        moveFile("log");
    }

    private void moveFile(String moveTo){
        if(count.get()<total.get()){
            files.get(count.getAndIncrement()).move(moveTo);
            if(count.get()<total.get()){
                loadFile();
            }
            else{
                shakeButton();
            }
        }
        else{
            shakeButton();
        }
        toggleBtnBackwards();
        refreshProgressBar();
    }

    private void btnBackwards(){
        btnForward.setDisable(true);
        count.decrementAndGet();
        btnEqual.setDisable(false);
        btnNotEqual.setDisable(false);
        loadFile();
        toggleBtnBackwards();
        refreshProgressBar();
    }

    private void btnForward(){
        Routing.grayAnomalies ="end";
    }

    private void toggleBtnBackwards(){
        btnBackwards.setDisable(count.get() < 1);
    }
}