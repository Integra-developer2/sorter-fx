package app.classes;

import app.models.modelStock;
import app.models.modelStockNumber;
import app.objects.objStock;
import app.objects.objGlobals;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static app.functions.printError;

public class StockFile {
    public static ObservableList<modelStockNumber> stockNumberFXCollections = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    public static ObservableList<modelStock> stockAnomaliesFXCollections = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    public static HashMap<String,Integer> prefixNumber = new HashMap<>();
    public static HashMap<String,List<objStock>> groupObject = new HashMap<>();
    private static boolean hasData=false;
    public static HashMap<Integer, objStock> rowObject = new HashMap<>();

    public static HashMap<Integer, objStock> rowObject(){
        getData();
        return rowObject;
    }

    public static objStock rowObject(Integer row){
        getData();
        return rowObject.get(row);
    }

    private static void getData(){
        if(!hasData){
            try{
                readFromFile();
            }
            catch(Exception e){
                printError(e,true);
            }

        }
    }

    private static void readFromFile() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(LatestFileEtichette()))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (count>0) {
                    String[] values = line.split(";");
                    if (values.length > 0) {
                        String firstBarcode = values[0].toUpperCase().replace(" ", "");
                        String lastBarcode = values[1].toUpperCase().replace(" ", "");
                        String reference = values[2].toUpperCase();
                        String obs = (values.length > 3) ? values[3].toUpperCase() : "";
                        String cassetto = (values.length > 4) ? values[4].toUpperCase() : "";
                        String group = (values.length > 5) ? values[5].toUpperCase() : "";
                        String progStart = (values.length > 6) ? Optional.ofNullable(values[6]).orElse(""):"";
                        String progEnd = (values.length > 7) ? Optional.ofNullable(values[7]).orElse(""):"";
                        String logic = (values.length > 8) ? values[8].toUpperCase() : "";
                        String prefix = (values.length > 9) ? values[9].toUpperCase() : "";
                        String stockNumber = (values.length > 10) ? Optional.ofNullable(values[10]).orElse(""):"";
                        String agency = (values.length > 11) ? Optional.ofNullable(values[11]).orElse(""):"";
                        String agencyID = (values.length > 12) ? Optional.ofNullable(values[12]).orElse(""):"";
                        String cppCode = (values.length > 13) ? Optional.ofNullable(values[13]).orElse(""):"";
                        String customer = (values.length > 14) ? Optional.ofNullable(values[14]).orElse(""):"";
                        if (!(firstBarcode.isEmpty() && lastBarcode.isEmpty() && reference.isEmpty() && obs.isEmpty())) {
                            rowObject.put(count,new objStock(count, firstBarcode, lastBarcode, reference, obs, cassetto, group, progStart, progEnd,logic,prefix,stockNumber,agency,agencyID,cppCode,customer));
                        }
                    }
                }
                count++;
            }
            hasData=true;
        }
    }

    public static File LatestFileEtichette() {
        File folder = new File(objGlobals.etichetteFolder);

        return Arrays.stream(Objects.requireNonNull(folder.listFiles(File::isFile)))
            .max(Comparator.comparingLong(File::lastModified))
            .orElse(null);
    }

    public static void writeNewFile() {
        String filename = newTargetEtichetta();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("PRIMO BARCODE;ULTIMO BARCODE;SCATOLO;PEDANA;CASSETTO;GRUPPO;PROG INIZIO;PROG FINE;LOGICA;PREFISSO;PACCO;AGENZIA;ID AGENZIA;CODICE PRODOTTO;CLIENTE");
            writer.newLine();
            for (Map.Entry<Integer, objStock> entry : rowObject.entrySet()) {
                objStock data = entry.getValue();
                String row = String.join(";",
                    data.firstBarcode,
                    data.lastBarcode,
                    data.stockLabel,
                    data.obs,
                    data.cassetto,
                    data.group,
                    Objects.requireNonNullElse(String.valueOf(data.progStart),""),
                    Objects.requireNonNullElse(String.valueOf(data.progEnd),""),
                    Objects.requireNonNullElse(data.logic,""),
                    Objects.requireNonNullElse(data.prefix,""),
                    Objects.requireNonNullElse(data.stockNumber,""),
                    Objects.requireNonNullElse(data.agency,""),
                    Objects.requireNonNullElse(data.agencyID,""),
                    Objects.requireNonNullElse(data.cppCode,""),
                    Objects.requireNonNullElse(data.customer,"")
                );
                writer.append(row);
                writer.newLine();
            }
        } catch (IOException e) {
            printError(e, true);
        }
    }

    public static String newTargetEtichetta(){
        File newFileName = getEtichetteFile();
        String ret = newFileName.getAbsolutePath();
        objGlobals.targetEtichette=ret;
        return ret;
    }

    private static File getEtichetteFile() {
        File file = LatestFileEtichette();
        String filename = file.getName();
        String ret = filename;
        if(!filename.endsWith("-fixed.csv")){
            ret = filename.replace(".csv","_1-fixed.csv");
        }
        else{
            Pattern pattern = Pattern.compile("_(\\d+)-fixed\\.csv");
            Matcher matcher = pattern.matcher(filename);
            if(matcher.find()){
                int count = Integer.parseInt(matcher.group(1));
                count = count + 1;
                ret = filename.replace("_"+matcher.group(1)+"-fixed.csv","_"+count+"-fixed.csv");
            }
        }
        return new File(file.getParent(), ret);
    }

    public static void addStockFXCollections(Integer row, objStock obj, String error){
        stockAnomaliesFXCollections.add(new modelStock(row,obj.firstBarcode,obj.lastBarcode,obj.stockLabel, obj.obs,obj.cassetto,obj.logic,obj.prefix,obj.stockNumber,error));
    }

}
