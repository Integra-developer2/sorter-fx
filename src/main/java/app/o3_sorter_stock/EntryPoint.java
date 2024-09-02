package app.o3_sorter_stock;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import static app.functions.logError;
import static app.functions.printError;
import app.objGlobals;

public class EntryPoint extends functions{

    public static void start() {
        try {
            write("primo: "+objGlobals.stockPrefix + strPad(String.valueOf(objGlobals.stockNumber),4,"0"), objGlobals.stockTxt);
            makePdfMulti(new StepController());
            makeSorterExport();
            write("ultimo: "+objGlobals.stockPrefix + objDoneStock.lastNumber, objGlobals.stockTxt);
        } catch (Exception e) {
            printError("error",e,true);
        }
    }

    public static void makePdfMulti(StepController stepController){
        int threadIndex = 0;
        StepPdf objStepPdf = new StepPdf();
        for (String from : objToPdf.list.keySet()) {
            String to = objToPdf.list.get(from);
            if(stepController.listIsFull()){
                stepController.printProgress("Pdf",objToPdf.list.size());
            }
            var Thread = new ThreadPdf( "pdf" + threadIndex, objStepPdf, from, to);
            try {
                Thread.join();
            } catch ( InterruptedException e) {
                logError("pdf from: "+from+" to "+to, e);
            }
            threadIndex++;
            Thread.start();
            stepController.stepAdd(from + " " + to);
        }
        ThreadCount.waitPdf();
    }

    public static void makeSorterExport(){
        String header = "N.Pacco-Anno;Sequenza nel Pacco;Barcode";
        for(String folder : objSorterExport.list.keySet()){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(objGlobals.sorterExport+folder+".csv"))) {
                HashMap<String, ArrayList<String[]>> keys = objSorterExport.list.get(folder);
                for (String key : keys.keySet()) {
                    writer.append("Soggetto: "+key).append(System.lineSeparator());
                    writer.append(header).append(System.lineSeparator());
                    for (String[] row : keys.get(key)) {
                        writer.append(String.join(";",row)).append(System.lineSeparator());
                    }
                }
                writer.append(System.lineSeparator());
            } catch (Exception e) {
                printError(e, true);
            }
        }
    }
}