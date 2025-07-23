package app.objects;

import app.classes.UI;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;

import static app.functions.barcode;

public class objFilesBlack {
    public static ArrayList<String> list = new ArrayList<>();
    public static ArrayList<String> all = new ArrayList<>();
    public static HashMap<String, ArrayList<String>> barcodeFiles = new HashMap<>();

    public static void list() throws Exception{
        Path startPath = Paths.get(objGlobals.targetTiff);
        Files.walkFileTree(startPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
            String pathString = path.toString();
            if (pathString.endsWith(".tiff")) {
                all.add(pathString);
                addBarcodeFiles(pathString);
                String basename = pathString.replace("-FRONTE.tiff","").replace("-RETRO.tiff","");
                if(!list.contains(basename)){
                    list.add(basename);
                }
            }
            return FileVisitResult.CONTINUE;
            }
        });

    }

    private static void addBarcodeFiles(String pathString) {
        String barcode = barcode(pathString);
        if(barcodeFiles.containsKey(barcode)){
            barcodeFiles.get(barcode).add(pathString);
        }else{
            ArrayList<String> newLine = new ArrayList<>();
            newLine.add(pathString);
            barcodeFiles.put(barcode, newLine);
        }
    }

    public static void refreshBarcodeFiles() throws Exception{
        UI.controller.addSpinner("Aggiorno barcode files");
        Path startPath = Paths.get(objGlobals.targetTiff);
        barcodeFiles = new HashMap<>();
        Files.walkFileTree(startPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                String pathString = path.toString();
                if (pathString.endsWith(".tiff")) {
                    addBarcodeFiles(pathString);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        UI.controller.removeSpinner();
    }

}
