package app.steps;

import app.classes.JobSorter;
import app.Routing;
import app.classes.Pc;
import app.classes.UI;
import app.objects.*;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static app.functions.*;
import static java.lang.Thread.sleep;

public class gray {
    public static void start(){
        try{
            objGlobals.totalThreads = 1;
            grayFiles();
            blackFiles();
            JobSorter.getData();
            readBarcodes();
            prints();
            grayFileNotFound();
            Routing.gray = "end";
        }
        catch(Exception e){
            printError(e,true);
        }

    }

    private static void prints(){
        UI.controller.addSpinner("Backup dei controller");
        objConcurrentGrayController.printAll();
        objConcurrentBlackController.printAll();
        UI.controller.removeSpinner();
    }

    private static void grayFileNotFound(){
        if(!objFilesBlack.barcodeFiles.isEmpty()){
            int total = objFilesBlack.barcodeFiles.size();
            objProgressItem pi = UI.controller.addProgress("Sposto i file anomali",total);
            int count = 0;
            for(String barcode: objFilesBlack.barcodeFiles.keySet()){
                if(!objConcurrentBlackController.barcodeStatus.containsKey(barcode)){
                    ArrayList<String> files = objFilesBlack.barcodeFiles.get(barcode);
                    for (String file : files) {
                        moveFilesBlackWithDir("grayFileNotFound", file);
                        objConcurrentBlackController.add("grayFileNotFound",barcode,file);
                    }
                }
                else{
                    objConcurrentBlackController.barcodeStatus.remove(barcode);

                }
                count++;
                UI.controller.refresh(pi,count);
            }
            UI.controller.refresh(pi,total);
        }
    }

