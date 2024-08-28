package app.o3_sorter_stock;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

public class objDoneStock extends functions{
    public static String lastNumber;
    private static final ConcurrentLinkedDeque<String> list = new ConcurrentLinkedDeque<>();
    private static final ReentrantLock lock = new ReentrantLock();

    public static void add(String stockNumber){
        list.add(stockNumber);
    }

    public static String getNext() {
        lock.lock();
        try {
            String last = list.peekLast();
            int intLast = Integer.parseInt(last);
            String next = strPad(String.valueOf(intLast+1),4,"0");
            list.add(next);
            lastNumber=next;
            return next;
        } finally {
            lock.unlock();
        }
    }

}
