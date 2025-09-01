package app.steps;

import app.Routing;
import app.classes.*;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static app.functions.*;
import static java.lang.Thread.sleep;

public class pdf {
    private static Thread threadSorterExport = null;
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> stockPdfs = new ConcurrentHashMap<>();

    public static void start(){
        try{
            UI.controller.addSpinner("mi preparo a generare i pdf");
            objGlobals.totalThreads = 1 ;
            ValidTiffs.getFromFile();
            UI.controller.removeSpinner();

            generatePdfs();

            controlloQualita();

            fileEtichette();

            while (threadSorterExport.isAlive()) {
                objLogTimeline.add("pdf",threadSorterExport.getName()+" running");
            }

            Pdfs.writeToFile();
            ValidTiffs.writeToFile();
            Routing.pdf = "end";
        }
        catch (Exception e){
            printError(e,true);
        }
    }

    private static void controlloQualita(){
        threadSorterExport = controlloQualitaThread();
        threadSorterExport.setDaemon(true);
        threadSorterExport.start();
    }

    private static void generatePdfs(){
        ValidTiffs.getData();
        ArrayList<Thread> threads = new ArrayList<>();
        if (!ValidTiffs.barcodeObject.isEmpty()) {
            AtomicInteger count = new AtomicInteger(0);
            Integer total = ValidTiffs.barcodeObject.size();
            objProgressItem pi = UI.controller.addProgress("Creo i file pdf", total);

            for (String barcode : ValidTiffs.barcodeObject.keySet()) {

                objValidTiff objValidTiff = ValidTiffs.barcodeObject.get(barcode);

                while (threads.size() >= objGlobals.totalThreads) {
                    refreshThreads(count, pi, objValidTiff.barcode,threads);
                }

                Thread newPdfThread = newPdfThread(objValidTiff, "generatePdfs-"+count.get());
                threads.add(newPdfThread);
                newPdfThread.start();

            }

            while (!threads.isEmpty()) {
                refreshThreads(count, pi, String.valueOf(count),threads);
            }
        }
    }

    private static void fileEtichette(){
        objProgressItem pi = UI.controller.addProgress("fileEtichette", StockFile.rowObject().size());
        int count = 0;

        LinkedHashMap<Integer, objStock> orderStock = orderStock();

        for (objStock obj : orderStock.values()) {

            File outEtichette = new File(objGlobals.fileEtichette + ValidTiffs.barcodeObject.get(obj.firstBarcode).passo + ".csv");

            mkdir(outEtichette.getAbsolutePath());

            boolean needsHeader = !outEtichette.exists() || outEtichette.length() == 0;

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outEtichette, true))) {
                if (needsHeader) {
                    bw.write("Agenzia Mittente;Cliente Mittente;Numero pacco;Quantita;Primo Barcode;Ultimo Barcode;Data Archiviazione;Tipologia;Note;Entity;Riferimento Scatolo");
                    bw.newLine();
                }
                String stock = obj.logic.equals("lotto") ? obj.prefix + "/" + obj.stockNumber : obj.prefix + obj.stockNumber;
                bw.write(
                        obj.agency+";"+
                                obj.group+";"+
                                stock+";"+
                                stockPdfs.get(obj.prefix).get(obj.stockNumber)+";"+
                                obj.firstBarcode+";"+
                                obj.lastBarcode+";"+
                                LocalDate.now(java.time.ZoneId.of("Europe/Rome")).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))+";"+
                                obj.cppCode+";"+
                                ";"+
                                obj.agencyID+";"+
                                obj.stockLabel
                );
                bw.newLine();
                UI.controller.refresh(pi, ++count);
            } catch (IOException e) {
                printError(e, true);
            }
        }
    }

    private static Thread controlloQualitaThread(){
        return new Thread(new Task<Void>(){
            @Override
            protected Void call() {
                objProgressItem pi = UI.controller.addProgress("File controllo qualita", ValidTiffs.barcodeObject.size());
                int count = 0;

                for (String barcode : ValidTiffs.barcodeObject.keySet()) {
                    objValidTiff obj = ValidTiffs.barcodeObject.get(barcode);
                    String passo = new File(obj.file).getParentFile().getParentFile().getName();

                    File out = new File(objGlobals.controlloQualita + passo + ".csv");

                    mkdir(out.getAbsolutePath());

                    boolean needsHeader = !out.exists() || out.length() == 0;

                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(out, true))) {
                        if (needsHeader) {
                            bw.write("Barcode");
                            bw.newLine();
                        }
                        bw.write(obj.barcode);
                        bw.newLine();
                        UI.controller.refresh(pi, ++count);
                    } catch (IOException e) {
                        printError(e, true);
                    }
                }
                return null;
            }
        });
    }

    private static LinkedHashMap<Integer, objStock> orderStock() {
        return StockFile.rowObject.entrySet().stream()
                .sorted(
                        Comparator
                                .comparingInt((Map.Entry<Integer, objStock> e) -> Integer.parseInt(e.getValue().cassetto))
                                .thenComparingInt(e -> Integer.parseInt(e.getValue().stockNumber))
                )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, _) -> a,
                        LinkedHashMap::new
                ));
    }


    private static void refreshThreads(AtomicInteger count, objProgressItem pi, String text,ArrayList<Thread> threads)  {
        try{
            Iterator<Thread> it = threads.iterator();
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
            if(Pc.usage.get("cpu")<90.00 && Pc.usage.get("disk")<90.00){
                objGlobals.totalThreads += 100;
                sleep(500);
            }
            else if(objGlobals.totalThreads > 1)
            {
                objGlobals.totalThreads -= 100;
            }
            objLogTimeline.add("generatePdfs","[ objGlobals.totalThreads ] : "+objGlobals.totalThreads);
        }
        catch (Exception e){
            logError("pdf",e);
        }
    }

    private static Thread newPdfThread(objValidTiff objValidTiff,String name){
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
        if(!to.isEmpty()){
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
                addStockPdfs(objValidTiff);
            }
            catch (IOException e) {
                printError(e,true);
            }
        }

    }

    private static void addStockPdfs(objValidTiff v){
        stockPdfs
            .computeIfAbsent(v.prefix, _ -> new ConcurrentHashMap<>())
            .computeIfAbsent(v.stockNumber, _ -> new AtomicInteger(0))
            .incrementAndGet();
    }

    public static String outputPdfFile(objValidTiff objValidTiff){
        File file = new File(objValidTiff.file + "-FRONTE.tiff");
        String barcode = objValidTiff.barcode;
        String status = file.getParentFile().getName().replaceAll("[0-9.]", "").toUpperCase();
        String entity = file.getName().split("-")[5];
        String year = String.valueOf(LocalDate.now().getYear());
        String stock;
        File folder;
        String filename;
        String ret = "";
        if(objValidTiff.prefix==null||objValidTiff.stockNumber==null){
            if(!objGlobals.onlyStockPdf){
                stock =  "[paccomancante]" ;
                folder = new File(objGlobals.pdfNoStockFolder,objValidTiff.group);
                filename = barcode + "-sorter-" +status + "-" + entity + "-" + year + "-" + stock + ".pdf";
                ret = new File(folder,filename).getPath();
            }
        }
        else{
            stock = objValidTiff.prefix + objValidTiff.stockNumber;
            folder = new File(objGlobals.pdfFolder,objValidTiff.group);
            filename = barcode + "-sorter-" +status + "-" + entity + "-" + year + "-" + stock + ".pdf";
            ret = new File(folder,filename).getPath();
        }

        return ret;
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

}