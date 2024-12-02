package app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static app.functions.moveFolder;
import static app.functions.hasNotExpectedFolder;
import static app.functions.hasAnomalyFolderStock2;
import static app.functions.step;
import static app.functions.alert;
import static app.functions.stepFile;
import static app.functions.confirm;
import static app.functions.deleteFolder;
import static app.functions.getProgressGray;
import static app.functions.getProgressStock;
import static app.functions.load;
import static app.functions.ls;
import static app.functions.printError;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ctrlStatusBar implements Initializable {
    @FXML
    private Label title;
    @FXML
    private Label lblMoveFiles;
    @FXML
    private ImageView imgMoveFiles;
    @FXML
    private Label lblStockFile;
    @FXML
    private ImageView imgStockFile;
    @FXML
    private Label lblGray;
    @FXML
    private Label plGray;
    @FXML
    private Label plStock;
    @FXML
    private ImageView imgGray;
    @FXML
    private Label lblAnomaliesGray;
    @FXML
    private ImageView imgAnomaliesGray;
    @FXML
    private Label lblStock;
    @FXML
    private ImageView imgStock;
    @FXML
    private Label lblAnomaliesStock;
    @FXML
    private ImageView imgAnomaliesStock;
    @FXML
    private Label lblAnomaliesStock2;
    @FXML
    private ImageView imgAnomaliesStock2;

    private double lastProgressGray = 0;
    private double lastProgressStock = 0;
    private File stepMoveFiles;
    private File stepStockFile;
    private File stepGray;
    private File stepAnomaliesGray;
    private File stepStockAnomalies;
    private File stepStockAnomalies2;
    private File stepStock;
    private Service<Void> moveFilesService;
    private Service<Void> stockFileService;
    private Service<Void> grayService;
    private Service<Void> grayAnomaliesService;
    private Service<Void> stockAnomaliesService;
    private Service<Void> stockAnomalies2Service;
    private Service<Void> stockService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(objGlobals.version);
        try {
            stepMoveFiles=stepFile("moveFilesEnd");
            stepStockFile=stepFile("stockFileEnd");
            stepGray=stepFile("grayEnd");
            stepAnomaliesGray=stepFile("stepAnomaliesGrayEnd");
            stepStock=stepFile("stockEnd");
            stepStockAnomalies=stepFile("stepStockAnomaliesEnd");
            stepStockAnomalies2=stepFile("stepStockAnomalies2End");
            moveFilesService = new Service<>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            if(!objGlobals.stop){
                                try {
                                    Platform.runLater(() -> {
                                        lblMoveFiles.setText("iniziato");
                                        imgMoveFiles.setImage(new Image(App.class.getResource("img/download.gif").toExternalForm()));
                                    });
                                    boolean start[] = {true};
                                    CountDownLatch latch = new CountDownLatch(1);
                                    if(outOfSpace()){
                                        Platform.runLater(() -> {
                                            try {
                                                start[0] = confirm(objAnomalies.noSpace, "SPAZIO INSUFFICIENTE!, CONTINUA LO STESSO ?");
                                            }
                                            finally {
                                                latch.countDown();
                                            }
                                        });
                                        latch.await();
                                    }
                                    if(start[0]){
                                        app.o1_sorter_move_files.EntryPoint.start();
                                        if(!objAnomalies.moveFiles.isEmpty()){
                                            Platform.runLater(() -> {
                                                lblMoveFiles.setText("Anomalies");
                                                imgMoveFiles.setImage(new Image(App.class.getResource("img/warning.gif").toExternalForm()));
                                                alert("ANOMALIA","SISTEMA LE ANOMALIE PRIMA DI CONTINUARE");
                                                load("viewMoveFilesAnomalie");
                                            });
                                        }else{
                                            if(!objGlobals.stop){
                                                Platform.runLater(() -> {completeMoveFiles();});
                                                stockFileService.start();
                                            }
                                        }
                                    }
                                    else{
                                        Platform.exit();
                                    }
                                }
                                catch (Exception e) {
                                    printError(e,true);throw e;
                                }
                            }

                            return null;
                        }
                    };
                }
            };

            stockFileService = new Service<>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            if(!objGlobals.stop){
                                Platform.runLater(() -> {
                                    lblStockFile.setText("iniziato");
                                });
                                try {
                                    if(objAnomalies.hasStockFileAnomaly()){
                                        Platform.runLater(() -> {
                                            lblStockFile.setText("Anomalies");
                                            imgStockFile.setImage(new Image(App.class.getResource("img/warning.gif").toExternalForm()));
                                            alert("ANOMALIA","SISTEMA LE ANOMALIE PRIMA DI CONTINUARE");
                                            load("viewStockFile",1200,500);
                                        });
                                    }else{
                                        if(!objGlobals.stop){
                                            Platform.runLater(() -> {completeStockFile();});
                                            grayService.start();
                                        }
                                    }
                                }
                                catch (Exception e) {
                                    printError(e,true);throw e;
                                }
                            }

                            return null;
                        }
                    };
                }
            };

            grayService = new Service<>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            if(!objGlobals.stop){
                                Platform.runLater(()->{
                                    bindProgressGray();
                                    lblGray.setText("iniziato");
                                    plGray.setVisible(true);
                                    imgGray.setImage(new Image(App.class.getResource("img/progress-00.gif").toExternalForm()));
                                });
                                try {
                                    app.o2_sorter_gray.EntryPoint.start();
                                    if(!objGlobals.stop){
                                        Platform.runLater(()->{completeGray();});
                                        grayAnomaliesService.start();
                                    }
                                } catch (Exception e) {
                                    printError(e,true);
                                }
                            }
                            return null;
                        }
                    };
                }
            };

            grayAnomaliesService = new Service<>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            if(!objGlobals.stop){
                                Platform.runLater(()->{
                                    lblAnomaliesGray.setText("iniziato");
                                    imgAnomaliesGray.setImage(new Image(App.class.getResource("img/warning.gif").toExternalForm()));
                                });
                                if(objAnomalies.hasGrayAnomaly()){
                                    Platform.runLater(()->{
                                        alert("ANOMALIA","SISTEMA LE ANOMALIE PRIMA DI CONTINUARE");
                                        load("viewGrayAnomalie",900,900);
                                    });
                                }
                                else if(!objGlobals.stop){
                                    Platform.runLater(()->{completeGrayAnomalies();});
                                }
                            }
                            return null;
                        }
                    };
                }
            };

            stockAnomaliesService = new Service<>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            if(!objGlobals.stop){
                                Platform.runLater(()->{
                                    lblAnomaliesStock.setText("iniziato");
                                    imgAnomaliesStock.setImage(new Image(App.class.getResource("img/warning.gif").toExternalForm()));
                                });
                                try {
                                    if(!objAnomalies.hasStockAnomaly()){
                                        if(!objGlobals.stop){
                                            Platform.runLater(()->{completeStockAnomalies();});
                                            stockAnomalies2Service.start();
                                        }
                                    }
                                    else{
                                        Platform.runLater(()->{
                                            alert("ANOMALIA","SISTEMA LE ANOMALIE PRIMA DI CONTINUARE");
                                            load("viewStockAnomalie",1000,600);
                                        });
                                    }
                                } catch (Exception e) {
                                    printError(e,true);throw e;
                                }
                            }
                            return null;
                        }
                    };
                }
            };

            stockAnomalies2Service = new Service<>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            if(!objGlobals.stop){
                                Platform.runLater(()->{
                                    lblAnomaliesStock2.setText("iniziato");
                                    imgAnomaliesStock2.setImage(new Image(App.class.getResource("img/warning.gif").toExternalForm()));
                                });
                                if(!hasNotExpectedFolder()&&!hasAnomalyFolderStock2()){ objAnomalies.findStockAnomaly2();}
                                if(hasNotExpectedFolder()){
                                    boolean start[] = {true};
                                    CountDownLatch latch = new CountDownLatch(1);
                                    try {
                                        ArrayList<String>unExpectedGroups=new ArrayList<>();
                                        Platform.runLater(() -> {
                                            try {
                                                String message = "CONFERMI CHE QUESTI COMUNI NON DEVONO ESSERE PREVISTI ?";
                                                try {
                                                    Files.walkFileTree(Paths.get(objGlobals.notExpectedFolder), new SimpleFileVisitor<Path>()  {
                                                        @Override
                                                        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                                                            Path fullPath = Paths.get(path.toFile().getParent());
                                                            Path notExpectedFolderPath = Paths.get(objGlobals.notExpectedFolder);
                                                            Path relativePath = notExpectedFolderPath.relativize(fullPath);
                                                            String group = relativePath.getName(0).toString();
                                                            if(!unExpectedGroups.contains(group)){
                                                                unExpectedGroups.add(group);
                                                            }
                                                            return FileVisitResult.CONTINUE;
                                                        }
                                                    });
                                                } catch (IOException e) {
                                                    printError(e, false);
                                                }
                                                start[0] = confirm("ATENZIONE", message, unExpectedGroups);
                                            } finally {
                                                latch.countDown();
                                            }
                                        });
                                        latch.await();
                                    } catch (InterruptedException e) {
                                        printError(e,false);
                                    }
                                    if(start[0]){
                                        moveFolder(objGlobals.notExpectedFolder, objGlobals.logFolder);
                                    }
                                    else{
                                        objGlobals.stop=true;
                                        alert("ANOMALIA","SISTEMA LA CARTELLA \"NON PREVISTI NEL FILE ETICHETTE\" E RIAPRI IL PROGRAMMA");
                                    }
                                }
                                if(!objGlobals.stop){
                                    if(hasAnomalyFolderStock2()){
                                        objAnomalies.loadObjNotExpected();
                                        Platform.runLater(()->{ load("viewStockAnomalie2",1000,600);});
                                    }
                                    else{
                                        Platform.runLater(()->{completeStockAnomalies2();});
                                        stockService.start();
                                    }
                                }
                            }
                            return null;
                        }
                    };
                }
            };

            stockService = new Service<>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            if(!objGlobals.stop){
                                Platform.runLater(()->{
                                    bindProgressStock();
                                    lblStock.setText("iniziato");
                                    plStock.setVisible(true);
                                    imgStock.setImage(new Image(App.class.getResource("img/progress-00.gif").toExternalForm()));
                                });
                                try {
                                    app.o3_sorter_stock.EntryPoint.start();
                                    if(!objGlobals.stop){
                                        Platform.runLater(()->{completeStock();});
                                    }
                                } catch (Exception e) {
                                    printError(e,true);throw e;
                                }
                            }
                            return null;
                        }
                    };
                }
            };

            setupServices();
            startService();
        } catch (Exception e) {
            printError(e,true);
        }
    }

    private void setupServices(){
        bindServiceStack(moveFilesService,lblMoveFiles,imgMoveFiles);
        bindServiceStack(stockFileService,lblStockFile,imgStockFile);
        bindServiceStack(grayService,lblGray,imgGray);
        bindServiceStack(grayAnomaliesService,lblAnomaliesGray,imgAnomaliesGray);
        bindServiceStack(stockAnomaliesService,lblAnomaliesStock,imgAnomaliesStock);
        bindServiceStack(stockAnomalies2Service,lblAnomaliesStock2,imgAnomaliesStock2);
        bindServiceStack(stockService,lblStock,imgStock);
    }

    private static boolean outOfSpace() throws Exception{
        try {
            String targetGrayPartition = objGlobals.targetGray.substring(0,2);
            String targetTiffPartition = objGlobals.targetTiff.substring(0,2);
            if(targetGrayPartition.equals(targetTiffPartition)){
                File diskPartition = new File(targetGrayPartition);
                long freeSpace = diskPartition.getFreeSpace();
                long graySize = folderSize(objGlobals.sourceGray);
                long tiffSize = folderSize(objGlobals.sourceTiff);
                long sourceSize = graySize + tiffSize;
                double sizeLocal = (double) freeSpace / (1024 * 1024 * 1024);
                double sizeSource = (double) sourceSize / (1024 * 1024 * 1024);
                if(sizeLocal<sizeSource){
                    objAnomalies.noSpace="Servono: "+String.format("%.2f", sizeSource)+" GB. Sono Disponibili: "+String.format("%.2f",sizeLocal)+" GB";
                    return true;
                }
            }
        }
        catch (Exception e) {
            throw e;
        }
        return false;
    }

    public static long folderSize(String directoryPath) throws Exception {
        Path path = Paths.get(directoryPath);
        final long[] size = {0};
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    size[0] += attrs.size();
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            printError(e,true);throw e;
        }
        return size[0];
    }

    private void handleError(Label label, ImageView imageView, WorkerStateEvent  event) {
        Platform.runLater(() -> {
            label.setText("errore");
            imageView.setImage(new Image(App.class.getResource("img/error.gif").toExternalForm()));
        });
        Throwable exception = event.getSource().getException();
        printError(new Exception(exception.toString()),true);
    }

    private void bindProgressGray(){
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Double progressGray = getProgressGray();
                if (progressGray > lastProgressGray) {
                    lastProgressGray = progressGray;
                    Platform.runLater(() -> {
                        plGray.setText(String.format("%.2f%%", progressGray * 100));
                        String gif;
                        if(progressGray<0.1){gif="img/progress-00.gif";}
                        else if(progressGray<0.2){gif="img/progress-01.gif";}
                        else if(progressGray<0.3){gif="img/progress-02.gif";}
                        else if(progressGray<0.4){gif="img/progress-03.gif";}
                        else if(progressGray<0.5){gif="img/progress-04.gif";}
                        else if(progressGray<0.6){gif="img/progress-05.gif";}
                        else if(progressGray<0.7){gif="img/progress-06.gif";}
                        else if(progressGray<0.8){gif="img/progress-07.gif";}
                        else if(progressGray<0.9){gif="img/progress-08.gif";}
                        else if(progressGray<1.0){gif="img/progress-09.gif";}
                        else {gif="img/progress-10.gif";}
                        imgGray.setImage(new Image(App.class.getResource(gif).toExternalForm()));
                    });
                }
            }
        }, 0, 60000);
    }

    private void bindProgressStock(){
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Double progressStock = getProgressStock();
                if (progressStock > lastProgressStock) {
                    lastProgressStock = progressStock;
                    Platform.runLater(() -> {
                        plStock.setText(String.format("%.2f%%", progressStock * 100));
                        String gif;
                        if(progressStock<0.1){gif="img/progress-00.gif";}
                        else if(progressStock<0.2){gif="img/progress-01.gif";}
                        else if(progressStock<0.3){gif="img/progress-02.gif";}
                        else if(progressStock<0.4){gif="img/progress-03.gif";}
                        else if(progressStock<0.5){gif="img/progress-04.gif";}
                        else if(progressStock<0.6){gif="img/progress-05.gif";}
                        else if(progressStock<0.7){gif="img/progress-06.gif";}
                        else if(progressStock<0.8){gif="img/progress-07.gif";}
                        else if(progressStock<0.9){gif="img/progress-08.gif";}
                        else if(progressStock<1.0){gif="img/progress-09.gif";}
                        else {gif="img/progress-10.gif";}
                        imgStock.setImage(new Image(App.class.getResource(gif).toExternalForm()));
                    });
                }
            }
        }, 0, 60000);
    }

    private void bindServiceStack(Service<Void> service, Label label, ImageView imageView){
        service.setOnFailed(event -> handleError(label, imageView, event));
    }

    private void startService(){
        if(!objGlobals.stop){
            if(!stepMoveFiles.exists()){ moveFilesService.start();}
            else{
                Platform.runLater(()->{ completeMoveFiles();});
                if(!stepStockFile.exists()){ stockFileService.start();}
                else{
                    Platform.runLater(()->{ completeStockFile();});
                    if(!stepGray.exists()){ grayService.start();}
                    else {
                        Platform.runLater(()->{ completeGray();});
                        if(!stepStock.exists()){
                            if(new File(objGlobals.anomalyFolderGray).exists()){ grayAnomaliesService.start();}
                            else{
                                Platform.runLater(()->{ completeGrayAnomalies();});
                                if(!objGlobals.skipAnomalies){ stockAnomaliesService.start();}
                                else{
                                    Platform.runLater(()->{ completeStockAnomalies();completeStockAnomalies2();});
                                    stockService.start();
                                }
                            }
                        }
                        else {
                            Platform.runLater(()->{ completeGrayAnomalies();completeStockAnomalies();completeStockAnomalies2();completeStock();});
                        }
                    }
                }
            }
        }
    }

    private void completeMoveFiles(){
        step(stepMoveFiles);
        lblMoveFiles.setText("completato");
        imgMoveFiles.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
    }

    private void completeStockFile(){
        step(stepStockFile);
        lblStockFile.setText("completato");
        imgStockFile.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
    }

    private void completeGray(){
        step(stepGray);
        lblGray.setText("completato");
        plGray.setVisible(false);
        imgGray.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
    }

    public void completeGrayAnomalies(){
        step(stepAnomaliesGray);
        if(new File(objGlobals.anomalyFolderGray).exists()){
            if(ls(objGlobals.anomalyFolderGray,".tiff").isEmpty()){
                deleteFolder(objGlobals.anomalyFolderGray);
            }
        }
        lblAnomaliesGray.setText("completato");
        imgAnomaliesGray.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
    }

    private void completeStockAnomalies(){
        step(stepStockAnomalies);
        lblAnomaliesStock.setText("completato");
        imgAnomaliesStock.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
    }

    private void completeStockAnomalies2(){
        step(stepStockAnomalies2);
        lblAnomaliesStock2.setText("completato");
        imgAnomaliesStock2.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
    }

    private void completeStock(){
        step(stepStock);
        lblStock.setText("completato");
        plStock.setVisible(false);
        imgStock.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
    }

}
