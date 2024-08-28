package app.o2_sorter_gray;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static app.functions.logError;
import static app.functions.updateProgressGray;
import app.objGlobals;
import static app.objGlobals.PRINT_AT;

public class Threads extends functions{
    private static int runningThreads=0;
    private static boolean donePrinting=false;
    private static int doneThreads=0;
    private static int printed=0;
    private static int total;
    private static final ArrayList<ThreadReadBarcode> list = new ArrayList<>();

    public static void start(ThreadReadBarcode thread, int _total){
        total=_total;
        refresh();
        ArrayList<ThreadReadBarcode> toDelete = new ArrayList<>();
        while(runningThreads>=objGlobals.totalThreadsGray){
            for(ThreadReadBarcode runningThread:list){
                refresh();
                if(!runningThread.isRunning()&&!toDelete.contains(runningThread)){
                    if (runningThread.isCompletedSuccessfully()) {}
                    else if (runningThread.hasError()) { logError("Thread error "+runningThread.path, runningThread.getException());}
                    else { logError("Thread error dont know why "+runningThread.path, new Exception());}
                    toDelete.add(runningThread);runningThreads--;doneThreads++;
                }
            }
        }
        for(ThreadReadBarcode done:toDelete){
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
        ArrayList<ThreadReadBarcode> toDelete = new ArrayList<>();
        while(runningThreads>0){
            for(ThreadReadBarcode runningThread:list){
                refresh();
                if(!runningThread.isRunning()&&!toDelete.contains(runningThread)){
                    if (runningThread.isCompletedSuccessfully()) {
                    } else if (runningThread.hasError()) { logError("Thread error "+runningThread.path, runningThread.getException());
                    } else { logError("Thread error dont know why "+runningThread.path, new Exception());}
                    toDelete.add(runningThread);runningThreads--;doneThreads++;
                }
            }
        }
        for(ThreadReadBarcode done:toDelete){
            list.remove(done);
        }
        refresh();
    }

    private static void progress(int count, int total) {
        double value = (double)count/(double)total;
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(4, RoundingMode.HALF_DOWN);
        updateProgressGray(bd.doubleValue());
    }

}