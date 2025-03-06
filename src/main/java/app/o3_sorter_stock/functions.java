package app.o3_sorter_stock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;

import static app.functions.logError;
import static app.functions.mkdir;
import static app.functions.moveFile;

import app.objGlobals;

public class functions{

    public static String file(String a, String b){
        File file = new File(a,b);
        return file.getPath();
    }

    public static String getFileName(String filePath){
        File file = new File(filePath);
        return file.getName();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static boolean isDirectory(String path){
        File directory = new File(path);
        return directory.isDirectory();
    }

    public static void delete(String path){
        File directory = new File(path);
        deleteFile(directory);
    }

    public static void deleteFile(File directory) {
        if(directory.isDirectory()) {
            File[] files = directory.listFiles();
            if(files != null) {
                for(File file : files) {
                    deleteFile(file);
                }
            }
        }
        directory.delete();
    }

    public static void copyFile(String from, String to){
        try {
            mkdir(to);
            Files.copy(Paths.get(from), Paths.get(to), REPLACE_EXISTING);
        } catch (IOException e) {
            logError("copyFile from "+from+" to "+to, e);
        }
    }

    public static String getFrontPath(String path){
        return path.replace("Backside", "");
    }

    public static String getBackPath(String path){
        return getFrontPath(path).replace("Camera", "BacksideCamera");
    }

    public static void copyFiles(String code, String from){
        String filename = getFileName(from);
        File fileTo = new File(objGlobals.errorMap.get(code).path, filename);
        copyFile(from,fileTo.toString());
    }

    public static void moveFiles(String code, String from){
        String filename = getFileName(from);
        String dir;
        dir=objGlobals.errorMap.get(code).path;
        mkdir(dir);
        moveFile(from,dir+"\\\\"+filename);
    }

    public static void readJobSorterCSV() throws Exception{
        objJobSorter.clear();
        objJobSorterGrouped.clear();
        int barcodeIndex = letterToIndex(objGlobals.colonnaBarcode);
        Files.walkFileTree(Paths.get(objGlobals.jogSorterFolder), new SimpleFileVisitor<Path>()  {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
                    Boolean isFirst = true;
                    String line;
                    while((line=reader.readLine())!=null){
                        String[] row = line.split(";");
                        if(row.length>0){
                            if(isFirst){
                                isFirst = false;
                                objJobSorter.addTitles(row);
                            }
                            else{
                                objJobSorter.add(row[barcodeIndex], row);
                                String groupBy = getGroup(row[barcodeIndex]);
                                objJobSorterGrouped.add(groupBy, row[barcodeIndex], row);
                                if(row.length>7){
                                    objJobSorterGrouped.alternative(row[barcodeIndex], row[7]);
                                }
                            }
                        }
                    }
                } catch (Exception e) { throw e;}
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void moveFilesWithDir(String code, String from){
        if(fileExists(from)){
            String dir=objGlobals.errorMap.get(code).path;
            mkdir(dir);
            Path path = Paths.get(from);
            Path inputPath = Paths.get(objGlobals.targetTiff);
            String to = path.toString().replace(inputPath.toString(),dir);
            mkdir(to);
            moveFile(from,to);
        }
    }

    public static void printStepControl(String file, String line){
        mkdir(file);
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.append(line+"\n");
            }
        } catch (IOException e) {
            logError("error putting Step",e);
        }
    }

    public static ArrayList<String>getFilesBack(String path, ArrayList<String> foundFiles){
        try {
            File f = new File(path);
            File[] files = f.listFiles();
            for (File file : files) {
                if(file.isDirectory()){
                    getFilesBack(file.getAbsolutePath(), foundFiles);
                }else if(file.getName().toLowerCase().contains("backside")){
                    foundFiles.add(file.getCanonicalPath());
                }
            }
            return foundFiles;
        } catch (IOException e) {
            logError("getFiles.back path: "+path, e);
        }
        return null;

    }

    public static String strPad(String inputString, int length, String append) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append(append);
        }
        sb.append(inputString);