    private static void readBarcodes() throws Exception {
        if (!objFilesGray.toBarcodeReader.isEmpty()) {
            if(!wslIsIdle()){
                throw new Exception("wsl non è attivo, riavvia il computer");
            }
            else{
                AtomicInteger count = new AtomicInteger(0);
                int total = objFilesGray.toBarcodeReader.size();
                ArrayList<Thread> threads = new ArrayList<>();
                objProgressItem pi = UI.controller.addProgress("Leggendo barcodes nei file grigi",total);

                for (String path : objFilesGray.toBarcodeReader) {
                    while (threads.size() >= objGlobals.totalThreads) {
                        refreshThreads(count, threads, pi, path);
                    }
                    Thread newThread = newThread(path, "readBarcodes-"+count.get());
                    newThread.start();
                    threads.add(newThread);
                }

                while (!threads.isEmpty()) {
                    refreshThreads(count, threads, pi, String.valueOf(count));
                }
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
                objLogTimeline.add("refreshThreads","[ readBarcodes ] done "+text);
            }
            else{
                objLogTimeline.add("refreshThreads","[ readBarcodes ] running "+text);
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
        objLogTimeline.add("gray","[ objGlobals.totalThreads ] : "+objGlobals.totalThreads);

    }

    private static Thread newThread(String path, String name){
        Thread t = new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                readBarcode(path);
                return null;
            }
        });
        t.setName(name);
        t.setDaemon(true);
        return t;
    }

    private static void grayFiles() throws Exception{
        UI.controller.addSpinner("Listo i file grigi");
        objFilesGray.list();

        if (objFilesGray.all.isEmpty()) {
            throw new Exception("Nessun File Grigio");
        } else {
            for (String file : objFilesGray.files) {
                String front = file.replace("PREFIX", "Camera");
                String back = file.replace("PREFIX", "BacksideCamera");
                if(!objFilesGray.all.contains(front)){
                    objConcurrentGrayController.add(back,"CFNF",null);
                }
                else if(!objFilesGray.all.contains(back)){
                    objConcurrentGrayController.add(front,"CFNF",null);
                }
                else{
                    objFilesGray.toBarcodeReader.add(file);
                }
            }
        }
        UI.controller.removeSpinner();
    }

    private static void blackFiles() throws Exception{
        objFilesBlack.list();
        if (!objFilesBlack.list.isEmpty()) {
            int total = objFilesBlack.list.size();
            objProgressItem pi = UI.controller.addProgress("leggendo i file tiff per barcode",total);

            int count = 0;
            for(String file : objFilesBlack.list) {
                String front = file + "-FRONTE.tiff";
                String back = file + "-RETRO.tiff";
                if (!objFilesBlack.all.contains(front)) {
                    objConcurrentBlackController.add("coupleNotFound",barcode(back),back);
                    moveFilesBlackWithDir("coupleNotFound",back);
                }
                else if (!objFilesBlack.all.contains(back)) {
                    objConcurrentBlackController.add("coupleNotFound",barcode(front),front);
                    moveFilesBlackWithDir("coupleNotFound",front);
                }
                count++;
                UI.controller.refresh(pi,count);
            }
            UI.controller.refresh(pi,total);
            if(!objFilesBlack.barcodeFiles.isEmpty()){
                int total2 = objFilesBlack.barcodeFiles.size();
                objProgressItem pi2 = UI.controller.addProgress("Crea elenco dei barcode",total2);

                int count2 = 0;
                for(String barcode:objFilesBlack.barcodeFiles.keySet()){
                    ArrayList<String>files=objFilesBlack.barcodeFiles.get(barcode);
                    if(files.size()>2){
                        int bigger = 0;
                        HashMap<Integer, ArrayList<String>> byProgSorter = new HashMap<>();
                        for (String file : files) {
                            String[] split = file.split("-");
                            int progSorter = Integer.parseInt(split[split.length-3]+split[split.length-2]);
                            if(progSorter > bigger){
                                bigger = progSorter;
                            }
                            if(byProgSorter.containsKey(bigger)){
                                byProgSorter.get(bigger).add(file);
                            }
                            else{
                                ArrayList<String> newList = new ArrayList<>();
                                newList.add(file);
                                byProgSorter.put(bigger, newList);
                            }
                        }
                        for(int progSorter:byProgSorter.keySet()){
                            for(String file:byProgSorter.get(progSorter)){
                                if(progSorter!=bigger){
                                    objConcurrentBlackController.add("isMultiple",barcode,file);
                                    moveFilesBlackWithDir("isMultiple",file);
                                }
                            }
                        }
                    }
                    count2++;
                    UI.controller.refresh(pi2,count2);
                }
                UI.controller.refresh(pi2,total2);
            }

            objFilesBlack.refreshBarcodeFiles();
        }
    }

    private static boolean wslIsIdle() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("wsl","ls","/srv/scandit_multiple/" + objGlobals.scanditProgram);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if(line.equals("/srv/scandit_multiple/" + objGlobals.scanditProgram)){
                return true;
            }
        }
        return false;
    }

    public static void readBarcode(String path) {
        try{
            String pathFront = path.replace("PREFIX", "Camera");
            String pathBack = path.replace("PREFIX", "BacksideCamera");
            objReadBarcode objReadBarcode = new objReadBarcode(pathFront, pathBack);
            objReadBarcode.readBarCodesDoingWhites();
            ArrayList<String> barcodesJobSorter = checkInJobSorter(objReadBarcode.resultsFront, objReadBarcode.resultsBack);
            if(barcodesJobSorter.isEmpty()){
                objReadBarcode.logGrayResults("notInJobSorter");
            }
            if(barcodesJobSorter.size()>1){
                objReadBarcode.logGrayResults("multipleFoundsInJobSorter");
            }
            else{
                if(!objReadBarcode.equals.isEmpty()){
                    objReadBarcode.logGrayResults("foundAndEqual");
                    objConcurrentBlackController.add("foundAndEqual", barcodeFiles(objReadBarcode.equals));
                }
                else if(!objReadBarcode.resultsFront.isEmpty()&&!objReadBarcode.resultsBack.isEmpty()){
                    objReadBarcode.logGrayResults("foundNotEqual");
                    objConcurrentBlackController.add("foundNotEqual", barcodeFiles(objReadBarcode.resultsFront, objReadBarcode.resultsBack));
                }
                else if(!objReadBarcode.resultsFront.isEmpty()||!objReadBarcode.resultsBack.isEmpty()){
                    objReadBarcode.logGrayResults("foundNotEqual");
                    HashMap<String, ArrayList<String>> barcodeFiles = barcodeFiles(objReadBarcode.resultsFront, objReadBarcode.resultsBack);
                    if(!barcodeFiles.isEmpty()){
                        for (String barcodeFile : barcodeFiles.keySet()) {
                            ArrayList<String> files = barcodeFiles.get(barcodeFile);
                            for (String file : files) {
                                if(isActCompiutaGiacenza(file, barcodeFile)){
                                    objConcurrentBlackController.add("foundActUnclaimed",barcodeFile,file);
                                }
                                else{
                                    objConcurrentBlackController.add("foundNotEqual",barcodeFile,file);
                                }
                            }
                        }
                    }
                }
                else{
                    objReadBarcode.logGrayResults("foundNothing");
                }
            }
        }
        catch(Exception e){
            logError("readBarcode",e);
        }

    }

    public static HashMap<String, ArrayList<String>> barcodeFiles(ArrayList<String> barcodesA,ArrayList<String> barcodesB) {
        HashMap<String, ArrayList<String>> ret = new HashMap<>();
        HashMap<String, ArrayList<String>> retA = barcodeFiles(barcodesA);
        HashMap<String, ArrayList<String>> retB = barcodeFiles(barcodesB);
        for (String barcode : retA.keySet()) {
            if(!barcode.isEmpty()){
                if(!ret.containsKey(barcode)){
                    ret.put(barcode, retA.get(barcode));
                }
            }
        }
        for (String barcode : retB.keySet()) {
            if(!barcode.isEmpty()){
                if(!ret.containsKey(barcode)){
                    ret.put(barcode, retB.get(barcode));
                }
            }
        }

        return ret;
    }

    public static HashMap<String, ArrayList<String>> barcodeFiles(ArrayList<String> barcodes) {
        HashMap<String, ArrayList<String>> barcodeFiles = new HashMap<>();
        for (String barcode : barcodes) {
            String alternative = JobSorter.alternativeBarcode(barcode);
            if(objFilesBlack.barcodeFiles.containsKey(barcode)){
                ArrayList<String> files = objFilesBlack.barcodeFiles.get(barcode);
                barcodeFiles.put(barcode, files);
            }
            else if(alternative!=null && !alternative.isEmpty() && objFilesBlack.barcodeFiles.containsKey(alternative)){
                ArrayList<String> files = objFilesBlack.barcodeFiles.get(alternative);
                barcodeFiles.put(alternative, files);
            }
        }
        return barcodeFiles;
    }

    public static ArrayList<String> checkInJobSorter(ArrayList<String> barcodesA, ArrayList<String> barcodesB) {
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<String> retA = checkInJobSorter(barcodesA);
        ArrayList<String> retB = checkInJobSorter(barcodesB);

        for (String barcode : retA) {
            String alternative = JobSorter.alternativeBarcode(barcode);
            if(!ret.contains(barcode)&&!ret.contains(alternative)){
                ret.add(barcode);
            }
        }

        for (String barcode : retB) {
            String alternative = JobSorter.alternativeBarcode(barcode);
            if(!ret.contains(barcode)&&!ret.contains(alternative)){
                ret.add(barcode);
            }
        }
        return ret;
    }

    public static ArrayList<String> checkInJobSorter(ArrayList<String> barcodes) {
        ArrayList<String> ret = new ArrayList<>();
        for (String barcode : barcodes) {
            if(JobSorter.barcodeRow(barcode)!=null){
                ret.add(barcode);
            }
        }
        return ret;
    }
}
