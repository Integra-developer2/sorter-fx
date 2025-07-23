package app.objects;

import java.util.concurrent.ConcurrentHashMap;

public class objProgressBar {
    public static ConcurrentHashMap<objProgressItem, Integer> objProgressItems = new ConcurrentHashMap<>();

    public static void add(objProgressItem pi,Integer count){
        Integer current = objProgressItems.get(pi);
        if(current==null||count>current){
            objProgressItems.put(pi,count);
        }
    }
}