        return sb.toString();
    }

    public static boolean isBack(String path){
        return path.contains("Backside")||path.contains("-RETRO");
    }

    public static boolean fileExists(String path){
        String filePath = path;
        File file = new File(filePath);
        return file.exists();
    }

    public static boolean folderExists(String filePath){
        File file = new File(filePath);
        Boolean ret;
        if(file.isDirectory()){
            ret = file.exists();
        }else{
            File parent = new File(file.getParent());
            ret = parent.exists();
        }
        return ret;
    }

    public static void mkDir(String path) throws IOException {
        File file = new File(path);
        if (file.getParentFile() != null) {
            Files.createDirectories(file.getParentFile().toPath());
        }
    }

    public static void write(String row, String file) {
        if(!fileExists(file)){
            try {
                mkDir(file);
            } catch (IOException ee) {
                logError("write", ee);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            try {
                writer.append(row+ "\n");
            } catch (IOException var5) {
                try {
                    writer.close();
                } catch (IOException var4) {
                    var5.addSuppressed(var4);
                }
                throw var5;
            }
        } catch (IOException var6) {
            logError("write", var6);
        }
    }

    public static boolean isFile(String path){
        Path filePath = Paths.get(path);
        return Files.isRegularFile(filePath);
    }

    public static void deleteAll(String path){
        try {
            Path directory = Paths.get(path);
            Files.walk(directory)
                .filter(Files::isRegularFile)
                .forEach((Path file) -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        logError("deleteAll "+path, e);
                    }
                });
        } catch (IOException e) {
            logError("deleteAll "+path, e);
        }finally{}
    }

    public static int letterToIndex(String letter) {
        return Character.toUpperCase(letter.charAt(0)) - 'A';
    }

    public static String[] blackPathSplit(String fileStr){
        File file = new File(fileStr);
        String filename = file.getName();
        filename = filename.replace("RAC-EST", "RAC_EST");
        return filename.split("-");
    }

    public static String getBarcodeFromBlackFile(String filename){
        String[] blackPathSplit = blackPathSplit(filename);
        return blackPathSplit[ 6 ];
    }

    public static String getIndexFromBlackFile(String filename){
        String[] blackPathSplit = blackPathSplit(filename);
        return blackPathSplit[ 9 ] + "-" + blackPathSplit[ 10 ];
    }

    public static String getGroup(String barcode) {
        String[] row = objJobSorter.rows.get(barcode);
        String[] groupBy = objGlobals.raggruppamentoJobSorter;
        String ret = "";
        for (String group : groupBy) {
            try {
                ret+=row[letterToIndex(group)];
            } catch (Exception e) {}
        }
        return ret;
    }

    public static int indexMin(String barcode){
        int ret=0;
        try {
            String filename =  objBlackFiles.barcodePathMin.get(barcode.toUpperCase());
            ret = index(barcode, filename);
        } catch (Exception e) {}
        return ret;
    }

    public static int indexMax(String barcode){
        int ret=0;
        try {
            String filename =  objBlackFiles.barcodePathMax.get(barcode.toUpperCase());
            ret = index(barcode, filename);
        } catch (Exception e) {}
        return ret;
    }

    public static int index(String barcode, String filename) throws Exception{
        int ret = 0;
        try {
            ret= Integer.parseInt(getIndexFromBlackFile(filename).replace("-", ""));
        } catch (NumberFormatException e) {
            logError("il barcode: "+barcode+" non è stato trovato", e);
            throw e;
        }
        return ret;
    }

    public static void printProgress(double percent, String text){
        if(percent>1.0){
            percent=1.0;
        }
        String line = strPad(text,20," ");
        String progress = strPad("",10-(int)round(percent*10.00,2),"_");
        line+=": "+strPad(progress,10,"#");
        line+=" ("+round(percent*100.0,2)+"%)";
        print(line);
    }

    public static void print(String text){
        System.out.println(text);
    }

    private static void addBarcode(ArrayList<String>fileAdded,String file) {
        if(file.contains(".tiff")){
            String fileName = file.replace("-FRONTE.tiff", "");
            if(!fileAdded.contains(fileName)){
                objBlackFiles.addBarcodePath(fileName);
                fileAdded.add(fileName);
            }
            String barcode = getBarcodeFromBlackFile(file);
            String alternative = objJobSorterGrouped.getAlternative(barcode);
            if(!objGlobals.barcodesFromFiles.contains(barcode)){
                objGlobals.barcodesFromFiles.add(barcode);
            }
            if(!alternative.isEmpty()&&!objGlobals.barcodesFromFiles.contains(alternative)){
                objGlobals.barcodesFromFiles.add(alternative);
            }
            objBlackFiles.add(file);
        }
    }

    public static void readBlackDir() throws Exception{
        objBlackFiles.clear();
        ArrayList<String>fileAdded = new ArrayList<>();
        if(objGlobals.allBlackFiles.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(objGlobals.allBlackFiles))) {
                String file;
                while ((file=reader.readLine())!=null) {
                    addBarcode(fileAdded,file);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        else{
            try {
                Files.walkFileTree(Paths.get(objGlobals.targetTiff), new SimpleFileVisitor<Path>()  {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        String file = path.toString();
                        addBarcode(fileAdded,file);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw e;
            }

            mkdir(objGlobals.allBlackFiles.toString());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(objGlobals.allBlackFiles.toString()))) {
                for (String file : objBlackFiles.paths) {
                    writer.append(file).append(System.lineSeparator());
                }
            } catch (Exception e) {
                throw e;
            }

        }
    }
}