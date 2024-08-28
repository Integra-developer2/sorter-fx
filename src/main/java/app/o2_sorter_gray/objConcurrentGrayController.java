package app.o2_sorter_gray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static app.functions.logError;
import static app.functions.mkdir;
import app.objGlobals;

public class objConcurrentGrayController extends functions{
   public static ConcurrentHashMap<String, String> all = new ConcurrentHashMap<>();
   public static ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<>();
   private static final AtomicBoolean isPrinting = new AtomicBoolean(false);
   private static int step = 0;

   public static void justAdd(String path, String status, String result) {
      if(!all.containsKey(path)){
         list.add(path+";"+status+";"+result);
         all.put(path, status+";"+result);
      }
   }
   
   public static void add(String path, String status, String result) {
      if(!all.containsKey(path)){
         list.add(path+";"+status+";"+result);
         all.put(path, status+";"+result);
         if(list.size()>=objGlobals.PRINT_AT && isPrinting.compareAndSet(false, true)){ printLog();}
      }
   }

   public static void printLog(){
      File output = new File(objGlobals.logGray, "lg_"+step++);
      String outputPath=output.getPath();
      mkdir(outputPath);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath,true))) {
         String line;while((line=list.poll())!=null){ writer.append(line+"\n"); }
      } catch (Exception e) { logError("objGrayController printLog",e);
      } finally { isPrinting.set(false);}
   }

   public static void printAll(){ printLog();
      File output = new File(objGlobals.logGraytxt);
      mkdir(objGlobals.logGray);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(output.getPath(),true))) {
         for(String file : all.keySet()){ 
            String line = file+";"+all.get(file);
            writer.append(line+"\n");
         }
      } catch (Exception e) { logError("objGrayController printAll",e);}
   }

}