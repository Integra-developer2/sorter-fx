package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static app.functions.printError;
import app.o3_sorter_stock.objDoneStock;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class objGlobals {
    public static String version = "SORTER-FX 1.0.1";
    public static final int PRINT_AT=100;
    public static int totalThreadsMoveFiles=300;
    public static int totalThreadsGray=100;
    public static int totalThreadsStock=300;
    public static int frontAngle=90;
    public static int backAngle=270;
    public static String startingFrom="";
    public static String scanditProgram="it.smaart.sorter.stock";
    public static String scanditKey="test.key";
    public static String sorterExportValoreDaSorter="C";
    public static String[] raggruppamentoJobSorter={"C"};
    public static String nomeCartellaPdf="{SC}/{F6}-sorter-{D1::uppercase::noNumber}-{F5}-{ANNO}-{NUMERO_PACCO}.pdf";
    public static String colonnaBarcode="B";
    public static String workingFolder;
    public static String sourceEtichette;
    public static String targetEtichette;
    public static ArrayList<String> sourceJobSorter;
    public static String sourceGray;
    public static String sourceTiff;
    public static String stockPrefix;
    public static String masterCode;
    public static int stockNumber=0;
    public static String targetGray;
    public static String targetTiff;
    public static String logFolder;
    public static String anomalyFolderGray;
    public static String anomalyFolderStock1;
    public static String anomalyFolderStock2;
    public static String anomalyLogFolder;
    public static String anomalyLog;
    public static String errorLog;
    public static String jogSorterFolder;
    public static String etichetteFolder;
    public static String bwDir;
    public static String logGray;
    public static String logBlack;
    public static String logStep;
    public static HashMap<Integer,objFileEtichette> fileEtichette = new HashMap<>();
    public static ArrayList<String>barcodesFromFiles=new ArrayList<>();
    public static objErrorMap errorMap;
    public static String stockTxt;
    public static Double progressGray=0.0000;
    public static Double progressStock=0.0000;
    public static boolean debug=false;
    public static ArrayList<String> tiffFiles = new ArrayList<>();
    public static ArrayList<String> grayFiles = new ArrayList<>();
    public static String pdfFolder;
    public static String logStock;
    public static boolean rotateHorizontal=true;
    public static boolean stop=false;
    public static String logGraytxt;
    public static String notExpectedFolder;
    public static boolean hasLoadError=false;
    public static File targetEtichetteLog;
    public static File allBlackFiles;
    public static String sorterExport;
    public static String outputFolder;
    public static boolean skipAnomalies=false;
    @SuppressWarnings("exports")
    public static Scene scene;
    @SuppressWarnings("exports")
    public static FXMLLoader fxmlLoader;
    @SuppressWarnings("exports")
    public static Parent parent;
    @SuppressWarnings("exports")
    public static Stage stage;
    public static File sourceEtichetteFile;
    public static File sourceJobSorterFile;
    public static File sourceGrayFile;
    public static File sourceTiffFile;
    public static File stockPrefixFile;
    public static File stockNumberFile;

    public static void variables(){
        try{
            outputFolder=outputFolder();
            targetGray=targetGray();
            targetTiff=targetTiff();
            logFolder=logFolder();
            sourceEtichetteFile=sourceEtichetteFile();
            sourceJobSorterFile=sourceJobSorterFile();
            sourceGrayFile=sourceGrayFile();
            sourceTiffFile=sourceTiffFile();
            stockPrefixFile=stockPrefixFile();
            stockNumberFile=stockNumberFile();
            sourceEtichette=readFromFile(sourceEtichetteFile);
            sourceJobSorter=sourceJobSorter();
            sourceGray=readFromFile(sourceGrayFile);
            sourceTiff=readFromFile(sourceTiffFile);
            stockPrefix=readOnce(stockPrefixFile);
            stockNumber=stockNumber();
            anomalyLog=anomalyLog();
            errorLog=errorLog();
            jogSorterFolder=jogSorterFolder();
            etichetteFolder=etichetteFolder();
            anomalyFolderGray=anomalyFolderGray();
            anomalyFolderStock1=anomalyFolderStock1();
            anomalyFolderStock2=anomalyFolderStock2();
            anomalyLogFolder=anomalyLogFolder();
            bwDir=bwDir();
            logGray=logGray();
            logBlack=logBlack();
            logStep=logStep();
            errorMap= new objErrorMap();
            pdfFolder=pdfFolder();
            stockTxt=stockTxt();
            logStock=logStock();
            logGraytxt=logGraytxt();
            notExpectedFolder=notExpectedFolder();
            targetEtichetteLog=targetEtichetteLog();
            targetEtichette=targetEtichette();
            allBlackFiles=allBlackFiles();
            sorterExport=sorterExport();

            objDoneStock.add(String.valueOf(stockNumber));
        } catch (Exception e) { printError(e,true);}
    }

    public static File sourceEtichetteFile(){return new File(logFolder,"sourceEtichette");}
    public static File sourceJobSorterFile(){return new File(logFolder,"sourceJobSorter");}
    public static File sourceGrayFile(){return new File(logFolder,"sourceGray");}
    public static File sourceTiffFile(){return new File(logFolder,"sourceTiff");}
    public static File stockPrefixFile(){return new File(logFolder,"stockPrefix");}
    public static File stockNumberFile(){return new File(logFolder,"stockNumber");}

    public static boolean hasEtichette(){
        return (!sourceEtichette.isEmpty()||targetEtichetteLog.exists());
    }

    public static boolean hasJobSorter(){
        return (!sourceJobSorter.isEmpty() || new File(objGlobals.jogSorterFolder).exists());
    }

    public static boolean hasInputs(){
        variables();
        return hasEtichette()&&hasJobSorter()&&!stockPrefix.isEmpty()&&stockNumber>0;
    }

    public static String logFolder() throws Exception{
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
        } catch (IOException e) { throw e;}
        return logFolder;
    }

    public static ArrayList<String> sourceJobSorter(){
        ArrayList<String> ret =  new ArrayList<>();
        if(sourceJobSorterFile.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(sourceJobSorterFile))) {
                String line;
                while ((line=reader.readLine())!=null) {
                    File fileLine = new File(line);
                    if(fileLine.exists()){
                        ret.add(fileLine.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                printError(e,true);
            }
        }
        return ret;
    }

    public static Integer stockNumber(){
        String file = readOnce(stockNumberFile);
        Integer ret = 0;
        if(!file.isEmpty()){
            ret = Integer.valueOf(file);
        }
        return ret;
    }

    public static void newtargetEtichette(String newtargetEtichette){
        if(targetEtichetteLog.exists()){
            targetEtichetteLog.delete();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetEtichetteLog))) {
            writer.append(newtargetEtichette);
            targetEtichette=newtargetEtichette;
        } catch (Exception e) {
            printError(e,true);
        }
    }

    public static File allBlackFiles() {
        return new File(logFolder, "allBlackFiles");
    }

    public static File targetEtichetteLog() {
        return new File(logFolder, "targetEtichetteLog");
    }

    public static String notExpectedFolder() {
        File file = new File(workingFolder, "NON PREVISTI NEL FILE ETICHETTE");
        return file.toString();
    }

    public static String logGraytxt() {
        File file = new File(logGray, "LOGGRAY.txt");
        return file.toString();
    }

    public static String anomalyLogFolder() {
        File file = new File(logFolder, "ANOMALIE");
        return file.toString();
    }

    public static String outputFolder(){
        File file = new File(workingFolder, "RISULTATO");
        return file.toString();
    }

    public static String pdfFolder() {
        File file = new File(outputFolder, "PDFS");
        return file.toString();
    }

    public static String sorterExport() {
        File file = new File(outputFolder, "sorterExport_");
        return file.toString();
    }

    public static String stockTxt() {
        File file = new File(outputFolder, "pacco_primo_e_ultimo.txt");
        return file.toString();
    }

    public static String logStep() {
        File file = new File(new File(logFolder, "STEP_CONTROL"), "STEPS");
        return file.toString();
    }

    public static String logGray() {
        File file = new File(new File(logFolder, "STEP_CONTROL"), "objConcurrentGrayController");
        return file.toString();
    }

    public static String logBlack() {
        File file = new File(new File(logFolder, "STEP_CONTROL"), "objConcurrentBlackController");
        return file.toString();
    }

    public static String logStock() {
        File file = new File(new File(logFolder, "STEP_CONTROL"), "ThreadPdf");
        return file.toString();
    }

    public static String targetGray() {
        File file = new File(new File(workingFolder, "FILE_TEMPORANEI"), "GRIGI");
        return file.toString();
    }

    public static String targetTiff() {
        File file = new File(new File(workingFolder, "FILE_TEMPORANEI"), "TIFF");
        return file.toString();
    }

    public static String anomalyLog() {
        File file = new File(logFolder, "ANOMALIE.txt");
        return file.toString();
    }

    public static String errorLog() {
        File file =new File(logFolder, "error.log");
        return file.toString();
    }

    public static String anomalyFolderGray() {
        File file =new File(workingFolder, "ANOMALIE GRIGI");
        return file.toString();
    }

    public static String anomalyFolderStock1() {
        File file =new File(workingFolder, "ANOMALIE ETICHETTE 1");
        return file.toString();
    }

    public static String anomalyFolderStock2() {
        File file =new File(workingFolder, "ANOMALIE ETICHETTE 2");
        return file.toString();
    }

    public static String jogSorterFolder() {
        File file =new File(new File(workingFolder, "FILE_TEMPORANEI"),"JOB-SORTER");
        return file.toString();
    }

    public static String etichetteFolder() {
        File file =new File(new File(workingFolder, "FILE_TEMPORANEI"),"ETICHETTE");
        return file.toString();
    }

    public static String readFromFile(File file){
        if(file.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line=reader.readLine())!=null) {
                    File fileLine = new File(line);
                    if(fileLine.exists()){
                        return fileLine.getAbsolutePath();
                    }
                }
            } catch (Exception e) {
                printError(e,true);
            }
        }
        return "";
    }

    public static String readOnce(File file){
        if(file.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line=reader.readLine())!=null) {
                    if(!line.isEmpty()){
                        return line;
                    }
                }
            } catch (Exception e) {
                printError(e,true);
            }
        }
        return "";
    }

    public static String targetEtichette() {
        if(targetEtichetteLog.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(targetEtichetteLog))) {
                String line;
                while ((line=reader.readLine())!=null) {
                    File file = new File(line);
                    if(file.exists()){
                        sourceEtichette=file.getAbsolutePath();
                        return file.toString();
                    }
                }
            } catch (Exception e) {
                printError(e,true);
            }
        }
        if(!sourceEtichette.isEmpty()){
            File folderFrom = new File(sourceEtichette);
            return  sourceEtichette.replace(folderFrom.getParent(), etichetteFolder);
        }
        return "";
    }

    public static String bwDir() {
        File file =new File(logFolder, "BW");
        return file.toString();
    }

    public static String newTargetEtichetta(){
        File file = new File(targetEtichette);
        String fileParent = file.getParent();
        String filename = file.getName();
        String extension = filename.substring(filename.lastIndexOf('.') + 1);
        String baseName = filename.replace("."+extension, "");
        File newFileName = new File(fileParent, baseName+"_fixed.csv");
        while (newFileName.exists()) {
            newFileName = new File(fileParent, newFileName.getName().replace("."+extension, "")+"_fixed.csv");
        }
        return newFileName.getAbsolutePath();
    }

}
