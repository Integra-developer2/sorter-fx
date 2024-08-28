package app.o1_sorter_move_files;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.HashMap;

import app.objGlobals;

public class functions {

    public static HashMap<String,String> map() throws IOException {
        Path sourceTiffPath = Paths.get(objGlobals.sourceTiff);
        String strSourceTiff = sourceTiffPath.toString();
        Path targetTiffPath = Paths.get(objGlobals.targetTiff);
        String strTargetTiff = targetTiffPath.toString();
        HashMap<String,String> ret = new HashMap<>();
        Files.walkFileTree(sourceTiffPath, new SimpleFileVisitor<Path>()  {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                String from = path.toString();
                String to = from.replace(strSourceTiff, strTargetTiff);
                File fileTo = new File(to);
                if(!fileTo.exists()&&from.endsWith(".tiff")){
                    ret.put(from, to);                  
                }
                return FileVisitResult.CONTINUE;
            }
        });

        Path sourceGrayPath = Paths.get(objGlobals.sourceGray);
        String strSourceGray = sourceGrayPath.toString();
        Path targetGrayPath = Paths.get(objGlobals.targetGray);
        String strTargetGray = targetGrayPath.toString();
        Files.walkFileTree(sourceGrayPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                String from = path.toString();
                String to = from.replace(strSourceGray, strTargetGray);
                File fileTo = new File(to);
                if(!fileTo.exists()&&from.endsWith(".bmp")){
                    ret.put(from, to);                 
                }
                return FileVisitResult.CONTINUE;
            }
        });

        for (String from : objGlobals.sourceJobSorter) {
            File fileFrom = new File(from);
            File folderTo = new File(objGlobals.jogSorterFolder);
            String to = from.replace(fileFrom.getParent(), folderTo.getPath());
            File fileTo = new File(to);
            if(!fileTo.exists()){
                ret.put(from, to);
            }
        }
        
        File fileFrom = new File(objGlobals.sourceEtichette);
        File folderTo = new File(objGlobals.etichetteFolder);
        String to = objGlobals.sourceEtichette.replace(fileFrom.getParent(), folderTo.getPath());
        File fileTo = new File(to);
        if(!fileTo.exists()){
            ret.put(objGlobals.sourceEtichette, to);          
        }

        return ret;
    }

    public static String calculateChecksum(String file) throws Exception{
        Path path = Paths.get(file);
        StringBuilder sb = new StringBuilder();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Files.readAllBytes(path);
        byte[] checksum = digest.digest(bytes);
        for (byte b : checksum) { sb.append(String.format("%02x", b));}
        return sb.toString();
    }
}
