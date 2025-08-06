package app.classes;

import app.models.modelStockToShoot;
import app.models.modelStockAnomalies;
import app.objects.objGlobals;
import app.objects.objStock;
import app.objects.objValidTiff;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static app.functions.logError;
import static app.functions.printError;

public class ValidTiffs {
    public static ObservableList<modelStockAnomalies> stockAnomaliesFXCollections = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    public static ObservableList<modelStockToShoot> modelStockToShootFXCollections = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    public static ObservableList<modelStockToShoot> modelStockShootingFXCollections = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    public static HashMap<String,objValidTiff> barcodeObject = new HashMap<>();
    public static HashMap<String, List<objValidTiff>> groupObject = new HashMap<>();
    public static String shootingAt="";
    public static int shootingIndex;

    public static void getData() {
        if(barcodeObject.isEmpty()){
            if(objGlobals.validTiffs.exists()){
                if(!objGlobals.validTiffs.delete()){
                    logError("ERROR",new Exception("Can't delete existing file"));
                }
            }
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(objGlobals.validTiffs))) {
                for(String barcode:AllBlackFiles.barcodeFileKeyset()){
                    String file = AllBlackFiles.barcodeFile(barcode);
                    if((new File(file+"-FRONTE.tiff")).exists()||(new File(file+"-RETRO.tiff")).exists()){
                        String group = JobSorter.barcodeGroup(barcode);
                        Integer index = AllBlackFiles.barcodeIndex(barcode);
                        objValidTiff objValidTiff = new objValidTiff(file, group, index, barcode,JobSorter.barcodeRow.get(barcode)[0]);

                        barcodeObject.put(barcode, objValidTiff);
                        groupObject.computeIfAbsent(group, _ -> new ArrayList<>()).add(objValidTiff);
                        bw.write(barcode + ";" + file + ";" + group + ";" + index + "\n");
                    }
                }
            }
            catch (IOException e) {
                printError(new Exception("Non è stato possibile scrivere validTiffs file"),true);
            }

        }
    }

    public static void writeToFile() {
        if(objGlobals.validTiffs.exists()){
            if(!objGlobals.validTiffs.delete()){
                logError("ERROR",new Exception("Can't delete existing file"));
            }
        }
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(objGlobals.validTiffs))) {
            for(String group:groupObject.keySet()){
                for(objValidTiff objValidTiff:groupObject.get(group)){
                    bw.write(
                    objValidTiff.barcode + ";" +
                        objValidTiff.file + ";" +
                        group + ";" +
                        objValidTiff.index + ";" +
                        objValidTiff.logic + ";" +
                        objValidTiff.prefix + ";" +
                        objValidTiff.stockNumber + ";" +
                        objValidTiff.agency  + ";" +
                        objValidTiff.obs  + ";" +
                        objValidTiff.stockLabel + ";" +
                        "\n"
                    );
                    if(!StockFile.groupObject.containsKey(group)){
                        modelStockAnomalies modelStockAnomalies = new modelStockAnomalies(
                            objValidTiff.index,
                            objValidTiff.group,
                            objValidTiff.barcode,
                            objValidTiff.file.replace(objGlobals.targetTiff,""),
                            objValidTiff.logic,
                            objValidTiff.prefix,
                            objValidTiff.agency
                        );
                        ValidTiffs.stockAnomaliesFXCollections.add(modelStockAnomalies);
                    }
                }
            }
        }
        catch (IOException e) {
            printError(new Exception("Non è stato possibile scrivere validTiffs file"),true);
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public static void getFromFile() {
        if(barcodeObject.isEmpty()) {
            if (!objGlobals.validTiffs.exists()) {
                getData();
            }
            else {
                try (BufferedReader br = new BufferedReader(new FileReader(objGlobals.validTiffs))) {
                    String line;
                    while ((line = br.readLine()) != null) //noinspection DuplicatedCode
                    {
                        String[] split = line.split(";");

                        String barcode = "";
                        String file = "";
                        String group = "";
                        String index = "";
                        String logic = "";
                        String prefix = "";
                        String stockNumber = "";
                        String agency = "";
                        String obs = "";
                        String stockLabel = "";

                        if(split.length>0){
                            barcode = Objects.requireNonNullElse(split[0],"");
                        }
                        if(split.length>2){
                            file = Objects.requireNonNullElse(split[1], "");
                        }
                        if(split.length>3){
                            group = Objects.requireNonNullElse(split[2], "");
                        }
                        if(split.length>4){
                            index = Objects.requireNonNullElse(split[3], "");
                        }
                        if(split.length>5){
                            logic = Objects.requireNonNullElse(split[4], "");
                        }
                        if(split.length>6){
                            prefix = Objects.requireNonNullElse(split[5], "");
                        }
                        if(split.length>7){
                            stockNumber = Objects.requireNonNullElse(split[6], "");
                        }
                        if(split.length>8){
                            agency = Objects.requireNonNullElse(split[7], "");
                        }
                        if(split.length>9){
                            obs = Objects.requireNonNullElse(split[9], "");
                        }
                        if(split.length>10){
                            stockLabel = Objects.requireNonNullElse(split[10], "");
                        }

                        if((new File(file+"-FRONTE.tiff")).exists()||(new File(file+"-RETRO.tiff")).exists()){
                            objValidTiff objValidTiff = new objValidTiff(file, group, Integer.parseInt(index), barcode, agency);

                            if (!logic.isEmpty() && !logic.equals("null")) {
                                objValidTiff.logic = logic;
                            }
                            if (!prefix.isEmpty() && !prefix.equals("null")) {
                                objValidTiff.prefix = prefix;
                            }
                            if (!stockNumber.isEmpty() && !stockNumber.equals("null")) {
                                objValidTiff.stockNumber = stockNumber;
                            }
                            if (!obs.isEmpty() && !obs.equals("null")) {
                                objValidTiff.obs = obs;
                            }
                            if (!stockLabel.isEmpty() && !stockLabel.equals("null")) {
                                objValidTiff.stockLabel = stockLabel;
                            }

                            barcodeObject.put(barcode, objValidTiff);
                            groupObject.computeIfAbsent(group, _ -> new ArrayList<>()).add(objValidTiff);
                        }

                    }
                }
                catch (IOException e) {
                    printError(e, true);
                }
            }
        }
    }

    public static void assignToStockRow(objValidTiff objValidTiff, objStock objStock){
        objValidTiff.logic = objStock.logic;
        objValidTiff.prefix = objStock.prefix;
        objValidTiff.stockNumber = objStock.stockNumber;
        objValidTiff.obs = objStock.obs;
        objValidTiff.stockLabel = objStock.stockLabel;
    }

}
