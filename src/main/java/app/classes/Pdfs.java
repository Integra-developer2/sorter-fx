package app.classes;

import app.objects.objGlobals;
import app.objects.objPdf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static app.functions.printError;

public class Pdfs {
    public static ConcurrentHashMap<String, ArrayList<objPdf>> prefixPdf=new ConcurrentHashMap<>();
    public static void writeToFile(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(objGlobals.pdfs))){
            bw.write("prefix;number;barcode;filePdf;fileTiff");
            bw.newLine();
            for(String prefix: prefixPdf.keySet()){
                ArrayList<objPdf> objs = prefixPdf.get(prefix);
                for(objPdf obj: objs){
                    bw.write(obj.prefix + ";" + obj.number + ";" + obj.barcode + ";" + obj.filePdf + ";" + obj.fileTiff);
                    bw.newLine();
                }
            }
        }
        catch (Exception e){
            printError(e,true);
        }
    }
}
