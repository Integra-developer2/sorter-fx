package app.steps;

import app.Routing;
import app.classes.Pdfs;
import app.classes.UI;
import app.classes.ValidTiffs;
import app.objects.*;
import javafx.concurrent.Task;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static app.functions.*;
import static java.lang.Thread.sleep;

public class pdf {
    private static final ArrayList<Thread> threads = new ArrayList<>();
    private static Thread threadSorteExport = null;

    public static void start(){
        try{
            objGlobals.totalThreads = 1 ;
            ValidTiffs.getFromFile();
            sorterExport();
            generatePdfs();
            while (threadSorteExport.isAlive()) {
                objLogTimeline.add("pdf",threadSorteExport.getName()+" running");
            }
            Pdfs.writeToFile();
            Routing.pdf = "end";
        }
        catch (Exception e){
            printError(e,true);
        }
    }

    private static void sorterExport(){
        threadSorteExport = sorterExportThread();
        threadSorteExport.setDaemon(true);
        threadSorteExport.start();

    }

    private static Thread sorterExportThread(){
        return new Thread(new Task<Void>(){
            @Override
            protected Void call() {
                HashMap<String,HashMap<String,ArrayList<objValidTiff>>> sorterExport = new HashMap<>();
                UI.controller.addSpinner("mi preparo a fare il sorterExport");
                for(String barcode : ValidTiffs.barcodeObject.keySet()){
                    objValidTiff obj = ValidTiffs.barcodeObject.get(barcode);
                    String passo = new File(obj.file).getParentFile().getParentFile().getName();

                    if(obj.stockNumber !=null){
                        sorterExport.computeIfAbsent(passo, _-> new HashMap<>());
                        sorterExport.get(passo).computeIfAbsent(obj.group, _-> new ArrayList<>());
                        sorterExport.get(passo).get(obj.group).add(obj);
                    }

                }

                for (HashMap<String, ArrayList<objValidTiff>> groupMap : sorterExport.values()) {
                    for (ArrayList<objValidTiff> list : groupMap.values()) {
                        list.sort(Comparator
                            .comparing(
                                (objValidTiff obj) -> safeParseInt(obj.stockNumber),
                                Comparator.nullsFirst(Integer::compareTo)
                            )
                            .thenComparing(
                                obj -> obj.index,
                                Comparator.nullsFirst(Comparator.naturalOrder())
                            )
                        );

                    }
                }

                UI.controller.removeSpinner();

                objProgressItem pi = UI.controller.addProgress("sorterExport",ValidTiffs.barcodeObject.size());

                for(String passo : sorterExport.keySet()){
                    try(BufferedWriter bw = new BufferedWriter(new FileWriter(objGlobals.sorterExport + passo + ".csv"))){
                        for(String group : sorterExport.get(passo).keySet()){
                            int count = 0;
                            bw.write("soggetto;"+group);
                            bw.newLine();
                            bw.write("N.Pacco-Anno;Sequenza nel Pacco;Barcode;Riferimento Scatolo");
                            bw.newLine();
                            for(objValidTiff obj : sorterExport.get(passo).get(group)){
                                String stringIndex = String.format("%06d",obj.index);
                                bw.write(obj.prefix+obj.stockNumber+";"+(++count)+";"+obj.barcode+"-"+stringIndex.substring(0,3)+"-"+stringIndex.substring(3)+";"+obj.stockLabel);
                                bw.newLine();
                                UI.controller.refresh(pi,count);
                            }
                            bw.write("");
                            bw.newLine();
                        }

                    }
                    catch(IOException e){
                        printError(e,true);
                    }
                }

                return null;
            }
        });
    }

    private static void generatePdfs() throws InterruptedException {
        if (!ValidTiffs.barcodeObject.isEmpty()) {
            AtomicInteger count = new AtomicInteger(0);
            Integer total = ValidTiffs.barcodeObject.size();
            objProgressItem pi = UI.controller.addProgress("Creo i file pdf",total);

            for (String barcode : ValidTiffs.barcodeObject.keySet()) {

                objValidTiff objValidTiff = ValidTiffs.barcodeObject.get(barcode);

                while (threads.size() >= objGlobals.totalThreads) {
                    refreshThreads(count, pi, objValidTiff.barcode);
                }

                Thread newThread = newThread(objValidTiff, "generatePdfs-"+count.get());
                newThread.start();
                threads.add(newThread);

            }

            while (!threads.isEmpty()) {
                refreshThreads(count, pi, String.valueOf(count));
            }
        }
    }

    private static void refreshThreads(AtomicInteger count, objProgressItem pi, String text) throws InterruptedException {
        Iterator<Thread> it = pdf.threads.iterator();
        while (it.hasNext()) {
            Thread t = it.next();
            if (!t.isAlive()) {
                it.remove();
                UI.controller.refresh(pi, count.incrementAndGet());
                objLogTimeline.add("refreshThreads","[ generatePdfs ] done "+text);
            }
            else{
                objLogTimeline.add("refreshThreads","[ generatePdfs ] running "+text);
            }
        }
        if(cpuUsage()<70.00){
            objGlobals.totalThreads += 100;
            sleep(500);
        }
        else if(objGlobals.totalThreads > 1)
        {
            objGlobals.totalThreads -= 100;
        }
        objLogTimeline.add("generatePdfs","[ objGlobals.totalThreads ] : "+objGlobals.totalThreads);

    }

