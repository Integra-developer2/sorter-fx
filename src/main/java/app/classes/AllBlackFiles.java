package app.classes;

import app.objects.objAllBlackFilesCache;
import app.objects.objGlobals;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static app.functions.*;
import static app.functions.logError;

public class AllBlackFiles {
    public static boolean hasData = false;
    private static final String cachePath = objGlobals.allBlackCacheFile;
    public static HashMap<String,String>barcodeFile = new HashMap<>();
    public static ArrayList<String>barcodesFromFiles=new ArrayList<>();
    public static ArrayList<String>all=new ArrayList<>();
    public static HashMap<String,Integer>barcodeIndex=new HashMap<>();

    public static String barcodeFile(String barcode){
        getData();
        return barcodeFile.get(barcode);
    }

    public static Set<String> barcodeFileKeyset(){
        getData();
        return barcodeFile.keySet();
    }

    public static void writeTxt(){
        getData();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(objGlobals.allBlackFiles)))
        {
            for(String file : all){
                writer.write(file + "\n");
            }
        }
        catch (IOException e) {
            logError("Error writing all black files!",e);
        }
    }

    public static Integer barcodeIndex(String barcode){
        getData();
        return barcodeIndex.get(barcode);
    }

    private static void readFolder() throws IOException {
        Files.walkFileTree(Paths.get(objGlobals.targetTiff), new SimpleFileVisitor<>()  {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                String file = path.toString();
                if(file.contains(".tiff")){
                    String fileName = file.replace("-FRONTE.tiff", "").replace("-RETRO.tiff", "");
                    String barcode = getBarcode(file);
                    int index = Integer.parseInt(getIndex(fileName).replace("-", ""));
                    if(!barcodeFile.containsKey(barcode)){
                        barcodeFile.put(barcode,fileName);
                        barcodeIndex.put(barcode,index);
                    }
                    else{
                        int currentIndex = Integer.parseInt(getIndex(barcodeFile.get(barcode)).replace("-", ""));
                        if(index>currentIndex){
                            barcodeFile.put(barcode,fileName);
                            barcodeIndex.put(barcode,index);
                        }
                    }
                    if(!barcodesFromFiles.contains(barcode)){
                        barcodesFromFiles.add(barcode);
                    }
                    String alternative = JobSorter.alternativeBarcode(barcode);
                    if(alternative!=null&&!alternative.isEmpty()&&!barcodesFromFiles.contains(alternative)){
                        barcodesFromFiles.add(alternative);
                    }
                    all.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        objAllBlackFilesCache cache = new objAllBlackFilesCache();
        cache.barcodeFile = barcodeFile;
        cache.barcodesFromFiles = barcodesFromFiles;
        cache.all = all;
        cache.barcodeIndex = barcodeIndex;
        saveToCache(cache);

        hasData = true;
    }

    public static String getBarcode(String filename){
        String[] split = split(filename);
        return split[ 6 ];
    }

    public static String getIndex(String filename){
        String[] blackPathSplit = split(filename);
        return blackPathSplit[ 9 ] + "-" + blackPathSplit[ 10 ];
    }

    private static String[] split(String fileStr){
        File file = new File(fileStr);
        String filename = file.getName();
        filename = filename.replace("RAC-EST", "RAC_EST");
        return filename.split("-");
    }

    private static void getData(){
        if(!hasData){
            if(new File(cachePath).exists()){
                try {
                    loadFromCache();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                try {
                    readFolder();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void saveToCache(objAllBlackFilesCache cache) throws IOException {
        mkdir(cachePath);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(cachePath)))) {
            oos.writeObject(cache);
        }
    }

    private static void loadFromCache() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(cachePath)))) {
            objAllBlackFilesCache cache = (objAllBlackFilesCache) ois.readObject();
            barcodeFile = cache.barcodeFile;
            barcodesFromFiles = cache.barcodesFromFiles;
            all = cache.all;
            barcodeIndex = cache.barcodeIndex;
            hasData = true;
        }
    }


}