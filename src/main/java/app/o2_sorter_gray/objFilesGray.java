package app.o2_sorter_gray;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import app.objGlobals;

public class objFilesGray extends functions {
    public static ArrayList<String> all = new ArrayList<>();
    public static ArrayList<String> files = new ArrayList<>();
    public static ArrayList<String> toBarcodeReader = new ArrayList<>();

    public static void list() throws Exception{
        Path startPath = Paths.get(objGlobals.targetGray);
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    String pathString = path.toString();
                    if (pathString.endsWith(".bmp")) {
                        all.add(pathString);
                        String compactPath = pathString.replace("BacksideCamera","PREFIX");
                        compactPath = compactPath.replace("Camera","PREFIX");
                        if(!files.contains(compactPath)){
                            files.add(compactPath);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) { throw e;}      
    }

}