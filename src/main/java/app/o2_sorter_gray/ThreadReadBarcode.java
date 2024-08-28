package app.o2_sorter_gray;

public class ThreadReadBarcode extends Thread {
   private Thread t;
   private final String threadName;
   private final ThreadObjReadBarcode obj;
   public final String path;
   private boolean completedSuccessfully;
   private Exception exception;

   ThreadReadBarcode(String _threadName, ThreadObjReadBarcode _obj, String _path) {
      this.threadName = _threadName;
      this.obj = _obj;
      this.path = _path;
      this.completedSuccessfully = false;
      this.exception = null;
   }

   @Override
   public void run() {
      try {
         this.obj.start(this.path);
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
