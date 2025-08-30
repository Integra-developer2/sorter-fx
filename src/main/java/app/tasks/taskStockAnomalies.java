package app.tasks;

import app.Routing;
import app.classes.*;
import app.objects.*;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static app.functions.logError;
import static app.functions.printError;

public class taskStockAnomalies {
    public static void run(){
        Thread t = new Thread(
            new Task<>() {
                @Override protected Void call() {
                    Platform.runLater(() -> {
                        UI.loadViewStatusBar(4,"VERIFICO SE CI SONO ANOMALIE");
                        UI.controller.addSpinner("MI PREPARO");
                        runAfter();
                    });
                    return null;
                }
            }
        );
        t.setName("taskStockAnomalies");
        t.setDaemon(true);
        t.start();
    }

    private static void runAfter() {
        Thread t = new Thread(new Task<>() {
            @Override protected Void call() {
                if(!hasStockAnomalies()){
                    StockFile.writeNewFile();
                    Routing.stockAnomalies="end";
                    Routing.next();
                }
                else{
                    Routing.stockAnomalies = "";
                    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                    scheduler.scheduleAtFixedRate(() -> {
                        if (Routing.stockAnomalies.isEmpty()) {
                            objLogTimeline.add("taskStockAnomalies","[ taskStockAnomalies runAfter ] idle");
                        }
                        else
                        {
                            scheduler.shutdown();

                            Platform.runLater(() -> {
                                if (objGlobals.stop) {
                                    objLogTimeline.add("taskStockAnomalies","[ taskStockAnomalies runAfter ] Stopped by objGlobals.stop");
                                    return;
                                }

                                Routing.stockAnomalies = "";

                                Routing.next();

                            });
                        }
                    }, 0, 300, TimeUnit.MILLISECONDS);

                    Platform.runLater(() -> UI.loadDefault(6,"SISTEMA LE ANOMALIE PRIMA DI CONTINUARE"));
                }
                return null;
            }
        });
        t.setName("taskStockAnomalies");
        t.setDaemon(true);
        t.start();
    }

