package app.objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static app.functions.logError;
import static app.functions.mkdir;

public class objConcurrentGrayController {
   public static ConcurrentHashMap<String, String> all = new ConcurrentHashMap<>();
   public static ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<>();
   private static final AtomicBoolean isPrinting = new AtomicBoolean(false);
   private static int step = 0;

   public static void add(String path, String status, String result) {
      if(!all.containsKey(path)){
            list.add(path+";"+status+";"+result);
            all.put(path, status+";"+result);
            int printAt = 100;
            if(list.size()>= printAt && isPrinting.compareAndSet(false, true)){
            printLog();
         }
      }
   }

   public static void printLog(){
      File output = new File(objGlobals.logGray, "lg_"+step++);
      String outputPath=output.getPath();
      mkdir(outputPath);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath,true))) {
         String line;while((line=list.poll())!=null){ writer.append(line).append("\n"); }
      } catch (Exception e) {
         logError("objGrayController printLog",e);
      } finally {
         isPrinting.set(false);
      }
   }

   public static void printAll(){ printLog();
      File output = new File(objGlobals.logGrayTxt);
      mkdir(objGlobals.logGray);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(output.getPath(),true))) {
         for(String file : all.keySet()){ 
            String line = file+";"+all.get(file);
            writer.append(line).append("\n");
         }
      } catch (Exception e) {
         logError("objGrayController printAll",e);
      }
   }

}