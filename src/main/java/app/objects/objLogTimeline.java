package app.objects;

import java.util.concurrent.ConcurrentHashMap;

public class objLogTimeline {
    public static ConcurrentHashMap<String,String> logQueue = new ConcurrentHashMap<>();

    public static void add(String page,String message){
        logQueue.putIfAbsent(page, message);
    }
}
