
package app.o3_sorter_stock;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import app.objGlobals;

public class ThreadCount extends functions{
    public static Queue<ThreadPdf> pdf = new ConcurrentLinkedQueue<>();
    public static void addPdf(ThreadPdf thread){
        if(pdf.size()>=objGlobals.totalThreadsStock){
            waitMaxPdf();
        }
        pdf.add(thread);
    }
    public static void waitMaxPdf(){
        while (pdf.size()>=objGlobals.totalThreadsStock) {
            checkPdf();
        }
    }
    public static void waitPdf(){
        while (!pdf.isEmpty()) {
            checkPdf();
        }
    }
    public static void checkPdf() {
        Iterator<ThreadPdf> iterator = pdf.iterator();
        while (iterator.hasNext()) {
            ThreadPdf thread = iterator.next();
            if (!thread.isRunning()) {
                iterator.remove();
            }
        }
    }

}
