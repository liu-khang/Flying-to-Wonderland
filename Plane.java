import java.util.Scanner;

public class Plane {
   public static Thread[] passengerArr;
   public static int nPassengers = 30;

   public static void main(String args[]) {
      Scanner reader = new Scanner(System.in);

      //asks user to input desired int
      System.out.print("Please enter an integer between 1 and 30 (number of passengers): ");
      nPassengers = reader.nextInt();
      reader.close();
      if(nPassengers < 1 || nPassengers > 30) throw new IllegalArgumentException("Invalid integer value");
      passengerArr = new Thread[nPassengers];

      //Passenger threads are created according to the integer entered by the user and are put to sleep to simulate arrival time
      for(int i = 0; i < nPassengers; i++){
         passengerArr[i] = (new Thread(new Passenger(i)));
         passengerArr[i].start();
      }

      //The KioskClerk threads are created
      Thread a = new Thread(new KioskClerk(0));
      a.start();

      //Clock thread is created
      Thread c = new Thread(new Clock(0));
      c.start();

      //Flight attendant thread is created
      Thread flightAtt = new Thread(new FlightAttendant(0));
      flightAtt.start();
   }
}
