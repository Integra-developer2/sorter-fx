package app.o2_sorter_gray;

import java.io.BufferedReader;
import java.io.FileReader;
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

public class objJobSorter extends functions{
    public static ArrayList<String> list = new ArrayList<>();
    public static HashMap<String,String> alternative = new HashMap<>();
    public static void list() throws Exception{
        Files.walkFileTree(Paths.get(objGlobals.jogSorterFolder), new SimpleFileVisitor<Path>()  {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                Boolean isFirst = true;
                try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
                    String line;
                    while((line=reader.readLine())!=null){
                        if(isFirst){isFirst=false;}
                        else{
                            String[] columns = line.split(";");
                            list.add(columns[1]);
                            if(columns.length>7&&!columns[7].isEmpty()){
                                alternative.put(columns[7], columns[1]);
                            }
                        }
                    }
                } catch (Exception e) { throw e;}   
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
