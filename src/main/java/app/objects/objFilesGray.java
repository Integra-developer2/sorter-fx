package app.objects;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class objFilesGray {
    public static ArrayList<String> all = new ArrayList<>();
    public static ArrayList<String> files = new ArrayList<>();
    public static ArrayList<String> toBarcodeReader = new ArrayList<>();

    public static void list() throws Exception{
        Path startPath = Paths.get(objGlobals.targetGray);
        Files.walkFileTree(startPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                String pathString = path.toString();
                if (pathString.endsWith(".bmp")) {
                    all.add(pathString);
                    String compactPath = pathString.replace("BacksideCamera", "PREFIX");
                    compactPath = compactPath.replace("Camera", "PREFIX");
                    if (!files.contains(compactPath)) {
                        files.add(compactPath);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

}