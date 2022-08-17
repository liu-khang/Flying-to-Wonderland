# Flying-to-Wonderland

This project demonstrates the utilization of multithreading in programs. In this specific project I was able to use a single set of code for several processes that are at different stages of execution, thus eliminating the need to have duplicate copies of the program running at the same time.

Passengers of Flight CS-340 to Purell-Wonderland, NY arrive at the airport 3 hours before departure (use
sleep(random_time) to simulate arrival). Upon their arrival, they go straight to the check-in counter to
have their boarding passes printed.

Unfortunately, due to budget cuts, the airline can only maintain 2 check-in counters at this time. To avoid
crowding at the check-in counters, the airline asks passengers to form lines (wait). On which line the
passenger should be decided randomly.

At each counter, there is a check-in clerk who will assist the passengers. Passengers will receive their
boarding pass from the check-in clerk with a seat and zone number printed on it. The check-in clerk will
generate this number and assign it to the passenger. The seat number is a random number between 1
and 30 with a corresponding zone number; passengers with seat numbers between 1 and 10 are in Zone
1, passengers with seat numbers between 11 and 20 are in Zone 2, passengers with seat numbers
between 21 and 30 are in Zone 3. Note that the aircraft holds only 30 passengers and is split up into 3
zones. (Output a message to the screen with the passenger’s seat and zone information).
After all passengers receive their boarding pass, the check-in clerks are done for the day (they terminate)
Once the passengers arrive at the gate, they take a seat and wait for the flight attendant to call for
passengers to board.

A half an hour before the plane departs, the flight attendant begins to call passengers up to the door of
the jet bridge. The flight attendant calls the first zone (maybe you would like to have implement different
semaphores, one for each zone). At the door, the passengers are asked to wait in line until all others
have boarded. When all have arrived, the passengers enter the plane in groups (determined by
groupNum), so that passengers can comfortably stow their belongings and take their seats.
The flight attendant calls each of the remaining zones the same way.
After all zones have boarded, the flight attendant makes an announcement indicating that the door of the
plane has closed. All passengers that arrive at the gate after this announcement are asked to rebook
their flight and return home (these threads terminate). All other passengers wait for the flight en route
to their destination.

Two hours pass, and the plane prepares for landing. The flight attendant signals the passengers that
the flight has landed.

The plane lands and the passengers wait for the go-ahead to disembark the plane. Passengers are
asked to leave the plane in ascending order of their seat number (let’s say on the plane you have Thread-
3 (seat 2), Thread-4 (seat 3), Thread-2 (seat 4) and Thread-1 (seat 1); the order in which they leave is
Thread-1, Thread-3, Thread-4, Thread-2;

The passengers disperse after the flight and go off to their respective destinations (threads terminate).
The flight attendant cleans the aircraft and is the last to leave after all the passengers. (thread terminates).
