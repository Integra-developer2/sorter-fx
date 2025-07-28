package app;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import app.objects.*;
import app.classes.SimpleImageInfo;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class functions {

    public static void alert(String title, String text) {
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            Label label = new Label(text);
            label.setWrapText(true);
            alert.getDialogPane().setContent(label);
            alert.showAndWait();
        });
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType yesButton = new ButtonType("SI");
        ButtonType noButton = new ButtonType("NO");
        alert.getButtonTypes().setAll(yesButton, noButton);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }

    public static void printError(Exception e, Boolean shouldStopPropagation){
        if(!objGlobals.stop){
            alert("ERROR",e.toString());
        }
        if(shouldStopPropagation){
            objGlobals.stop=true;
        }
        logError("ERROR",e);
    }

    public static void printError(String text, Exception e, Boolean shouldStopPropagation){
        if(shouldStopPropagation){
            objGlobals.stop=true;
        }
        alert("ERROR",text+"\n"+e.toString());
        logError(text,e);
    }

    public static void logError(String text, Exception e) {
        objLogTimeline.add("logError","[ error ] "+text+" "+e.getMessage());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(objGlobals.errorLog, true))) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            writer.append(text).append(";").append(e.toString())
                .append(System.lineSeparator())
                .append(sw.toString())
                .append(System.lineSeparator());
        } catch (Exception ee) {
            alert("ERROR LOG", ee.toString());
        }
    }

    public static void logTime(String text) {
        LocalDateTime now = LocalDateTime.now();
        objLogTimeline.add("logTime","[ logTime ] "+text+" "+now);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(objGlobals.timeLog, true))) {
            StringWriter sw = new StringWriter();
            writer.append(text).append(";").append(now.toString())
                .append(System.lineSeparator())
                .append(sw.toString())
                .append(System.lineSeparator());
        } catch (Exception ee) {
            logError("ERROR LOG", ee);
        }
    }

    public static void mkdir(String strPath){
        try {
            Path path = Paths.get(strPath);
            if(!Files.isDirectory(path)){
                File file = new File(strPath);
                String parent = file.getParent();
                path = Paths.get(parent);
            }
            if(!Files.exists(path)){
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            printError("mkdir "+strPath, e,true);
        }
    }

    public static ArrayList<String> preg_match(String _pattern, String string, int index) {
        Pattern pattern = Pattern.compile(_pattern);
        Matcher matcher = pattern.matcher(string);
        ArrayList<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group(index));
        }
        return matches;
    }

    public static Boolean isHorizontal(String path) {
        try {
            File filePath = new File(path);
            try {
                SimpleImageInfo imageInfo = new SimpleImageInfo(filePath);
                return imageInfo.getWidth() > imageInfo.getHeight();
            } catch (IOException var3) {
                return null;
            }
        } catch (Exception var4) {
            return null;
        }
    }

    public static BufferedImage getBufferedImage(File tiffFile) throws IOException {
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

    public static String readStringFromFile(File file){
        if(file.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line=reader.readLine())!=null) {
                    File fileLine = new File(line);
                    if(fileLine.exists()){
                        return fileLine.getAbsolutePath();
                    }
                }
            } catch (Exception e) {
                printError(e,true);
            }
        }
        return "";
    }

    public static ArrayList<String> readArrayFromFile(File file){
        ArrayList<String> fileList = new ArrayList<>();
        if(file.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line=reader.readLine())!=null) {
                    File fileLine = new File(line);
                    if(fileLine.exists()){
                        fileList.add(fileLine.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                printError(e,true);
            }
        }
        return fileList;
    }

    public static void delete(String path) {
        File directory = new File(path);
        deleteFile(directory);
    }

    public static void deleteFile(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFile(file);
                }
            }
        }
        if (!directory.delete()) {
            logError(String.valueOf(directory),new Exception("Could not delete"));
        }
    }

    public static void moveFilesBlackWithDir(String code, String from){
        objError objError = objGlobals.errorMap.get(code);
        Path fromPath = Paths.get(from);
        if(!objError.moveFile){
            delete(from);
        }
        else if(Files.exists(fromPath)){
            Path blackFolderPath = Paths.get(objGlobals.targetTiff);
            String to = fromPath.toString().replace(blackFolderPath.toString(),new File(objError.path,code).toString());
            Path toPath = Paths.get(to);
            mkdir(to);
            try {
                Files.move(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException e) {
                logError("copyFiles code " + code + " from " + from, e);
            }
        }
    }

    public static void logResultGray(String code, String from, String result) {
        objConcurrentGrayController.add(from,code,result);
    }

    public static Boolean isActCompiutaGiacenza(String pathA, ArrayList<String> barcodesA, String pathB, ArrayList<String> barcodesB) {
        return isActCompiutaGiacenza(pathA,barcodesA)||isActCompiutaGiacenza(pathB,barcodesB);
    }

    public static Boolean isActCompiutaGiacenza(String path, ArrayList<String> barcodes) {
        for(String barcode:barcodes){
            if(isActCompiutaGiacenza(path,barcode)){
                return true;
            }
        }
        return false;
    }

    public static Boolean isActCompiutaGiacenza(String path, String barcode) {
        if(path.contains("Compiutagiacenza")){
            ArrayList<String> startsWith = new ArrayList<>();
            startsWith.add("788");
            startsWith.add("688");
            startsWith.add("386");
            startsWith.add("286");
            startsWith.add("FUAGLABCINTE");
            startsWith.add("ARAGLABCINTE");
            startsWith.add("FUAGLABSINTE");
            startsWith.add("ARAGLABSINTE");
            startsWith.add("AGB");
            startsWith.add("AGC");
            startsWith.add("AB");
            startsWith.add("AC");
            for(String prefix:startsWith){ if(barcode.startsWith(prefix)){ return true;}}
        }
        return false;
    }

    public static String barcode(String path){
        File file = new File(path);
        String filename = file.getName().replace("RAC-EST", "RAC_EST");
        String[] split = filename.split("-");
        return split[6];
    }

    public static boolean isBack(String path){
        return path.contains("Backside")||path.contains("-RETRO");
    }

    public static void mkDir(String path) throws IOException {
        File file = new File(path);
        if (file.getParentFile() != null) {
            Files.createDirectories(file.getParentFile().toPath());
        }
    }

}
