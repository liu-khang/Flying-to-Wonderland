import java.util.Vector;
import java.util.concurrent.Semaphore;
import static java.lang.Thread.sleep;

public class Passenger implements Runnable {
   String name;
   public static Vector<Passenger> lineList = new Vector<>();
   public static Vector<Passenger> gateLine = new Vector<>();
   /*public static Vector<Passenger> disembarkList = new Vector<>();*/ //Keeps track of passengers that are going to disembark
   private static long time = System.currentTimeMillis();

   int i; //Keeps track of how many people board when the line size is larger than 4
   int id; //Uniquely identifies each Passenger thread
   private int zone; //Passenger's designated zone
   private int seatNum; //Passenger's designated seat number
   private int groupNum = 4; //Maximum amount of Passengers allowed to board the plane at once
   private static int counterNum = 3; //Maximum amount of Passengers allowed in the Kiosk line
   private int boardCount = 0; //keeps track of the amount of boarded passengers
   private int totalZoneCount = 0; //keeps track of the total amount of passengers from each zone waiting to board
   private int passengerCount = 0; //keeps track of the number of Passengers for disembarking later on

   public void setName(String name) {
      this.name = name;
   } //Constructor

   public String getName() {
      return this.name;
   }

   public void setZone(int z) {
      this.zone = z;
   }

   public void setSeatNum(int s) {
      this.seatNum = s;
   }

   public int getZone() {
      return this.zone;
   }

   public int getSeatNum() {
      return this.seatNum;
   }

   public static Semaphore counterSem = new Semaphore(counterNum * 2, true); //Counting semaphore that only lets a certain amount of passengers access the kiosk

   public static Semaphore seatWait = new Semaphore(0, true); //This semaphore blocks all threads until FlightAttendant makes the boarding announcement (signaled by FlightAttendant)

   public static Semaphore z2p = new Semaphore(0, true); //Blocks Zone 2 passengers until it is their time to board (signaled by FlightAttendant)

   public static Semaphore z3p = new Semaphore(0, true); //Blocks Zone 3 passengers until it is their time to board (signaled by FlightAttendant)

   public static Semaphore wsz1 = new Semaphore(0, true); //Blocks Zone 1 passengers until all passengers in Zone 1 are in line to board (signaled by FlightAttendant)

   public static Semaphore wsz2 = new Semaphore(0, true); //Blocks Zone 2 passengers until all passengers in Zone 2 are in line to board (signaled by FlightAttendant)

   public static Semaphore wsz3 = new Semaphore(0, true); //Blocks Zone 3 passengers until all passengers in Zone 3 are in line to board (signaled by FlightAttendant)

   public static Semaphore pLanding = new Semaphore(0, true); //Blocks all passengers until FlightAttendant makes announcement that the plane has landed (signaled by FlightAttendant)

   public static Semaphore disembark = new Semaphore(0, true); //Blocks all passengers until FlightAttendant makes announcement for disembarking (signaled by FlightAttendant)

   public Passenger(int id){
      this.id = id;
      setName("Passenger-" + id);
   }

   public void msg(String m) {
      System.out.println("[" + (System.currentTimeMillis() - time + "]" + getName() + ": " + m + "\n"));
   }

   public void kioskLine() throws InterruptedException {
      msg("is waiting to get in line.");
      counterSem.acquire();
      msg("is in line at the kiosk.");
      lineList.add(this);
   } //Method for line at the Kiosk, used 6 permits to simulate two Kiosks with max line count of 3

   public void sitAndWait() throws InterruptedException {
      msg("is taking a seat and is waiting for the flight attendant announcement");
      seatWait.acquire();
   }

   @Override
   public void run() {
      //simulates arrival
      try {
         sleep(1 + (long)(Math.random() * ((6000 - 1) + 1)));
      } catch (InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }
      msg("has arrived.");

      //Passenger enters line for boarding pass at kiosk, semaphore blocks incoming threads if the number of permits is zero (maximum line amount is reached)
      KioskClerk.waitForPassengers.release();
      try {
         kioskLine();
      }
      catch(Exception e) {
         System.out.println("Problem with the passenger threads.");
      }

      //Sleep is used to simulate passengers heading to the gate to wait for boarding
      msg("is heading to the gate to wait for boarding time.");
      try {
         sleep(1 + (long)(Math.random() * ((10000 - 1) + 1)));
      } catch (InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }

      //Blocks late passengers once gates are closed
      /*if()*/

      //Passenger sits down and awaits (is blocked until) the flight attendant's boarding announcement
      totalZoneCount++;
      try {
         sitAndWait();
      } catch (InterruptedException e) {
         System.out.println("Problem with the passenger threads.");
      }

      //Blocks passengers based on zones, pre-sorting for the announcement
      if(this.getZone() == 2 && !KioskClerk.zone1.isEmpty()){
         try {
            z2p.acquire();
         } catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");
         }
      }

