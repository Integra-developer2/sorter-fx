package app.steps;

import app.classes.AllBlackFiles;
import app.Routing;
import app.classes.UI;
import app.objects.objGlobals;
import app.objects.objLogTimeline;
import app.objects.objProgressItem;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static app.functions.*;
import static java.lang.Thread.sleep;

public class moveFiles {
    public static void start(){
        try{
            objGlobals.totalThreads = 1;
            HashMap<String, String> map = map();
            if(map.isEmpty()){
                logError("warning",new Exception("Non ci sono file da copiare"));
                AllBlackFiles.writeTxt();
                Routing.moveFiles="end";
            }
            else if(outOfSpace()&&!Routing.moveFiles.equals("outOfSpace")){
                Routing.moveFiles="outOfSpace";
            }
            else{
                copy(map);
                check(map);
                UI.controller.addSpinner("Ultimi preparativi");
                AllBlackFiles.writeTxt();
                UI.controller.removeSpinner();
                Routing.moveFiles="end";
            }
        }
        catch(Exception e){
            printError(e,true);
        }

    }

    private static void copy(HashMap<String, String> map) throws InterruptedException {
        if (!map.isEmpty()) {
            AtomicInteger count = new AtomicInteger(0);
            Integer total = map.size();
            ArrayList<Thread> threads = new ArrayList<>();
            objProgressItem pi = UI.controller.addProgress("Copio i file",total);

            for (String from : map.keySet()) {
                while (threads.size() >= objGlobals.totalThreads) {
                    refreshThreads(count, threads, pi, from,100);
                }
                Thread newThread = newThread(from, map.get(from), "copyFiles-"+count.get());
                newThread.start();
                threads.add(newThread);
            }

            while (!threads.isEmpty()) {
                refreshThreads(count, threads, pi, String.valueOf(count),100);
            }
        }
    }

    private static void refreshThreads(AtomicInteger count, ArrayList<Thread> threads, objProgressItem pi, String text, int add) throws InterruptedException {
        Iterator<Thread> it = threads.iterator();
        while (it.hasNext()) {
            Thread t = it.next();
            if (!t.isAlive()) {
                it.remove();
                UI.controller.refresh(pi, count.incrementAndGet());
                objLogTimeline.add("refreshThreads","[ copyFiles ] done "+text);
            }
            else{
                objLogTimeline.add("refreshThreads","[ copyFiles ] running "+text);
            }
        }
        if(cpuUsage()<70.00){
            objGlobals.totalThreads += add;
            sleep(500);
        }
        else if(objGlobals.totalThreads > 1)
        {
            objGlobals.totalThreads -= add;
        }
        objLogTimeline.add("moveFiles","[ objGlobals.totalThreads ] : "+objGlobals.totalThreads);
    }

