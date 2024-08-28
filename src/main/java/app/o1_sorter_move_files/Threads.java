package app.o1_sorter_move_files;

import java.util.ArrayList;

import static app.functions.logError;
import app.objGlobals;

public class Threads extends functions{
    private static int runningThreads=0;
    private static final ArrayList<ThreaCopyFiles> list = new ArrayList<>();

    public static void start(ThreaCopyFiles thread) { 
        ArrayList<ThreaCopyFiles> toDelete = new ArrayList<>();
        while(runningThreads>=objGlobals.totalThreadsMoveFiles){
            for(ThreaCopyFiles runningThread:list){
                if(!runningThread.isRunning()&&!toDelete.contains(runningThread)){
                    if (runningThread.isCompletedSuccessfully()) {} 
                    else if (runningThread.hasError()) { logError("Thread error "+runningThread.from, runningThread.getException());} 
                    else { logError("Thread error dont know why "+runningThread.from, new Exception());}
                    toDelete.add(runningThread);runningThreads--;break;
                }
            }
        }
        for(ThreaCopyFiles done:toDelete){ list.remove(done);}
        thread.start();
        list.add(thread);
        runningThreads++;
    }

    public static void waitRunning() {
        ArrayList<ThreaCopyFiles> toDelete = new ArrayList<>();
        while(runningThreads>0){
            for(ThreaCopyFiles runningThread:list){
                if(!runningThread.isRunning()&&!toDelete.contains(runningThread)){
                    if (runningThread.isCompletedSuccessfully()) { 
                    } else if (runningThread.hasError()) { logError("Thread error "+runningThread.from, runningThread.getException());
                    } else { logError("Thread error dont know why "+runningThread.from, new Exception());}
                    toDelete.add(runningThread);runningThreads--;
                }
            }
        }
        for(ThreaCopyFiles done:toDelete){ list.remove(done);}
    }
    
}