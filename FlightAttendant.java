import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class FlightAttendant implements Runnable{

   private static long time = System.currentTimeMillis();

   private String name;

   private void setName(String name) {
      this.name = name;
   } //Constructor

   private String getName() {
      return this.name;
   }

   public FlightAttendant(int id){
      setName("FlightAttendant-" + id);
   } //Constructor

   private void msg(String m){
      System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+ m + "\n");
   } //Message for Passenger threads

   public static Semaphore callZone2 = new Semaphore(0, true); //Blocks FlightAttendant announcement until it is time to board Zone 2

   public static Semaphore callZone3 = new Semaphore(0, true); //Blocks FlightAttendant announcement until it is time to board Zone 3

   public static Semaphore z1LineReady = new Semaphore(0, true); //Blocks FlightAttendant until everyone in Zone 1 is in line for boarding

   public static Semaphore z2LineReady = new Semaphore(0, true); //Blocks FlightAttendant until everyone in Zone 2 is in line for boarding

   public static Semaphore z3LineReady = new Semaphore(0, true); //Blocks FlightAttendant until everyone in Zone 3 is in line for boarding

   public static Semaphore gatesClosing = new Semaphore(0,true ); //Blocks FlightAttendant until it is time to close the gates

   public static Semaphore disembarkTime = new Semaphore(0, true); //Blocks FlightAttendant until it is time to make announcement for disembarking

   public static Semaphore cleanLeavePlane = new Semaphore(0, true); //Blocks FlightAttendant until all passengers have left the plane

   @Override
   public void run() {

      //FlightAttendant is blocked until Clock signals when to announce for boarding
      try {
         Clock.announceTime.acquire();
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }

      //makes announcement to let passengers in
      msg("is calling passengers to board, starting with Zone 1.");
      Passenger.seatWait.release(Passenger.seatWait.getQueueLength());

      if(!KioskClerk.zone1.isEmpty()) {
         try {
            z1LineReady.acquire();
            Passenger.wsz1.release(KioskClerk.zone1.size());
         } catch(InterruptedException e) {
            System.out.println("Main thread Interrupted");
         }
      }

      //Waits until all passengers in zone 1 have boarded the plane before calling the zone 2
      if(!KioskClerk.zone1.isEmpty()) {
         try {
            callZone2.acquire();
         } catch(InterruptedException e) {
            System.out.println("Main thread Interrupted");
         }
      }
      msg("Now boarding passengers in Zone 2");
      Passenger.z2p.release(Passenger.z2p.getQueueLength());

      if(!KioskClerk.zone2.isEmpty()) {
         try {
            z2LineReady.acquire();
            Passenger.wsz2.release(KioskClerk.zone2.size());
         } catch(InterruptedException e) {
            System.out.println("Main thread Interrupted");
         }
      }

      //Waits until all passengers in zone 2 have boarded the plane before calling the zone 3
      if(!KioskClerk.zone1.isEmpty() || !KioskClerk.zone2.isEmpty()) {
         try {
            callZone3.acquire();
         } catch(InterruptedException e) {
            System.out.println("Main thread Interrupted");
         }
      }
      msg("Now boarding passengers in Zone 3");
      Passenger.z3p.release(Passenger.z3p.getQueueLength());

      if(!KioskClerk.zone3.isEmpty()) {
         try {
            z3LineReady.acquire();
            Passenger.wsz3.release(KioskClerk.zone3.size());
         } catch(InterruptedException e) {
            System.out.println("Main thread Interrupted");
         }
      }

      //FlightAttendant announces that gates are closed
      try {
         gatesClosing.acquire();
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }

      try {
         sleep(5000);
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      } //sleeps for a few milliseconds to let last passengers enter plane before closing gates
      msg("Gates are now closed, please rebook your flights if you did not arrive on time.");

      //FlightAttendant signals that the flight has landed after a fixed amount of time (flight time)
      try {
         sleep(20000);
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }
      msg("Plane has landed, get ready to disembark the plane");
      Passenger.pLanding.release(Passenger.pLanding.getQueueLength());

      //FlightAttendant waits for signal from Clock to signal Passengers to disembark
      try {
         disembarkTime.acquire();
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }

      msg("It is now time to disembark the plane.");
      Passenger.disembark.release(Passenger.disembark.getQueueLength());

      //FlightAttendant waits until all passengers leave the aircraft so it can clean the plane and leave
      msg("The flight attendant is waiting for all passengers to disembark.");
      try {
         cleanLeavePlane.acquire();
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }

      try {
         sleep(2000);
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      } //sleeps for a few milliseconds to let the last passenger leave the plane before cleaning up
      msg("The flight attendant cleans up the plane and leaves.");
   }
}