    private static Thread newThread(String from, String to, String name){
        Thread t = new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                copyFile(from, to);
            return null;
            }
        });
        t.setName(name);
        t.setDaemon(true);
        return t;
    }

    private static Thread newThreadCheck(String fileFrom, String fileTo, String name){
        Thread t = new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    String sumSource = calculateChecksum(fileFrom);
                    String sumTarget = calculateChecksum(fileTo);
                    if (!sumSource.equals(sumTarget)) {
                        logError(fileFrom,new Exception("Checksums don't match"));
                    }
                }
                catch (Exception e) {
                    logError(fileFrom,new Exception("Checksums don't match"));
                }
                return null;
            }
        });
        t.setName(name);
        t.setDaemon(true);
        return t;
    }

    private static void check(HashMap<String, String> map) throws InterruptedException {
        if (!map.isEmpty()) {
            AtomicInteger count = new AtomicInteger(0);
            Integer total = map.size();
            ArrayList<Thread> threads = new ArrayList<>();
            objProgressItem pi = UI.controller.addProgress("Verifico se sono tutti copiati",total);

            for (String from : map.keySet()) {
                while (threads.size() >= objGlobals.totalThreads) {
                    refreshThreads(count, threads, pi, from,1);
                }
                Thread newThread = newThreadCheck(from, map.get(from), "checkFiles-"+count.get());
                newThread.start();
                threads.add(newThread);
            }

            while (!threads.isEmpty()) {
                refreshThreads(count, threads, pi, String.valueOf(count),1);
            }
        }
    }

    public static HashMap<String,String> map() throws IOException {
        UI.controller.addSpinner("Controllo i file tiff");
        Path sourceTiffPath = Paths.get(objGlobals.sourceTiff);
        String strSourceTiff = sourceTiffPath.toString();
        Path targetTiffPath = Paths.get(objGlobals.targetTiff);
        String strTargetTiff = targetTiffPath.toString();
        HashMap<String,String> ret = new HashMap<>();
        Files.walkFileTree(sourceTiffPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
            String from = path.toString();
            String to = from.replace(strSourceTiff, strTargetTiff);
            File fileTo = new File(to);
            if (!fileTo.exists() && from.endsWith(".tiff")) {
                ret.put(from, to);
            }
            return FileVisitResult.CONTINUE;
            }
        });
        UI.controller.removeSpinner();
        UI.controller.addSpinner("Controllo i file grigi");
        Path sourceGrayPath = Paths.get(objGlobals.sourceGray);
        String strSourceGray = sourceGrayPath.toString();
        Path targetGrayPath = Paths.get(objGlobals.targetGray);
        String strTargetGray = targetGrayPath.toString();
        Files.walkFileTree(sourceGrayPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
            String from = path.toString();
            String to = from.replace(strSourceGray, strTargetGray);
            File fileTo = new File(to);
            if (!fileTo.exists() && from.endsWith(".bmp")) {
                ret.put(from, to);
            }
            return FileVisitResult.CONTINUE;
            }
        });

        UI.controller.removeSpinner();
        UI.controller.addSpinner("Controllo i file JobSorter");
        for (String from : objGlobals.sourceJobSorter) {
            File fileFrom = new File(from);
            File folderTo = new File(objGlobals.jobSorterFolder);
            String to = from.replace(fileFrom.getParent(), folderTo.getPath());
            File fileTo = new File(to);
            if(!fileTo.exists()){
                ret.put(from, to);
            }
        }

        UI.controller.removeSpinner();
        UI.controller.addSpinner("Controllo i file Etichette");
        File fileFrom = new File(objGlobals.sourceEtichette);
        File folderTo = new File(objGlobals.etichetteFolder);
        String to = objGlobals.sourceEtichette.replace(fileFrom.getParent(), folderTo.getPath());
        File fileTo = new File(to);
        if(!fileTo.exists()){
            ret.put(objGlobals.sourceEtichette, to);
        }
        UI.controller.removeSpinner();
        return ret;
    }

    public static String calculateChecksum(String file) throws Exception{
        Path path = Paths.get(file);
        StringBuilder sb = new StringBuilder();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Files.readAllBytes(path);
        byte[] checksum = digest.digest(bytes);
        for (byte b : checksum) { sb.append(String.format("%02x", b));}
        return sb.toString();
    }

    private static boolean outOfSpace() throws Exception{
        String targetGrayPartition = objGlobals.targetGray.substring(0,2);
        String targetTiffPartition = objGlobals.targetTiff.substring(0,2);
        if(targetGrayPartition.equals(targetTiffPartition)){
            File diskPartition = new File(targetGrayPartition);
            long freeSpace = diskPartition.getFreeSpace();
            long graySize = folderSize(objGlobals.sourceGray);
            long tiffSize = folderSize(objGlobals.sourceTiff);
            long sourceSize = graySize + tiffSize;
            double sizeLocal = (double) freeSpace / (1024 * 1024 * 1024);
            double sizeSource = (double) sourceSize / (1024 * 1024 * 1024);
            if(sizeLocal<sizeSource){
                objGlobals.noSpace="Servono: "+String.format("%.2f", sizeSource)+" GB. Sono Disponibili: "+String.format("%.2f",sizeLocal)+" GB";
                return true;
            }
        }
        return false;
    }

    public static long folderSize(String directoryPath) throws Exception {
        Path path = Paths.get(directoryPath);
        final long[] size = {0};
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    size[0] += attrs.size();
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            printError(e,true);throw e;
        }
        return size[0];
    }

    public static void copyFile(String from, String to) {
        try {
            File fileTarget = new File(to);
            if (!fileTarget.exists()) {
                File parentFileTarget = new File(fileTarget.getParent());
                if (!parentFileTarget.exists()) {
                    Files.createDirectories(parentFileTarget.toPath());
                }
                copyNio(from,to);
            }
        }
        catch (IOException e) {
            logError("ThreadObjCopyFiles",e);
        }
    }

    public static void copyNio(String from, String to) {
        try {
            Files.copy(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logError("copyNio failed to copy: "+from, e);
        }
    }
}
