package app.o2_sorter_gray;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static app.functions.logError;
import static app.functions.mkdir;
import app.objError;
import app.objGlobals;

public class functions {
   
   public static String folder(String baseFolder, String folder){
      File ret = new File(baseFolder, folder);
      return ret.toString();
   }

   public static double round(double value, int places) {
      if (places < 0) {
         throw new IllegalArgumentException();
      }

      BigDecimal bd = BigDecimal.valueOf(value);
      bd = bd.setScale(places, RoundingMode.HALF_UP);
      return bd.doubleValue();
   }
   
   public static void moveFilesBlackWithDir(String code, ArrayList<String> list){
      for (String from : list) { moveFilesBlackWithDir(code, from);}
   }

   
   public static void delete(String path) {
      File directory = new File(path);
      deleteFile(directory);
   }

   public static void deleteFile(File directory) {
      if (directory.isDirectory()) {
         File[] files = directory.listFiles();
         if (files != null) {
            File[] var2 = files;
            int var3 = files.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               File file = var2[var4];
               deleteFile(file);
            }
         }
      }
      directory.delete();
   }
   
   public static void moveFilesBlackWithDir(String code, String from){
      objError objError = (objError) objGlobals.errorMap.get(code);
      Path fromPath = Paths.get(from);
      if(!objError.moveFile){
         delete(from);
      }
      else if(Files.exists(fromPath)){
         Path path = Paths.get(from);
         Path blackFolderPath = Paths.get(objGlobals.targetTiff);
         String to = path.toString().replace(blackFolderPath.toString(),objError.path);
         Path toPath = Paths.get(to);
         mkdir(to);
         try {
            Files.move(Paths.get(from), toPath, StandardCopyOption.REPLACE_EXISTING);
         } catch (IOException e) {
            logError("copyFiles code " + code + " from " + from, e);
         }
      }
   }

   public static void logResultGray(String code, String from, String result) {
      objConcurrentGrayController.add(from,code,result);
   }
   
   public static String strPad(String inputString, int length, String append) {
      if (inputString.length() >= length) {
         return inputString;
      } else {
         StringBuilder sb = new StringBuilder();
         while(sb.length() < length - inputString.length()) {
            sb.append(append);
         }
         sb.append(inputString);
         return sb.toString();
      }
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
      if(path.contains("1.Compiutagiacenza")){
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
         return false;
      }
      else{
         return false;
      }
   }

   public static String barcode(String path){
      File file = new File(path);
      String filename = file.getName().replace("RAC-EST", "RAC_EST");
      String[] split = filename.split("-");
      return split[6];
   } 
}
