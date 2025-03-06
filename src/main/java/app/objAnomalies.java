package app;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static app.functions.deleteFolder;
import static app.functions.makeStockNumber;
import static app.functions.moveErrors;
import static app.functions.moveStock2;
import static app.functions.printError;
import static app.functions.readEtichette;
import app.o1_sorter_move_files.functions;
import static app.o3_sorter_stock.functions.getBarcodeFromBlackFile;
import static app.o3_sorter_stock.functions.getGroup;
import static app.o3_sorter_stock.functions.getIndexFromBlackFile;
import static app.o3_sorter_stock.functions.indexMax;
import static app.o3_sorter_stock.functions.indexMin;
import static app.o3_sorter_stock.functions.readBlackDir;
import static app.o3_sorter_stock.functions.readJobSorterCSV;

import app.o3_sorter_stock.objBlackFiles;
import app.o3_sorter_stock.objDoneStockNumber;
import app.o3_sorter_stock.objEtichetta;
import app.o3_sorter_stock.objJobSorterGrouped;
import app.o3_sorter_stock.objSorterExport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class objAnomalies extends functions{
    public static String noSpace="";
    public static ConcurrentLinkedQueue<String> moveFiles = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<String> gray = new ConcurrentLinkedQueue<>();
    public static ObservableList<modelEtichette> stock = FXCollections.observableArrayList();
    public static ObservableList<modelEtichette2> stock2 = FXCollections.observableArrayList();
    public static ArrayList<String> stock2List = new ArrayList<>();
    public static HashMap<String,ArrayList<String>> unExpectedGroups = new HashMap<>();

    public static void addMoveFiles(String anomalyFile){
        if(!moveFiles.contains(anomalyFile)){
            moveFiles.add(anomalyFile);
        }
    }

    public static boolean hasGrayAnomaly(){
        File anomalyFile = new File(objGlobals.anomalyFolderGray);
        if(anomalyFile.exists()){
            try {
                Files.walkFileTree(Paths.get(objGlobals.anomalyFolderGray), new SimpleFileVisitor<Path>()  {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        String strPath=path.toString();
                        if(strPath.endsWith(".tiff")){
                            objAnomalies.gray.add(strPath);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                printError(e,true);
            }
        }
        return !objAnomalies.gray.isEmpty();
    }

    public static void addGray(String anomalyFile){
        if(!gray.contains(anomalyFile)){
            gray.add(anomalyFile);
        }
    }

    public static void grayReset(){
        gray.clear();
    }

    public static void addStock(Integer row, objFileEtichette obj, String error){
        stock.add(new modelEtichette(row,obj.firstBarcode,obj.lastBarcode,obj.reference, obj.obs,error));
    }

    public static void clear(){
        moveFiles.clear();
        gray.clear();
        stock.clear();
        stock2.clear();
        stock2List.clear();
        unExpectedGroups.clear();
    }

    public static void addStock2(Integer id,objNotExpected obj){
        stock2.add(new modelEtichette2(id, obj));
    }

    public static boolean hasStockAnomaly(){
        try {
            objAnomalies.clear();
            objEtichetta.clear();
            objDoneStockNumber.clear();
            objSorterExport.clear();
            objBlackFiles.clear();
            readJobSorterCSV();
            readEtichette();
            readBlackDir();
            for (Integer row : objGlobals.fileEtichette.keySet()) {
                objFileEtichette obj = objGlobals.fileEtichette.get(row);
                if(obj.firstBarcode.isEmpty()){
                    objAnomalies.addStock(row, obj,"Primo barcode non puo essere vuoto");
                }
                else if(obj.lastBarcode.isEmpty()){
                    objAnomalies.addStock(row, obj,"Ultimo barcode non puo essere vuoto");
                }
                else{
                    int indexFrom = indexMin(obj.firstBarcode);
                    int indexTo = indexMax(obj.lastBarcode);
                    if(indexFrom==0){
                        String alternative = objJobSorterGrouped.getAlternative(obj.firstBarcode);
                        if(!alternative.isEmpty()){
                            indexFrom = indexMin(alternative);
                            obj.firstBarcode = alternative;
                        }
                    }
                    if(indexTo==0){
                        String alternative = objJobSorterGrouped.getAlternative(obj.lastBarcode);
                        if(!alternative.isEmpty()){
                            indexTo = indexMax(alternative);
                            obj.lastBarcode = alternative;
                        }
                    }
                    String groupFrom = getGroup(obj.firstBarcode);
                    String groupTo = getGroup(obj.lastBarcode);
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

                    if(error.isEmpty()){
                        if(indexTo < indexFrom){
                            int tmp = indexTo;
                            indexTo = indexFrom;
                            indexFrom = tmp;
                        }
                        objEtichetta.add(groupFrom,indexFrom,indexTo);
                        obj.extra(groupFrom,indexFrom,indexTo);
                        objGlobals.fileEtichette.put(row,obj);
                    }
                    else{
                        objAnomalies.addStock(row, obj, error);
                    }
                }
            }
        } catch (Exception e) {
            printError(e,true);
        }
        return !objAnomalies.stock.isEmpty();
    }

    public static boolean hasStockAnomaly2(){
        try {
            makeStockNumber();
            moveErrors();
            if(hasStock2List()){
                stockAnomaly2();
            }
        } catch (Exception e) {
            printError(e, true);
        }
        return !stock2.isEmpty()||!unExpectedGroups.isEmpty();
    }

    private static boolean hasStock2List(){
        File folderFile = new File(objGlobals.anomalyFolderStock2);
        if(folderFile.exists()){
            try {
                Files.walkFileTree(Paths.get(folderFile.toString()), new SimpleFileVisitor<Path>()  {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        String strPath=path.toString();
                        String filename = strPath.replace("-FRONTE.tiff", "").replace("-RETRO.tiff", "");
                        if(strPath.endsWith(".tiff")&&!objAnomalies.stock2List.contains(filename)){
                            objAnomalies.stock2List.add(filename);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                printError(e, true);
            }
            if(objAnomalies.stock2List.isEmpty()){
                deleteFolder(folderFile.toString());
            }
        }
        return !objAnomalies.stock2List.isEmpty();
    }

    private static void stockAnomaly2(){
        HashMap<String,Integer>originalProgEnd = new HashMap<>();
        HashMap<String, ArrayList<objFileEtichette>> etichetteByGroup = new HashMap<>();
        HashMap<Integer, objNotExpected> notExpected = new HashMap<>();
        Integer id = 0;

        HashMap<String,Integer>stock2ListWithIndex = new HashMap<>();
        for (String file : objAnomalies.stock2List) {
            int index = Integer.parseInt(getIndexFromBlackFile(file).replace("-", ""));
            stock2ListWithIndex.put(file,index);
        }
        LinkedHashMap<String, Integer> sortedStock2ListWithIndex = stock2ListWithIndex.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1, e2) -> e1,LinkedHashMap::new));

        for (String file : sortedStock2ListWithIndex.keySet()) {
            Integer index = sortedStock2ListWithIndex.get(file);
            etichetteByGroup.clear();
            for (Integer row : objGlobals.fileEtichette.keySet()) {
                objFileEtichette obj = objGlobals.fileEtichette.get(row);
                etichetteByGroup.computeIfAbsent(obj.group, k -> new ArrayList<>()).add(obj);
            }
            String barcode = getBarcodeFromBlackFile(file);
            String group = getGroup(barcode);
            String alternative = objJobSorterGrouped.getAlternative(barcode);
            if(!etichetteByGroup.containsKey(group)){
                ArrayList<String> currentFiles;
                if(unExpectedGroups.containsKey(group)){
                    currentFiles=unExpectedGroups.get(group);
                }
                else{
                    currentFiles = new ArrayList<>();
                }
                if(!currentFiles.contains(file)){
                    currentFiles.add(file);
                    unExpectedGroups.put(group,currentFiles);
                }
            }
            else{
                ArrayList<objFileEtichette> possibles = etichetteByGroup.get(group);
                if(possibles.size()==1){
                    int firstProg = possibles.get(0).progStart;
                    int lastProg = possibles.get(0).progEnd;
                    objFileEtichette currentRow = objGlobals.fileEtichette.get(possibles.get(0).row);
                    if(index<firstProg){
                        currentRow.firstBarcode = barcode;
                        currentRow.progStart = index;
                    }
                    else if(index>lastProg){
                        currentRow.lastBarcode = barcode;
                        currentRow.progEnd = index;
                    }
                    objGlobals.fileEtichette.put(possibles.get(0).row,currentRow);
                    moveStock2(group, file, "target");
                }
                else{
                    boolean gotPossible=false;
                    possibles.sort((e1, e2) -> Integer.compare(e1.progEnd, e2.progEnd));
                    for(int i=0;i<possibles.size()-1;i++){
                        int lastProg = possibles.get(i).progEnd;
                        int firstProg = possibles.get(i+1).progStart;
                        if(index>=lastProg&&index<=firstProg){
                            Integer row1 = possibles.get(i).row;
                            String box1=objGlobals.fileEtichette.get(row1).reference;
                            String pallet1=objGlobals.fileEtichette.get(row1).obs;
                            Integer row2 = possibles.get(i+1).row;
                            String box2=objGlobals.fileEtichette.get(row2).reference;
                            String pallet2=objGlobals.fileEtichette.get(row2).obs;
                            objNotExpected objNotExpected;
                            if(!notExpected.containsKey(row1)){
                                objNotExpected = new objNotExpected(group,row1,box1,pallet1,row2,box2,pallet2,index,index,barcode,barcode);
                            }
                            else{
                                objNotExpected = notExpected.get(row1);
                                if(index<objNotExpected.indexStart){
                                    objNotExpected.indexStart=index;
                                    objNotExpected.firstBarcode=barcode;
                                }
                                if(index>objNotExpected.indexEnd){
                                    objNotExpected.indexEnd=index;
                                    objNotExpected.lastBarcode=barcode;
                                }
                            }
                            objNotExpected.barcodeList.add(barcode);
                            if(!alternative.isEmpty()){
                                objNotExpected.barcodeList.add(barcode);
                            }
                            objNotExpected.fileList.add(file);
                            notExpected.put(row1,objNotExpected);
                            gotPossible=true;
                        }
                    }
                    if(!gotPossible){
                        Integer lastIndex = possibles.size()-1;
                        int lastProg = possibles.get(lastIndex).progEnd;
                        int firstProg = possibles.get(0).progStart;
                        if(index>=lastProg||(originalProgEnd.containsKey(group)&&index >=originalProgEnd.get(group))){
                            objFileEtichette currentRow = objGlobals.fileEtichette.get(possibles.get(lastIndex).row);
                            currentRow.lastBarcode = barcode;
                            currentRow.progEnd = index;
                            objGlobals.fileEtichette.put(possibles.get(lastIndex).row,currentRow);
                            if(!originalProgEnd.containsKey(group)){
                                originalProgEnd.put(group, lastProg);
                            }
                            moveStock2(group, file, "target");
                        }
                        else if(index<=firstProg){
                            objFileEtichette currentRow = objGlobals.fileEtichette.get(possibles.get(0).row);
                            currentRow.firstBarcode = barcode;
                            currentRow.progStart = index;
                            moveStock2(group, file, "target");
                        }
                        else{
                            printError(new Exception("SITUAZIONE NON PREVISTA GROUP:"+group+" BARCODE: "+barcode),false);
                            moveStock2(group, file, "log");
                        }
                    }
                }
            }
        }

        for (Integer row1 : notExpected.keySet()){
            objNotExpected obj = notExpected.get(row1);
            addStock2(++id,obj);
        }
    }

}
