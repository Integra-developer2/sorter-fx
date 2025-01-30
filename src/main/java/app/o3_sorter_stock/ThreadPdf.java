package app.o3_sorter_stock;

public class ThreadPdf extends Thread {
   private Thread t;
   private final String threadName;
   private final ThreadObjPdf obj;
   public final String from;
   public final String to;
   private boolean completedSuccessfully;
   private Exception exception;

   ThreadPdf(String _threadName, ThreadObjPdf _obj, String _from, String _to) {
      this.threadName = _threadName;
      this.obj = _obj;
      this.from = _from;
      this.to = _to;
      this.completedSuccessfully = false;
      this.exception = null;
   }

   @Override
   public void run() {
      try {
         this.obj.start(this.from, this.to);
         this.completedSuccessfully = true;
      } catch (Exception e) {
         this.exception = e;
      }
   }

   @Override
   public synchronized void start() {
      if (this.t == null) {
         this.t = new Thread(this, this.threadName);
         this.t.start();
      }
   }

   public boolean isRunning() {
      return this.t != null && this.t.isAlive();
   }

   public boolean isCompletedSuccessfully() {
      return this.completedSuccessfully;
   }

   public boolean hasError() {
      return this.exception != null;
   }

   public Exception getException() {
      return this.exception;
   }
}
