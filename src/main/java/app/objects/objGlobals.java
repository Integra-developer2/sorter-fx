package app.objects;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static app.functions.printError;

import app.classes.Api;
import javafx.scene.Scene;

public class objGlobals {
    public static String version = "SORTER-FX 2.0.2";
    public static int totalThreads=1;
    public static String runStep="";
    public static String scanditProgram="it.smaart.sorter.stock";
    public static String scanditKey="test.key";
    public static String raggruppamentoJobSorter="C";
    public static String colonnaBarcode="B";
    public static String workingFolder="";
    public static String sourceEtichette="";
    public static String targetEtichette="";
    public static String noSpace="";
    public static ArrayList<String> sourceJobSorter=new ArrayList<>();
    public static String sourceGray="";
    public static String sourceTiff="";
    public static String targetGray="";
    public static String targetTiff="";
    public static String logFolder;
    public static String cacheFolder;
    public static String anomalyFolderGray;
    public static String logAnomalyFolderGray;
    public static String logAnomalyFolderBlack;
    public static String anomalyFolderStock1;
    public static String logAnomalyStockNumber;
    public static String anomalyFolderStock2;
    public static String logToPdfFolder;
    public static String errorLog;
    public static String timeLog;
    public static String jobSorterFolder;
    public static String etichetteFolder;
    public static String bwDir;
    public static String logGray;
    public static String logBlack;
    public static String logStep;
    public static objErrorMap errorMap;
    public static String pdfFolder;
    public static String pdfNoStockFolder;
    public static String logStock;
    public static boolean rotateHorizontal=true;
    public static boolean stop=false;
    public static String logGrayTxt;
    public static String notExpectedFolder;
    public static File allBlackFiles;
    public static File validTiffs;
    public static String controlloQualita;
    public static String fileEtichette;
    public static String outputFolder;
    public static Scene scene;
    public static String notPredictedLog;
    public static String objNotExpectedFolder;
    public static String jobSorterCacheFile;
    public static String allBlackCacheFile;
    public static boolean terminalPause = false;
    public static String logSourceGray = "";
    public static String logSourceTiff = "";
    public static String logSourceEtichette  = "";
    public static String logSourceJobSorter  = "";
    public static String pdfs = "";
    public static File urlFile;
    public static AtomicBoolean shouldUpdateUrlFile = new AtomicBoolean(false);
    public static String apiOption;
    public static String partition;
    public static AtomicInteger lastProgressBar = new AtomicInteger(0);

    public static void variables(){
        logFolder=logFolder();
        partition=partition();
        targetGray=new File(new File(workingFolder, "FILE_TEMPORANEI"), "GRIGI").toString();
        targetTiff=new File(new File(workingFolder, "FILE_TEMPORANEI"), "TIFF").toString();
        jobSorterFolder=new File(new File(workingFolder, "FILE_TEMPORANEI"),"JOB-SORTER").toString();
        etichetteFolder=new File(new File(workingFolder, "FILE_TEMPORANEI"),"ETICHETTE").toString();
        outputFolder=new File(workingFolder, "RISULTATO").toString();
        anomalyFolderGray=new File(workingFolder, "ANOMALIE GRIGI").toString();
        anomalyFolderStock1=new File(workingFolder, "ANOMALIE ETICHETTE 1").toString();
        anomalyFolderStock2=new File(workingFolder, "ANOMALIE ETICHETTE 2").toString();
        notExpectedFolder=new File(workingFolder, "NON PREVISTI NEL FILE ETICHETTE").toString();

        logSourceGray=new File(logFolder, "logSourceGray").toString();
        logSourceTiff=new File(logFolder, "logSourceTiff").toString();
        logSourceEtichette=new File(logFolder, "logSourceEtichette").toString();
        logSourceJobSorter=new File(logFolder, "logSourceJobSorter").toString();

        logAnomalyFolderGray=new File(logFolder, "ANOMALIE GRIGI").toString();
        logAnomalyFolderBlack=new File(logFolder, "ANOMALIE TIFF").toString();
        logAnomalyStockNumber=new File(logFolder, "ANOMALIE STOCK").toString();
        logToPdfFolder=new File(logFolder, "TIFF PER GRUPPO").toString();

        cacheFolder=new File(objGlobals.logFolder,"CACHE").toString();
        errorLog=new File(logFolder, "errors.txt").toString();
        timeLog=new File(logFolder, "timeLog.txt").toString();
        bwDir=new File(logFolder, "BW").toString();
        logGray=new File(new File(logFolder, "STEP_CONTROL"), "objConcurrentGrayController").toString();
        logBlack=new File(new File(logFolder, "STEP_CONTROL"), "objConcurrentBlackController").toString();
        logStep=new File(new File(logFolder, "STEP_CONTROL"), "STEPS").toString();
        logStock=new File(new File(logFolder, "STEP_CONTROL"), "ThreadPdf").toString();
        pdfs = new File(logFolder, "pdfs.txt").toString();
        urlFile = new File(logFolder, "url.txt");

        notPredictedLog=new File(logFolder, "nonPrevisti.log").toString();
        objNotExpectedFolder=new File(logFolder, "objNotExpectedFolder").toString();

        allBlackFiles=new File(logFolder, "allBlackFiles.txt");
        validTiffs=new File(logFolder, "validTiffs.txt");

        jobSorterCacheFile=new File(cacheFolder,"jobSorter.cache").toString();
        allBlackCacheFile=new File(cacheFolder,"allBlackFiles.cache").toString();
        logGrayTxt =new File(logGray, "LOG_GRAY.txt").toString();
        pdfFolder=new File(outputFolder, "PDFS").toString();
        pdfNoStockFolder=new File(logFolder, "PDFS [paccomancante]").toString();
        controlloQualita=new File(outputFolder, "controllo_qualità_").toString();
        fileEtichette=new File(outputFolder, "etichette_").toString();

        errorMap= new objErrorMap();

        if(objGlobals.urlFile.exists()&&!shouldUpdateUrlFile.get()){
            Api.setApiUrl();
        }
    }

    private static String partition(){
        Path path = Paths.get(workingFolder);
        Path root = path.getRoot();
        if (root != null) {
            return root.toString().substring(0, 1);
        }
        printError(new Exception("partition not found"),true);
        return null;
    }

    public static String logFolder() {
        try{
            File file = new File(objGlobals.workingFolder,"_LOGS");
            logFolder=file.getAbsolutePath();
            Path logPath = Paths.get(logFolder);
            if(!Files.exists(logPath)){
                Files.createDirectories(logPath);
                Files.setAttribute(logPath, "dos:hidden", true);
            }
            else{
                Files.setAttribute(logPath, "dos:hidden", true);
            }
            return logFolder;
        }
        catch(Exception e){
            printError(e,true);
        }

        return null;
    }

}
