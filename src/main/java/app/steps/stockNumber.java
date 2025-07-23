package app.steps;

import app.classes.StockFile;
import app.classes.UI;
import app.classes.ValidTiffs;
import app.models.modelStockNumber;
import app.models.modelStockToShoot;
import app.objects.*;
import javafx.concurrent.Task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static app.functions.cpuUsage;
import static app.functions.printError;
import static java.lang.Thread.sleep;

public class stockNumber {

    private static final ConcurrentHashMap<String,objStockToShoot> pkObjStockToShoot = new ConcurrentHashMap<>();

    public static void start(){
        try{
            objGlobals.totalThreads = 1;
            pkObjStockToShoot.clear();
            StockFile.prefixNumber.clear();
            StockFile.stockNumberFXCollections.clear();
            ValidTiffs.modelStockToShootFXCollections.clear();
            getStock();
            ValidTiffs.writeToFile();
            StockFile.writeNewFile();
            for(String pk:pkObjStockToShoot.keySet()){
                ValidTiffs.modelStockToShootFXCollections.add(new modelStockToShoot(pkObjStockToShoot.get(pk)));
            }

        }
        catch(Exception e){
            printError(e,true);
        }

    }

    private static void getStock() throws InterruptedException {
        ValidTiffs.getData();

        if (!StockFile.rowObject().isEmpty()) {
            List<Map.Entry<Integer, objStock>> entries = new ArrayList<>(StockFile.rowObject().entrySet());
            entries.sort(Comparator
                .comparing((Map.Entry<Integer, objStock> e) -> e.getValue().logic, Comparator.nullsFirst(String::compareTo))
                .thenComparing(e -> e.getValue().prefix, Comparator.nullsFirst(String::compareTo))
                .thenComparing(Map.Entry::getKey)
            );
            StockFile.rowObject.clear();

            for(Map.Entry<Integer, objStock> entry : entries){

                String prefix = entry.getValue().prefix;

                Integer stockNumber = Integer.parseInt(entry.getValue().stockNumber) + 1;

                if(StockFile.prefixNumber.containsKey(entry.getValue().prefix)){
                    stockNumber = StockFile.prefixNumber.get(prefix)+1;
                    StockFile.prefixNumber.put(prefix,stockNumber);
                }
                else{
                    StockFile.prefixNumber.put(prefix,stockNumber);
                }

                objStock objStock = new objStock(
                    entry.getKey(),
                    entry.getValue().firstBarcode,
                    entry.getValue().lastBarcode,
                    entry.getValue().stockLabel,
                    entry.getValue().obs,
                    entry.getValue().group,
                    entry.getValue().progStart,
                    entry.getValue().progEnd,
                    entry.getValue().logic,
                    prefix,
                    stockNumber == 0 ? "" :String.valueOf(stockNumber),
                    entry.getValue().agency
                );

                modelStockNumber modelStockNumber = new modelStockNumber(objStock);

                StockFile.rowObject.put(entry.getKey(), objStock);
                StockFile.groupObject.computeIfAbsent(entry.getValue().group, _ -> new ArrayList<>()).add(objStock);
                StockFile.stockNumberFXCollections.add(modelStockNumber);

            }

            AtomicInteger count = new AtomicInteger(0);
            Integer total = ValidTiffs.groupObject.size();
            ArrayList<Thread> threads = new ArrayList<>();
            UI.controller.removeSpinner();
            objProgressItem pi = UI.controller.addProgress("Creo i pacchi",total);

            for(String group : ValidTiffs.groupObject.keySet()){

                while (threads.size() >= objGlobals.totalThreads) {
                    refreshThreads(count, threads, pi, group);
                }

                Thread newThread = newThread(group, "stockNumber-"+count.get());
                newThread.start();
                threads.add(newThread);
            }

            while (!threads.isEmpty()) {
                refreshThreads(count, threads, pi, String.valueOf(count));
            }
        }

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
        if(cpuUsage()<70.00){
            objGlobals.totalThreads += 1;
            sleep(500);
        }
        else if(objGlobals.totalThreads > 1)
        {
            objGlobals.totalThreads -= 1;
        }
        objLogTimeline.add("getStock","[ objGlobals.totalThreads ] : "+objGlobals.totalThreads);

    }

    private static Thread newThread(String group, String name){
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
