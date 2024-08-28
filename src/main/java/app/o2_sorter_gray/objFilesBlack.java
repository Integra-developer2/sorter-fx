package app.o2_sorter_gray;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;

import app.objGlobals;

public class objFilesBlack extends functions{
    public static ArrayList<String> all = new ArrayList<>();
    public static ArrayList<String> list = new ArrayList<>();
    public static HashMap<String, ArrayList<String>> barcodeFiles = new HashMap<>();

    public static void list() throws Exception{
        Path startPath = Paths.get(objGlobals.targetTiff);
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    String pathString = path.toString();
                    if (pathString.endsWith(".tiff")) {
                        all.add(pathString);
                        String barcode = barcode(pathString);
                        if(barcodeFiles.containsKey(barcode)){
                            barcodeFiles.get(barcode).add(pathString);
                        }else{
                            ArrayList<String> newLine = new ArrayList<>();
                            newLine.add(pathString);
                            barcodeFiles.put(barcode, newLine);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }            
            });
        } catch (IOException e) { throw e;}
        
    }

    public static void refreshBarcodeFiles() throws Exception{
        Path startPath = Paths.get(objGlobals.targetTiff);
        barcodeFiles = new HashMap<>();
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    String pathString = path.toString();
                    if (pathString.endsWith(".tiff")) {
                        String barcode = barcode(pathString);
                        if(barcodeFiles.containsKey(barcode)){
                            barcodeFiles.get(barcode).add(pathString);
                        }else{
                            ArrayList<String> newLine = new ArrayList<>();
                            newLine.add(pathString);
                            barcodeFiles.put(barcode, newLine);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }            
            });
        } catch (IOException e) { throw e;}        
    }

}
