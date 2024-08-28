package app.o3_sorter_stock;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import static app.functions.logError;
import app.objGlobals;

public class StepPdf extends functions {
    public void start(String from, String to) {
        String tiffFile1 = from + "-FRONTE.tiff";
        String tiffFile2 = from + "-RETRO.tiff";
        String outputPdfFile = to;
        try {
            mkDir(to);
            rotateIfNeeded(tiffFile1);
            rotateIfNeeded(tiffFile2);
            mergeTiffToPdf(tiffFile1, tiffFile2, outputPdfFile);
        } catch (IOException e) {
            logError("StepPdf start", e);
        }
        if(fileExists(outputPdfFile)){
            objDonePdf.done(tiffFile1);
            objDonePdf.done(tiffFile2);
        }
        else{
            objDonePdf.error(tiffFile1);
            objDonePdf.error(tiffFile2);
        }
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

        BufferedImage tiffImage = readTiffImage(tiffFile);
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

    private static BufferedImage readTiffImage(File tiffFile) throws IOException {
        try (ImageInputStream input = ImageIO.createImageInputStream(tiffFile)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(input);
                BufferedImage image = reader.read(0);
                reader.dispose();
                return image;
            } else {
                return null;
            }
        }
    }

    public static void rotateIfNeeded(String path) {
        boolean isHorizontal = isHorizontal(path);
        boolean needHorizontal = objGlobals.rotateHorizontal;
        if((isHorizontal&&needHorizontal)||(!isHorizontal&&!needHorizontal)){}
        else{
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
    public static Boolean isHorizontal(String path) {
        try {
            File fpath = new File(path);
            try {
                SimpleImageInfo imageInfo = new SimpleImageInfo(fpath);
                return imageInfo.getWidth() > imageInfo.getHeight();
            } catch (IOException var3) {
                return null;
            }
        } catch (Exception var4) {
            return null;
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
            transform.translate((newWidth - width) / 2, (newHeight - height) / 2);
            int x = width / 2;
            int y = height / 2;
            transform.rotate(radians, x, y);
            g2d.setTransform(transform);
            g2d.drawImage(originalImage, 0, 0, null);
            g2d.dispose();
            File outputFile = new File(path);
            ImageIO.write(rotatedImage, "TIFF", outputFile);
        } catch (IOException e) {
            logError("rotateWithJai "+path, e);
        }
    }
}
