package app.steps;

import app.classes.Pc;
import app.classes.StockFile;
import app.classes.UI;
import app.classes.ValidTiffs;
import app.models.modelStockFile;
import app.models.modelStockNumber;
import app.models.modelStockToShoot;
import app.objects.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static app.functions.printError;
import static java.lang.Thread.sleep;

public class stockNumber {

    private static final ConcurrentHashMap<String,objStockToShoot> pkObjStockToShoot = new ConcurrentHashMap<>();

    public static void start(){
        try{
            objGlobals.totalThreads = 1;
            pkObjStockToShoot.clear();
            StockFile.prefixNumber.clear();
            StockFile.stockFileFXCollections.clear();
            StockFile.stockNumberFXCollections.clear();
            ValidTiffs.modelStockToShootFXCollections.clear();
            getStock();
            checkStockFile();
            if(!StockFile.stockFileFXCollections.isEmpty()){
                Comparator<modelStockFile> byGroup =
                        Comparator.comparing(msf -> msf.group().get(),
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

                FXCollections.sort(StockFile.stockFileFXCollections, byGroup);
            }
            else{
                getStockByTiff();
                ValidTiffs.writeToFile();
                for(String pk:pkObjStockToShoot.keySet()){
                    ValidTiffs.modelStockToShootFXCollections.add(new modelStockToShoot(pkObjStockToShoot.get(pk)));
                }
            }

        }
        catch(Exception e){
            printError(e,true);
        }

    }

    private static void checkStockFile(){
        for(String group:StockFile.groupObject.keySet()){
            boolean groupHasError = false;
            List<objStock> objStockList =  StockFile.groupObject.get(group);
            int count = 1, i = -1, n = objStockList.size();

            for(objStock objStock:objStockList){
                String error = "";
                i++;
                objStock prev = i > 0 ? objStockList.get(i - 1) : null;
                objStock next = i + 1 < n ? objStockList.get(i + 1) : null;

                if(objStock.pacco == null || objStock.pacco.isEmpty()){
                    if(objStockList.size()==1){
                        objStock.pacco = "1";
                    }
                    else{
                        Matcher m = Pattern.compile("\\bPACCO\\D*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(objStock.stockLabel);
                        String pacco = m.find() ? m.group(1) : null;
                        if(pacco == null){
                            error=error(error,"Pacco non è presente");
                        }
                        else{
                            int intPacco = Integer.parseInt(pacco);
                            if(intPacco != count){
                                error=error(error,"Pacco non è in ordine");
                                groupHasError = true;
                            }
                            else {
                                objStock.pacco = pacco;
                            }
                        }
                    }
                }

                if(objStock.cassetto == null || objStock.cassetto.isEmpty()){
                    Matcher m = Pattern.compile("\\bCASSETTO\\D*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(objStock.stockLabel);
                    String cassetto = m.find() ? m.group(1) : null;
                    if(cassetto == null){
                        error=error(error,"Cassetto non è presente");
                    }
                    else{
                        objStock.cassetto = cassetto;
                    }
                }

                if(prev != null){
                    if(Integer.parseInt(objStock.progStart) < Integer.parseInt(prev.progEnd)){
                        error = error(error,"ProgInizio in sovrapposizione");
                    }
                }

                if(next != null){
                    if(Integer.parseInt(objStock.progEnd) > Integer.parseInt(next.progStart)){
                        error = error(error,"ProgFine in sovrapposizione");
                    }
                }

                if(groupHasError && error.isEmpty()){
                    error += "-";
                }

                if(!error.isEmpty()){
                    StockFile.stockFileFXCollections.add(new modelStockFile(objStock,error));
                }

                count++;

            }

        }

    }

    private static String error(String error, String message){
        if(!error.isEmpty()){
            error += ", ";
        }
        error+=message;
        return error;
    }

    private static void getStockByTiff() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        Integer total = ValidTiffs.groupObject.size();
        ArrayList<Thread> threads = new ArrayList<>();
        UI.controller.removeSpinner();
        objProgressItem pi = UI.controller.addProgress("Creo i pacchi",total);

        for(String group : ValidTiffs.groupObject.keySet()){

            while (threads.size() >= objGlobals.totalThreads) {
                refreshThreads(count, threads, pi, group);
            }

            Thread newStockByTiffThread = newStockByTiffThread(group, "stockNumber-"+count.get());
            newStockByTiffThread.start();
            threads.add(newStockByTiffThread);
        }

        while (!threads.isEmpty()) {
            refreshThreads(count, threads, pi, String.valueOf(count));
        }
    }

    private static void getStock() {
        ValidTiffs.getData();

        if (StockFile.rowObject().isEmpty()) {

            printError(new Exception("Stock File is empty"),true);

        }
        else{

            List<Map.Entry<Integer, objStock>> entries = entries();

            StockFile.rowObject.clear();
            StockFile.groupObject.clear();
            StockFile.prefixNumber.clear();

            for(Map.Entry<Integer, objStock> entry : entries){

                objStock entryValue = entry.getValue();

                String prefix = entryValue.prefix;

                String entryStockNumber = entryValue.stockNumber;

                if(entryStockNumber.isEmpty()){
                    printError(new Exception("Stock Number is empty"),true);
                }
                assert !entryStockNumber.isEmpty();

                int intEntryStockNumber = Integer.parseInt(entryStockNumber);

                Integer stockNumber = intEntryStockNumber == 0 || intEntryStockNumber == 1 ? 1 : intEntryStockNumber + 1;

                if(StockFile.prefixNumber.containsKey(entryValue.prefix)){
                    stockNumber = StockFile.prefixNumber.get(prefix)+1;
                    StockFile.prefixNumber.put(prefix,stockNumber);
                }
                else{
                    StockFile.prefixNumber.put(prefix,stockNumber);
                }

                objStock objStock = new objStock(
                    entry.getKey(),
                    entryValue.firstBarcode,
                    entryValue.lastBarcode,
                    entryValue.stockLabel,
                    entryValue.obs,
                    entryValue.cassetto,
                    entryValue.pacco,
                    entryValue.group,
                    entryValue.progStart,
                    entryValue.progEnd,
                    entryValue.logic,
                    prefix,
                    stockNumber == 0 ? "" :String.valueOf(stockNumber),
                    entryValue.agency,
                    entryValue.agencyID,
                    entryValue.cppCode,
                    entryValue.customer
                );

                modelStockNumber modelStockNumber = new modelStockNumber(objStock);

                StockFile.rowObject.put(entry.getKey(), objStock);
                StockFile.groupObject.computeIfAbsent(entryValue.group, _ -> new ArrayList<>()).add(objStock);
                StockFile.stockNumberFXCollections.add(modelStockNumber);

            }

        }

    }

    private static List<Map.Entry<Integer, objStock>> entries(){
        List<Map.Entry<Integer, objStock>> entries = new ArrayList<>(StockFile.rowObject().entrySet());
        entries.sort(
                Comparator
                        .comparing((Map.Entry<Integer, objStock> e) -> e.getValue().logic, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(e -> e.getValue().prefix, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(
                                e -> {
                                    String prog = e.getValue().progStart;
                                    return prog == null ? null : Integer.parseInt(prog);
                                },
                                Comparator.nullsFirst(Integer::compareTo)
                        )
                        .thenComparing(Map.Entry::getKey, Comparator.nullsFirst(Comparator.naturalOrder()))
        );

        return entries;
    }

    private static void refreshThreads(AtomicInteger count, ArrayList<Thread> threads, objProgressItem pi, String text) throws InterruptedException {
        Iterator<Thread> it = threads.iterator();
        while (it.hasNext()) {
            Thread t = it.next();
            if (!t.isAlive()) {
                it.remove();
                UI.controller.refresh(pi, count.incrementAndGet());
                objLogTimeline.add("refreshThreads","[ getStock ] done "+text);
            }
            else{
                objLogTimeline.add("refreshThreads","[ getStock ] running "+text);
            }
        }
        if(Pc.usage.get("cpu")<90.00 && Pc.usage.get("disk")<90.00){
            objGlobals.totalThreads += 1;
            sleep(500);
        }
        else if(objGlobals.totalThreads > 1)
        {
            objGlobals.totalThreads -= 1;
        }
        objLogTimeline.add("getStock","[ objGlobals.totalThreads ] : "+objGlobals.totalThreads);

    }

    private static Thread newStockByTiffThread(String group, String name){
        Thread t = new Thread(new Task<Void>() {
            @Override
            protected Void call() {

                List<objValidTiff> objValidTiffs = ValidTiffs.groupObject.get(group);
                List<objStock> objStocks = StockFile.groupObject.get(group);

                for(objValidTiff objValidTiff : objValidTiffs){
                    objStock previous = null;
                    objStock next = null;
                    objStock first = null;
                    objStock last = null;

                    for(objStock objStock  : objStocks){
                        int progStart = Integer.parseInt(objStock.progStart);
                        int progEnd = Integer.parseInt(objStock.progEnd);

                        if( ( objValidTiff.index >= progStart && objValidTiff.index <= progEnd ) || objStocks.size()==1 ){
                            ValidTiffs.assignToStockRow(objValidTiff,objStock);
                            break;
                        }
                        else if(objValidTiff.index <= progStart && previous != null && next == null){
                            next = objStock;
                        }
                        else if(next == null){
                            previous = objStock;
                        }

                        if(first==null){
                            first = objStock;
                        }

                        last  = objStock;

                    }

                    if(objValidTiff.prefix==null){

                        if(last!=null&&objValidTiff.index >= Integer.parseInt(last.progEnd)){
                            ValidTiffs.assignToStockRow(objValidTiff,last);
                            last.progEnd = String.valueOf(objValidTiff.index);
                            last.lastBarcode = objValidTiff.barcode;
                            StockFile.rowObject.get(last.row).lastBarcode = objValidTiff.barcode;
                            StockFile.rowObject.get(last.row).progEnd = String.valueOf(objValidTiff.index);
                        }
                        else if(first!=null&&objValidTiff.index <= Integer.parseInt(first.progEnd)){
                            ValidTiffs.assignToStockRow(objValidTiff,first);
                            first.progStart = String.valueOf(objValidTiff.index);
                            first.firstBarcode = objValidTiff.barcode;
                            StockFile.rowObject.get(first.row).firstBarcode = objValidTiff.barcode;
                            StockFile.rowObject.get(first.row).progStart = String.valueOf(objValidTiff.index);
                        }
                        else if(previous!=null && objValidTiff.index >= Integer.parseInt(previous.progEnd) && next!=null && objValidTiff.index <= Integer.parseInt(next.progEnd)){
                            String pk = previous.row + "-" + next.row;
                            objStock finalPrevious = previous;
                            objStock finalNext = next;
                            pkObjStockToShoot.computeIfAbsent(pk, _ -> new objStockToShoot(
                                finalPrevious.group,
                                finalPrevious.row,
                                finalPrevious.stockLabel,
                                finalPrevious.obs,
                                finalNext.row,
                                finalNext.stockLabel,
                                finalNext.obs
                            ));

                            pkObjStockToShoot.get(pk).objValidTiff.add(objValidTiff);

                        }
                    }

                }


                return null;
            }
        });
        t.setName(name);
        t.setDaemon(true);
        return t;
    }


}
