package app.o3_sorter_stock;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import static app.functions.makeStockNumber;
import static app.functions.printError;
import static app.functions.writeStockFile;
import app.objAnomalies;
import app.objGlobals;

public class EntryPoint extends functions{

    public static void start() {
        try {
            objToPdf.clear();
            objDoneStock.clear();
            if(objAnomalies.hasStockAnomaly()){
                printError( new Exception("hasStockAnomaly() should be empty"),true);
            }
            else{
                makeStockNumber();
                makePdfMulti();
                writeStockFile("result");
                makeSorterExport();
            }
        } catch (Exception e) {
            printError("error",e,true);
        }
    }

    private static void makePdfMulti() {
        if(!objToPdf.list.isEmpty()){
            int threadIndex = 0;
            for (String from : objToPdf.list.keySet()) {
                String to = objToPdf.list.get(from);
                Threads.start(new ThreadPdf("bcr_"+threadIndex++, new ThreadObjPdf(), from, to), objToPdf.list.size());
            }
            Threads.waitRunning();
        }
    }

    public static void makeSorterExport(){
        String header = "N.Pacco-Anno;Sequenza nel Pacco;Barcode;Riferimento Scatolo";
        for(String folder : objSorterExport.list.keySet()){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(objGlobals.sorterExport+folder+".csv"))) {
                HashMap<String, ArrayList<String[]>> keys = objSorterExport.list.get(folder);
                for (String key : keys.keySet()) {
                    writer.append("soggetto;"+key).append(System.lineSeparator());
                    writer.append(header).append(System.lineSeparator());
                    for (String[] row : keys.get(key)) {
                        writer.append(String.join(";",row)).append(System.lineSeparator());
                    }
                    writer.append(System.lineSeparator());
                }
                writer.append(System.lineSeparator());
            } catch (Exception e) {
                printError(e, true);
            }
        }
    }
}