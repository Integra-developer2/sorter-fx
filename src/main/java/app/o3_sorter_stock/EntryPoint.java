package app.o3_sorter_stock;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.opencsv.CSVWriter;

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
        for(String folder : objSorterExport.list.keySet()){
            File csvFileFile = new File(objGlobals.sorterExport+folder+".csv");
            String csvFile = csvFileFile.toString();
            String[] header = {"N.Pacco-Anno", "Sequenza nel Pacco", "Barcode"};
            try (
                CSVWriter writer = new CSVWriter(new FileWriter(csvFile), ';',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)
            ) {
                HashMap<String, ArrayList<String[]>> keys = objSorterExport.list.get(folder);
                for (String key : keys.keySet()) {
                    String[] firstRow = new String[]{objGlobals.sorterExportTitolo+":", key};
                    writer.writeNext(firstRow);
                    writer.writeNext(header);
                    for (String[] row : keys.get(key)) {
                        writer.writeNext(row);
                    }
                    writer.writeNext(new String[]{});
                }
            } catch (IOException e) {
                printError("makeSorterExport",e,true);
            }
        }
    }
}