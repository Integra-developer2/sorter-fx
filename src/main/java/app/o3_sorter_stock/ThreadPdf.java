package app.o3_sorter_stock;

public class ThreadPdf extends Thread {
    private Thread t;
    private final String threadName;
    private final String from;
    private final String to;
    StepPdf obj;
    public ThreadPdf( String threadName, StepPdf obj, String from, String to ) {
        this.threadName = threadName;
        this.obj = obj;
        this.from = from;
        this.to = to;
    }
    @Override
    public void run() {
        obj.start(from, to);
    }
    @Override
    public void start () {
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
        ThreadCount.addPdf(this);
    }
    public boolean isRunning() {
        if (t != null) {
            return t.isAlive();
        }
        return false;
    }
}
