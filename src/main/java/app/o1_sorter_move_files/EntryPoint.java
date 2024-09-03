package app.o1_sorter_move_files;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static app.functions.writeAllBlackFiles;
import app.objAnomalies;
import app.objGlobals;

public class EntryPoint extends functions{
    public static void start() throws Exception{
        try{
            HashMap<String, String> map = map();
            if(!map.isEmpty()){
                copy(map);
                check(map);
                writeAllBlackFiles();
            }
        } catch (Exception e) { throw e;}
    }

    public static void copy(HashMap<String, String> map) throws Exception{
        try {
            int threadIndex = 0;
            for(String from : map.keySet()) {
                Threads.start(new ThreaCopyFiles("cf_"+threadIndex++, new ThreadObjCopyFiles(), from, map.get(from)));
            }
            Threads.waitRunning();
        } catch (Exception e) { throw e;}
    }

    public static void check(HashMap<String, String> map) throws Exception {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(objGlobals.totalThreadsMoveFiles);
            for(String file : map.keySet()){
                executor.submit(() -> {
                    try {
                        String fileTarget = map.get(file);
                        String sumSource = calculateChecksum(file);
                        String sumTarget = calculateChecksum(fileTarget);
                        if(!sumSource.equals(sumTarget)){
                            objAnomalies.addMoveFiles(file);
                        }
                    } catch (Exception e) { objAnomalies.addMoveFiles(file);}
                });
            }
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) { throw e;}
    }

}