      if(this.getZone() == 3 && (!KioskClerk.zone1.isEmpty() || !KioskClerk.zone2.isEmpty())){
         try {
            z3p.acquire();
         } catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");
         }
      }

      //Passengers in order from Zone 1-3 are lining up to enter the aircraft
      if(this.getZone() == 1) {
         msg("is lining up to enter the airplane");
         gateLine.add(this);

         //Passengers wait in line until everyone from the zone has arrived
         if(gateLine.size() == KioskClerk.zone1.size()) FlightAttendant.z1LineReady.release();
         try {
            wsz1.acquire();
         } catch(InterruptedException e) {
            System.out.println("Main thread Interrupted");
         }

         while(!gateLine.isEmpty()) {
            int size = gateLine.size();
            i = 0;
            if(size > groupNum) {
               while(i != 4) {
                  msg("has boarded the plane.");
                  gateLine.removeElement(this);
                  KioskClerk.zone1.removeElement(this);
                  i++;
                  break;
               }
            }
            else {
               while(!gateLine.isEmpty()) {
                  msg("has boarded the plane.");
                  gateLine.removeElement(this);
                  KioskClerk.zone1.removeElement(this);
                  break;
               }
            }
            break;
         }
         if(KioskClerk.zone1.isEmpty()) FlightAttendant.callZone2.release();
      }

      if(this.getZone() == 2) {
         msg("is lining up to enter the airplane");
         gateLine.add(this);

         //Passengers wait in line until everyone from the zone has arrived
         if(gateLine.size() == KioskClerk.zone2.size()) FlightAttendant.z2LineReady.release();
         try {
            wsz2.acquire();
         } catch(InterruptedException e) {
            System.out.println("Main thread Interrupted");
         }

         while(!gateLine.isEmpty()) {
            int size = gateLine.size();
            i = 0;
            if(size > groupNum) {
               while(i != 4) {
                  msg("has boarded the plane.");
                  gateLine.removeElement(this);
                  KioskClerk.zone2.removeElement(this);
                  i++;
                  break;
               }
            } else {
               while(!gateLine.isEmpty()) {
                  msg("has boarded the plane.");
                  gateLine.removeElement(this);
                  KioskClerk.zone2.removeElement(this);
                  break;
               }
            }
            break;
         }
         if(KioskClerk.zone2.isEmpty()) FlightAttendant.callZone3.release();
      }
      if(KioskClerk.zone2.isEmpty()) FlightAttendant.callZone3.release();

      if(this.getZone() == 3) {
         msg("is lining up to enter the airplane");
         gateLine.add(this);

         //Passengers wait in line until everyone from the zone has arrived
         if(gateLine.size() == KioskClerk.zone3.size()) FlightAttendant.z3LineReady.release();
         try {
            wsz3.acquire();
         } catch(InterruptedException e) {
            System.out.println("Main thread Interrupted");
         }

         while(!gateLine.isEmpty()) {
            int size = gateLine.size();
            i = 0;
            if(size > groupNum) {
               while(i != 4) {
                  msg("has boarded the plane.");
                  gateLine.removeElement(this);
                  KioskClerk.zone3.removeElement(this);
                  i++;
                  break;
               }
            } else {
               while(!gateLine.isEmpty()) {
                  msg("has boarded the plane.");
                  gateLine.removeElement(this);
                  KioskClerk.zone3.removeElement(this);
                  break;
               }
            }
            break;
         }
      }

      //Signals to the FlightAttendant that all the zones have boarded and its time to close the gates
      boardCount++;
      if(boardCount == totalZoneCount) FlightAttendant.gatesClosing.release();

      //Passengers wait until signaled that the flight has landed
      try {
         pLanding.acquire();
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }

      //Passengers wait for the go-ahead to disembark from the plane

      passengerCount++;
      /*disembarkList.add(this);*/
      try {
         disembark.acquire();
      } catch(InterruptedException e) {
         System.out.println("Main thread Interrupted");
      }

      //Passengers disembark the plane according to their seat #
      /*if(passengerCount == totalZoneCount){
         for(int i = 1; i <= 30; i++) {
            for(int j = 0; j < disembarkList.size(); j++) {
               if(disembarkList.get(j).getSeatNum() == i) {
                  System.out.println(disembarkList.get(j).getName() + " is in seat " + disembarkList.get(j).getSeatNum() + " and departs the plane." + "\n");
               }
            }
         }
      }*/
      msg("is in " + "seat " + this.getSeatNum() + " and departs the plane");

      //Signals to the FlightAttendant and Clock that all passengers have disembarked from the plane
      if(passengerCount == totalZoneCount) {
         FlightAttendant.cleanLeavePlane.release();
         Clock.terminateTime.release();
      }

   }

}