    private static Thread newThread(objValidTiff objValidTiff,String name){
        Thread t = new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                generatePdf(objValidTiff);
                return null;
            }
        });
        t.setName(name);
        t.setDaemon(true);
        return t;
    }

    public static void generatePdf(objValidTiff objValidTiff) {
        String tiffFile1 = objValidTiff.file + "-FRONTE.tiff";
        String tiffFile2 = objValidTiff.file + "-RETRO.tiff";
        String to = outputPdfFile(objValidTiff);
        try {
            mkDir(to);
            rotateIfNeeded(tiffFile1);
            rotateIfNeeded(tiffFile2);
            mergeTiffToPdf(tiffFile1, tiffFile2, to);
            Pdfs.prefixPdf.computeIfAbsent(objValidTiff.prefix, _-> new ArrayList<>());
            Pdfs.prefixPdf.get(objValidTiff.prefix).add(new objPdf(
                objValidTiff.prefix,
                objValidTiff.barcode,
                Integer.parseInt(objValidTiff.stockNumber),
                to,
                objValidTiff.file
            ));
        }
        catch (IOException e) {
            printError(e,true);
        }

    }

    public static String outputPdfFile(objValidTiff objValidTiff){
        File file = new File(objValidTiff.file + "-FRONTE.tiff");
        String barcode = objValidTiff.barcode;
        String status = file.getParentFile().getName().replaceAll("[0-9.]", "").toUpperCase();
        String entity = file.getName().split("-")[5];
        String year = String.valueOf(LocalDate.now().getYear());
        String stock;
        File folder;
        if(objValidTiff.prefix==null||objValidTiff.stockNumber==null){
            stock =  "[paccomancante]" ;
            folder = new File(objGlobals.pdfNoStockFolder,objValidTiff.group);
        }
        else{
            stock = objValidTiff.prefix + objValidTiff.stockNumber;
            folder = new File(objGlobals.pdfFolder,objValidTiff.group);
        }

        String filename = barcode + "-sorter-" +status + "-" + entity + "-" + year + "-" + stock + ".pdf";

        return new File(folder,filename).getPath();
    }

    public static void mergeTiffToPdf(String tiffFile1, String tiffFile2, String outputPdfFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            addTiffToPdf(document, tiffFile1);
            addTiffToPdf(document, tiffFile2);
            document.save(outputPdfFile);
        }
    }

    private static void addTiffToPdf(PDDocument document, String tiffFilePath) throws IOException {
        File tiffFile = new File(tiffFilePath);

        if (!tiffFile.exists()) {
            System.err.println("File not found: " + tiffFilePath);
            return;
        }

        BufferedImage tiffImage = getBufferedImage(tiffFile);
        if (tiffImage == null) {
            System.err.println("Could not read TIFF file: " + tiffFilePath);
            return;
        }
        float scale = Math.min(PDRectangle.A4.getWidth() / tiffImage.getWidth(), PDRectangle.A4.getHeight() / tiffImage.getHeight());
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDImageXObject pdImage = PDImageXObject.createFromFile(tiffFilePath, document);
        float xPos = (PDRectangle.A4.getWidth() - (tiffImage.getWidth() * scale)) / 2;
        float yPos = PDRectangle.A4.getHeight() - (tiffImage.getHeight() * scale);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.drawImage(pdImage, xPos, yPos, tiffImage.getWidth() * scale, tiffImage.getHeight() * scale);
        }
    }

    public static void rotateIfNeeded(String path) {
        boolean isHorizontal = Boolean.TRUE.equals(isHorizontal(path));
        boolean needHorizontal = objGlobals.rotateHorizontal;
        if(!((isHorizontal&&needHorizontal)||(!isHorizontal&&!needHorizontal))){
            int degrees;
            if(isBack(path)){
                if(needHorizontal){
                    degrees=270;
                }
                else{
                    degrees=90;
                }
            }
            else{
                if(needHorizontal){
                    degrees=90;
                }
                else{
                    degrees=270;
                }
            }
            rotateWithJai(path, Math.toRadians(degrees));
        }
    }

    public static void rotateWithJai(String path, double radians) {
        try {
            File inputFile = new File(path);
            BufferedImage originalImage = ImageIO.read(inputFile);
            double sin = Math.abs(Math.sin(radians));
            double cos = Math.abs(Math.cos(radians));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            int newWidth = (int) Math.floor(width * cos + height * sin);
            int newHeight = (int) Math.floor(height * cos + width * sin);

            BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
            Graphics2D g2d = rotatedImage.createGraphics();
            AffineTransform transform = new AffineTransform();
            transform.translate((double) (newWidth - width) / 2, (double) (newHeight - height) / 2);
            int x = width / 2;
            int y = height / 2;
            transform.rotate(radians, x, y);
            g2d.setTransform(transform);
            g2d.drawImage(originalImage, 0, 0, null);
            g2d.dispose();
            File outputFile = new File(path);
            ImageIO.write(rotatedImage, "TIFF", outputFile);

        }
        catch (IOException e) {
            logError("rotateWithJai "+path, e);
        }
    }
    private static Integer safeParseInt(String s) {
        try {
            return s != null ? Integer.parseInt(s) : null;
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

}