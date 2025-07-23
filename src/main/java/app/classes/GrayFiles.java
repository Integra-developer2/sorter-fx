package app.classes;

import app.objects.objGlobals;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ConcurrentLinkedQueue;

import static app.functions.printError;

public class GrayFiles {
    public static ConcurrentLinkedQueue<String> anomalies = new ConcurrentLinkedQueue<>();

    public static boolean hasGrayAnomalies(){
        if(new File(objGlobals.anomalyFolderGray).exists()){
            try {
                Files.walkFileTree(Paths.get(objGlobals.anomalyFolderGray), new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                        String strPath = path.toString();
                        if (strPath.endsWith(".tiff")) {
                            GrayFiles.addGray(strPath);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            catch (IOException e) {
                printError(e,true);
            }
        }
        return ! GrayFiles.anomalies.isEmpty();
    }

    public static void addGray(String anomalyFile){
        if(!anomalies.contains(anomalyFile)){
            anomalies.add(anomalyFile);
        }
    }

    public static void removeFolder(){
        Path folder = Paths.get(objGlobals.anomalyFolderGray);
        try{
            if(Files.exists(folder)){
                Files.walkFileTree(folder,new SimpleFileVisitor<>(){
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Path target = Paths.get(file.toString().replace(objGlobals.anomalyFolderGray, objGlobals.logAnomalyFolderGray));
                        Files.createDirectories(target.getParent());
                        Files.copy(file, target);
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }
        catch (Exception e){
            printError(e,true);
        }

    }

}