    private static boolean hasStockAnomalies(){
        try {
            StockFile.stockAnomaliesFXCollections.clear();
            HashMap<String, objStockFile> toApi = new HashMap<>();
            ArrayList<String>allBarcodes=new ArrayList<>();

            UI.controller.removeSpinner();
            if(!AllBlackFiles.hasData){
                UI.controller.addSpinner("Faccio elenco dei file tiff");
                AllBlackFiles.getData();
                UI.controller.removeSpinner();
            }
            objProgressItem pi = UI.controller.addProgress("Verifico se ci sono anomalie", StockFile.rowObject().size());
            int count = 0;
            for (Integer row : StockFile.rowObject().keySet()) {
                objStock obj = StockFile.rowObject(row);
                if(obj.firstBarcode.isEmpty()){
                    StockFile.addStockFXCollections(row, obj,"Primo barcode non puo essere vuoto");
                }
                else if(obj.lastBarcode.isEmpty()){
                    StockFile.addStockFXCollections(row, obj,"Ultimo barcode non puo essere vuoto");
                }
                else{
                    String alternativeFrom = Objects.requireNonNullElse(JobSorter.barcodeAlternative(obj.firstBarcode),"");
                    String alternativeTo = Objects.requireNonNullElse(JobSorter.barcodeAlternative(obj.lastBarcode),"");

                    String ReverseAlternativeFrom = Objects.requireNonNullElse(JobSorter.alternativeBarcode(obj.firstBarcode),"");
                    String ReverseAlternativeTo = Objects.requireNonNullElse(JobSorter.alternativeBarcode(obj.lastBarcode),"");

                    ArrayList<String> barcodes = new ArrayList<>();

                    barcodes.add(obj.firstBarcode);
                    barcodes.add(obj.lastBarcode);

                    if(!alternativeFrom.isEmpty()){
                        barcodes.add(alternativeFrom);
                    }
                    if(!alternativeTo.isEmpty()){
                        barcodes.add(alternativeTo);
                    }
                    if(!ReverseAlternativeFrom.isEmpty()){
                        barcodes.add(ReverseAlternativeFrom);
                    }
                    if(!ReverseAlternativeTo.isEmpty()){
                        barcodes.add(ReverseAlternativeTo);
                    }

                    int indexFrom = Objects.requireNonNullElse(AllBlackFiles.barcodeIndex(obj.firstBarcode),0);
                    int indexTo = Objects.requireNonNullElse(AllBlackFiles.barcodeIndex(obj.lastBarcode),0);
                    if(indexFrom==0){

                        if(!alternativeFrom.isEmpty()){
                            indexFrom = Optional.ofNullable(AllBlackFiles.barcodeIndex(alternativeFrom)).orElse(0);
                            if(indexFrom>0){
                                obj.firstBarcode = alternativeFrom;
                            }
                        }
                    }
                    if(indexTo==0){
                        if(!alternativeTo.isEmpty()){
                            indexTo = Optional.ofNullable(AllBlackFiles.barcodeIndex(alternativeTo)).orElse(0);
                            if(indexTo>0){
                                obj.lastBarcode = alternativeTo;
                            }
                        }
                    }
                    String groupFrom = Objects.requireNonNullElse(JobSorter.barcodeGroup(obj.firstBarcode),"");
                    String groupTo = Objects.requireNonNullElse(JobSorter.barcodeGroup(obj.lastBarcode),"");


                    String error="";
                    if(indexFrom==0){
                        error+="barcode iniziale non trovato nei file tiff \n";
                    }
                    else if(groupFrom.isEmpty()){
                        error+="barcode iniziale non trovato nel job-sorter \n";
                    }

                    if(indexTo==0){
                        error+="barcode finale non trovato nei file tiff \n";
                    }
                    else if(groupTo.isEmpty()){
                        error+="barcode finale non trovato nel job-sorter \n";
                    }

                    if(!groupFrom.isEmpty()&&!groupTo.isEmpty()&&!groupFrom.equals(groupTo)){
                        error+="raggruppamento diverso \n";
                    }

                    toApi.put(obj.firstBarcode,new objStockFile(error,barcodes,indexFrom,indexTo,groupFrom,groupTo,obj,row));
                    allBarcodes.addAll(barcodes);

                }
                UI.controller.refresh(pi,++count);
            }

            JsonObject prefixJson = objApiBarcode(allBarcodes);

            if(prefixJson==null){
                printError(new Exception("API Non raggiungibile"),true);
            }
            else{
                objProgressItem pi2 = UI.controller.addProgress("Prendo i dati dal API",toApi.size());
                int count2 = 0;
                for(String firstBarcode:toApi.keySet()){
                    objStockFile objStockFile = toApi.get(firstBarcode);
                    String prefix="";
                    String stockNumber="";
                    String logic = "";
                    String error = objStockFile.error;
                    String agency = "";
                    String agencyID = "";
                    String cppCode = "";
                    String customer = "";
                    if(error.isEmpty()){
                        for(String barcode: objStockFile.barcodes){
                            JsonObject json;
                            if( objStockFile.groupFrom.contains("NOTFOUND") || objStockFile.groupFrom.contains("NOTDELIVERED") ){
                                json = prefixJson.getAsJsonObject("notFound").getAsJsonObject("NN");
                            }
                            else{
                                json = prefixJson.getAsJsonObject(barcode);
                            }
                            if(json!=null&&json.get("error")==null) {
                                logic = json.get("auto_stock_logic").getAsString();
                                stockNumber = Objects.requireNonNullElse(json.get("auto_stock_number").getAsString(),"0");
                                agency = Objects.requireNonNullElse(json.get("agency").getAsString(),"");
                                agencyID = Objects.requireNonNullElse(json.get("ID").getAsString(),"");
                                cppCode = Objects.requireNonNullElse(json.get("cpp_code").getAsString(),"");
                                customer = Objects.requireNonNullElse(json.get("customer").getAsString(),"");

                                if(logic.equals("lotto")){
                                    prefix = json.get("flow_name").getAsString();
                                }
                                else{
                                    prefix = json.get("auto_stock_prefix").getAsString();
                                }
                                break;
                            }
                        }
                        if(logic.equals("lotto") && prefix.equals("ND") ){
                            error+="Numero lotto non presente \n";
                        }
                        else if(prefix.isEmpty()||stockNumber.isEmpty()||logic.isEmpty()){
                            error+="Barcode non trovato nei prodotti postali \n";
                        }
                    }

                    if(error.isEmpty()){
                        if(objStockFile.indexTo < objStockFile.indexFrom){
                            int tmp = objStockFile.indexTo;
                            objStockFile.indexTo = objStockFile.indexFrom;
                            objStockFile.indexFrom = tmp;
                        }

                        objStockFile.obj.extraFromJobSorter(objStockFile.groupFrom,String.valueOf(objStockFile.indexFrom),String.valueOf(objStockFile.indexTo));
                        objStockFile.obj.extraFromStockFile(logic,prefix,stockNumber,agency,agencyID,cppCode,customer);
                        StockFile.rowObject.put(objStockFile.row, objStockFile.obj);
                    }
                    else{
                        StockFile.addStockFXCollections(objStockFile.row, objStockFile.obj, error);
                    }

                    UI.controller.refresh(pi2,++count2);
                }

            }

        }
        catch (Exception e) {
            printError(e,true);
        }

        return !StockFile.stockAnomaliesFXCollections.isEmpty();
    }

    private static JsonObject objApiBarcode(ArrayList<String> barcodes){
        JsonObject prefixJson = null;
        int tryApi = 0;
        while(prefixJson==null&&tryApi<10){
            objLogTimeline.add("api","api try");
            try{
                prefixJson =  Api.prefix(barcodes);
            }
            catch (Exception e){
                logError("Api.prefix",e);
                try{
                    Thread.sleep(1000);
                }
                catch (InterruptedException e1){
                    printError(e1,true);
                }
                tryApi++;
            }
        }
        return prefixJson;
    }
}
