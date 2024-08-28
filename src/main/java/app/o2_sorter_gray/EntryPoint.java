package app.o2_sorter_gray;
import java.util.ArrayList;
import java.util.HashMap;

public class EntryPoint extends functions {

   public static void start() throws Exception{
      try {
         grayFiles();
         blackFiles();
         objJobSorter.list();
         readBarcodes();
         moveGFNF();
         prints();
      } catch (Exception e) { throw e;}
   }

   private static void prints(){
      objConcurrentGrayController.printAll();
      objConcurrentBlackController.printAll();
   }

   private static void moveGFNF(){
      for(String barcode:objFilesBlack.barcodeFiles.keySet()){
         if(!objConcurrentBlackController.barcodeStatus.containsKey(barcode)){
            ArrayList<String> files = objFilesBlack.barcodeFiles.get(barcode);
            for (String file : files) {
               moveFilesBlackWithDir("GFNF", file);
               objConcurrentBlackController.add("GFNF",barcode,file);
            }
         }
      }
   }

   private static void readBarcodes() {
      if(!objFilesGray.toBarcodeReader.isEmpty()){
         int threadIndex = 0;
         for(String path : objFilesGray.toBarcodeReader) {
            Threads.start(new ThreadReadBarcode("bcr_"+threadIndex++, new ThreadObjReadBarcode(), path), objFilesGray.toBarcodeReader.size());
         }
         Threads.waitRunning();
      }
   }

   private static void grayFiles() throws Exception{
      objFilesGray.list();
      if (objFilesGray.all.isEmpty()) {
         throw new Exception("Nessun File Grigio");
      } else {
         for (String file : objFilesGray.files) {
            String front = file.replace("PREFIX", "Camera");
            String back = file.replace("PREFIX", "BacksideCamera");
            if(!objFilesGray.all.contains(front)){ objConcurrentGrayController.add(back,"CFNF",null);
            } else if(!objFilesGray.all.contains(back)){ objConcurrentGrayController.add(front,"CFNF",null);
            } else{ objFilesGray.toBarcodeReader.add(file);}
         }
      }
   }

   private static void blackFiles() throws Exception{
      objFilesBlack.list();
      if (!objFilesBlack.all.isEmpty()) { 
         for(String file : objFilesBlack.list) {
            String front = file + "-FRONTE.tiff";
            String back = file + "-RETRO.tiff";
            if (!objFilesBlack.all.contains(front)) {
               objConcurrentBlackController.add("CGNF",barcode(back),back);
               delete(back);
            } else if (!objFilesBlack.all.contains(back)) {
               objConcurrentBlackController.add("CGNF",barcode(front),front);
               delete(front);
            }
         }
         for(String barcode:objFilesBlack.barcodeFiles.keySet()){
            ArrayList<String>files=objFilesBlack.barcodeFiles.get(barcode);
            if(files.size()>2){
               int bigger = 0;
               HashMap<Integer, ArrayList<String>> byProgSorter = new HashMap<>();
               for (String file : files) {
                  String[] split = file.split("-");
                  int progSorter = Integer.parseInt(split[split.length-3]+split[split.length-2]);
                  if(progSorter > bigger){ bigger = progSorter;}
                  if(byProgSorter.containsKey(bigger)){
                     byProgSorter.get(bigger).add(file);
                  }else{
                     ArrayList<String> newList = new ArrayList<>();
                     newList.add(file);
                     byProgSorter.put(bigger, newList);
                  }
               }
               for(int progSorter:byProgSorter.keySet()){
                  for(String file:byProgSorter.get(progSorter)){
                     if(progSorter!=bigger){
                        objConcurrentBlackController.add("ISMT",barcode,file);
                        delete(file);
                     }
                  }
               }
            }
         }
         objFilesBlack.refreshBarcodeFiles();
      }
   }

}
