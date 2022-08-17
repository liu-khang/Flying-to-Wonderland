import java.util.concurrent.Semaphore;

public class Clock extends Thread {
   private static long time = System.currentTimeMillis();

   public static Semaphore announceTime = new Semaphore(0, true); //Blocks FlightAttendant then signals it when its time to board the plane

   public static Semaphore terminateTime = new Semaphore(0,true); //Blocks Clock until all passengers have left the plane so it can terminate

   public Clock(int id){
      setName("Clock-" + id);
   } //Constructor

   private void msg(String m){
      System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
   }

   @Override
   public void run() {
      //Clock sleeps until it is time to signal the FlightAttendant thread for boarding
      try {
         sleep(40000);
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }

      //Signals the announceTime semaphore so the FlightAttendant thread knows its time to start boarding
      announceTime.release();

      //signal for disembarking
      try {
         sleep(40000);
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }
      FlightAttendant.disembarkTime.release();

      //Clock waits until all passengers disembark then it will terminate
      try {
         terminateTime.acquire();
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }
      try {
         sleep(2500);
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      } //sleeps for a few milliseconds to let the last passenger leave the planes before terminating
      msg("Clock terminated.");

   }
}
