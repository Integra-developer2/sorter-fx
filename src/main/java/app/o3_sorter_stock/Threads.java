package app.o3_sorter_stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static app.functions.logError;
import static app.functions.updateProgressStock;
import app.objGlobals;
import static app.objGlobals.PRINT_AT;

public class Threads extends functions{
    private static int runningThreads=0;
    private static boolean donePrinting=false;
    private static int doneThreads=0;
    private static int printed=0;
    private static int total;
    private static final ArrayList<ThreadPdf> list = new ArrayList<>();

    public static void start(ThreadPdf thread, int _total){
        total=_total;
        refresh();
        ArrayList<ThreadPdf> toDelete = new ArrayList<>();
        while(runningThreads>=objGlobals.totalThreadsStock){
            for(ThreadPdf runningThread:list){
                refresh();
                if(!runningThread.isRunning()&&!toDelete.contains(runningThread)){
                    if (runningThread.isCompletedSuccessfully()) {}
                    else if (runningThread.hasError()) { logError("Thread error "+runningThread.from, runningThread.getException());}
                    else { logError("Thread error dont know why "+runningThread.from, new Exception());}
                    toDelete.add(runningThread);runningThreads--;doneThreads++;
                }
            }
        }
        for(ThreadPdf done:toDelete){
            list.remove(done);
        }
        thread.start();
        list.add(thread);
        runningThreads++;
    }

    private static void refresh(){
        if((doneThreads - printed)>PRINT_AT){ progress(doneThreads, total);printed += PRINT_AT;}
        if(doneThreads==0&&printed==0){ progress(doneThreads, total);printed=1;}
        if(doneThreads==total&&!donePrinting){ progress(1, 1);donePrinting=true;}
    }

    public static void waitRunning(){ refresh();
        ArrayList<ThreadPdf> toDelete = new ArrayList<>();
        while(runningThreads>0){
            for(ThreadPdf runningThread:list){
                refresh();
                if(!runningThread.isRunning()&&!toDelete.contains(runningThread)){
                    if (runningThread.isCompletedSuccessfully()) {
                    } else if (runningThread.hasError()) { logError("Thread error "+runningThread.from, runningThread.getException());
                    } else { logError("Thread error dont know why "+runningThread.from, new Exception());}
                    toDelete.add(runningThread);runningThreads--;doneThreads++;
                }
            }
        }
        for(ThreadPdf done:toDelete){
            list.remove(done);
        }
        refresh();
    }

    private static void progress(int count, int total) {
        double value = (double)count/(double)total;
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(4, RoundingMode.HALF_DOWN);
        updateProgressStock(bd.doubleValue());
    }

}