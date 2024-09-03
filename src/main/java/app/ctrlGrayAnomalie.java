package app;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import static app.functions.deleteFolder;
import static app.functions.isHorizontal;
import static app.functions.load;
import static app.functions.printError;
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

public class ctrlGrayAnomalie implements Initializable{
    @FXML
    private Label title;
    @FXML
    private StackPane parentFront;
    @FXML
    private StackPane parentBack;
    @FXML
    private ImageView front;
    @FXML
    private ImageView back;
    @FXML
    private ImageView frontLeft;
    @FXML
    private ImageView frontRight;
    @FXML
    private ImageView backLeft;
    @FXML
    private ImageView backRight;
    @FXML
    private ImageView progressImg;
    @FXML
    private Label progressLabel;
    @FXML
    private Button btnBackwards;
    @FXML
    private Button btnFoward;
    @FXML
    private Button btnEqual;
    @FXML
    private Button btnNotEqual;
    @FXML
    private VBox appBox;
    @FXML
    private VBox header;
    @FXML
    private HBox footer;
    private int count=1;
    private int total;
    private HashMap<Integer,ctrlGrayAnomalieObj> files;
    private double frontRotationAngle = 0;
    private double backRotationAngle = 0;
    private boolean over=false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(objGlobals.version);
        title.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> appBox.getWidth(),appBox.widthProperty()));
        btnEqual.setOnAction(event -> btnEqual());
        btnNotEqual.setOnAction(event -> btnNotEqual());
        btnBackwards.setOnAction(event -> btnBackwards());
        btnFoward.setOnAction(event -> btnFoward());
        frontLeft.setOnMouseClicked(event -> frontLeft());
        frontRight.setOnMouseClicked(event -> frontRight());
        backLeft.setOnMouseClicked(event -> backLeft());
        backRight.setOnMouseClicked(event -> backRight());
        files = new HashMap<>();
        for (String file : objAnomalies.gray) {
            String filename = file.replace("-FRONTE.tiff", "").replace("-RETRO.tiff", "");
            File checkFileFront = new File(filename+"-FRONTE.tiff");
            File checkFfileBack = new File(filename+"-RETRO.tiff");
            if(checkFileFront.exists()&&checkFfileBack.exists()){
                boolean fileExists = files.values().stream().anyMatch(obj -> obj.filename.equals(filename));
                if (!fileExists) {
                    total++;
                    files.put(total, new ctrlGrayAnomalieObj(filename));
                }
            }
            else{
                if(checkFileFront.exists()){
                    checkFileFront.delete();
                }
                if(checkFfileBack.exists()){
                    checkFfileBack.delete();
                }
            }
        }
        next();
        applyClipping(parentFront);
        applyClipping(parentBack);
        addZoomFunctionality(front);
        addZoomFunctionality(back);
        progressLabel.setText(count+"/"+total);
    }

    private Image loadTiffAsImage(File tiffFile) throws Exception{
        BufferedImage bufferedImage = readTiffImage(tiffFile);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    private void btnBackwards(){
        count--;
        next();
        if(count==1){
            btnBackwards.setDisable(true);
        }
        btnFoward.setDisable(true);
        progressLabel.setText(count+"/"+total);
    }

    private static BufferedImage readTiffImage(File tiffFile) throws IOException {
        try (ImageInputStream input = ImageIO.createImageInputStream(tiffFile)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(input);
                BufferedImage image = reader.read(0);
                reader.dispose();
                return image;
            } else {
                return null;
            }
        }
    }

    private void btnEqual(){
        if(!over){
            files.get(count).move("target");
            if(count<total){
                count++;
                progressLabel.setText(count+"/"+total);
                next();
            }
            else{
                over=true;
                shakeButton(btnFoward);
            }
        }
        else{
            shakeButton(btnFoward);
        }
    }

    private void btnNotEqual(){
        if(!over){
            files.get(count).move("log");
            if(count<total){
                count++;
                progressLabel.setText(count+"/"+total);
                next();
            }
            else{
                over=true;
                shakeButton(btnFoward);
            }
        }
        else{
            shakeButton(btnFoward);
        }
    }

    private void shakeButton(Button button) {
        button.setDisable(false);
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), button);
        shake.setFromX(0f);
        shake.setByX(10f);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
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
                () -> (appBox.getHeight()*0.9) - header.getHeight() - footer.getHeight(),
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
            Bounds parentBounds = ((VBox) imageView.getParent()).getLayoutBounds();

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
        if (value > max) return max;
        return value;
    }

    private void next(){
        try {
            File fileFront = files.get(count).fileFront;
            File fileBack = files.get(count).fileBack;
            bindImageViewToParent(front,isHorizontal(fileFront.toString()),parentFront);
            bindImageViewToParent(back,isHorizontal(fileBack.toString()),parentBack);
            front.setImage(loadTiffAsImage(fileFront));
            back.setImage(loadTiffAsImage(fileBack));
        } catch (Exception e) {
            printError(e,false);
        }
        if(over){
            btnFoward.setDisable(false);
        }
        if(count>1){
            btnBackwards.setDisable(false);
        }

        Platform.runLater(()->{
            double percent = (double)count/(double)total;
            if(percent<0.1){progressImg.setImage(new Image(App.class.getResource("img/progress-00.gif").toExternalForm()));}
            else if(percent<0.2){progressImg.setImage(new Image(App.class.getResource("img/progress-01.gif").toExternalForm()));}
            else if(percent<0.3){progressImg.setImage(new Image(App.class.getResource("img/progress-02.gif").toExternalForm()));}
            else if(percent<0.4){progressImg.setImage(new Image(App.class.getResource("img/progress-03.gif").toExternalForm()));}
            else if(percent<0.5){progressImg.setImage(new Image(App.class.getResource("img/progress-04.gif").toExternalForm()));}
            else if(percent<0.6){progressImg.setImage(new Image(App.class.getResource("img/progress-05.gif").toExternalForm()));}
            else if(percent<0.7){progressImg.setImage(new Image(App.class.getResource("img/progress-06.gif").toExternalForm()));}
            else if(percent<0.8){progressImg.setImage(new Image(App.class.getResource("img/progress-07.gif").toExternalForm()));}
            else if(percent<0.9){progressImg.setImage(new Image(App.class.getResource("img/progress-08.gif").toExternalForm()));}
            else if(percent<1.0){progressImg.setImage(new Image(App.class.getResource("img/progress-09.gif").toExternalForm()));}
            else{
                progressImg.setImage(new Image(App.class.getResource("img/progress-10.gif").toExternalForm()));
            }
        });
    }

    private void btnFoward(){
        deleteFolder(objGlobals.anomalyFolderGray);
        objAnomalies.grayReset();
        Platform.runLater(()->{
            try {
                load("viewStatusBar");
            } catch (Exception e) {
                throw e;
            }
        });
    }
}