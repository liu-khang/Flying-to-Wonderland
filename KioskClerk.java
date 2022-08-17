import java.util.Vector;
import java.util.concurrent.Semaphore;

public class KioskClerk implements Runnable {
   public static Vector<Passenger> zone1 = new Vector<>();
   public static Vector<Passenger> zone2 = new Vector<>();
   public static Vector<Passenger> zone3 = new Vector<>();
   public static long time = System.currentTimeMillis();

   int boardCounter;
   public String name;


   public KioskClerk(int id){
      setName("KioskClerk-" + id);
   } //Constructor

   public void setName(String name){
      this.name = name;
   }

   public String getName(){
      return this.name;
   }

   public void msg(String m){
      System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+ m + "\n");
   }

   public static Semaphore waitForPassengers = new Semaphore(0, true);

   @Override
   public void run() {
      msg("is waiting for passengers to line up.");

      try {
         waitForPassengers.acquire();
      } catch (InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }

      while(boardCounter != Plane.nPassengers) {
         int rand = 1 + ((int)(Math.random() * ((30 - 1) + 1)));
         //Assigns seats and determines zones of customers
         try {
            //Zone1
            if(rand >= 1 && rand <= 10) {
               Passenger.lineList.get(0).setZone(1);
               Passenger.lineList.get(0).setSeatNum(rand);
               System.out.println(Passenger.lineList.get(0).getName() + ": Zone #" + Passenger.lineList.get(0).getZone() + " - " + "Seat #" + Passenger.lineList.get(0).getSeatNum() + "\n");
               zone1.add(Passenger.lineList.remove(0));
               Passenger.counterSem.release();
               boardCounter++;
            }
            //Zone2
            if(rand >= 11 && rand <= 20) {
               Passenger.lineList.get(0).setZone(2);
               Passenger.lineList.get(0).setSeatNum(rand);
               System.out.println(Passenger.lineList.get(0).getName() + ": Zone #" + Passenger.lineList.get(0).getZone() + " - " + "Seat #" + Passenger.lineList.get(0).getSeatNum() + "\n");
               zone2.add(Passenger.lineList.remove(0));
               Passenger.counterSem.release();
               boardCounter++;
            }
            //Zone3
            if(rand >= 21 && rand <= 30) {
               Passenger.lineList.get(0).setZone(3);
               Passenger.lineList.get(0).setSeatNum(rand);
               System.out.println(Passenger.lineList.get(0).getName() + ": Zone #" + Passenger.lineList.get(0).getZone() + " - " + "Seat #" + Passenger.lineList.get(0).getSeatNum() + "\n");
               zone3.add(Passenger.lineList.remove(0));
               Passenger.counterSem.release();
               boardCounter++;
            }
         } catch(ArrayIndexOutOfBoundsException e) { }
      }
      msg("All passengers have received their boarding passes, the check-in clerks are now done for the day.");

   }
}
