package app.o3_sorter_stock;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class objDonePdf {
    public static Queue<String> done = new ConcurrentLinkedQueue<>();
    public static Queue<String> error = new ConcurrentLinkedQueue<>();

    public static void done(String path){
        done.add(path);
    }

    public static void error(String path){
        error.add(path);
    }

}
