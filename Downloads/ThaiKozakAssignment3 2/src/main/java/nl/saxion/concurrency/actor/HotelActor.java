package nl.saxion.concurrency.actor;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import nl.saxion.concurrency.Reservation;
import nl.saxion.concurrency.messages.*;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class HotelActor extends AbstractActorWithTimers {
    private String name;
    private int nrOfRoom;
    private int availableRoom;
    private int confirmedCode;

    boolean failReservation = false;
    private LoggingAdapter log;
    private ArrayList<Reservation>reservations = new ArrayList<>();
    private static int reservationCode = 0;
    private static Object TICK_KEY = "TickKey";

    @Override
    public void preStart() throws Exception {
        getContext().getSystem().getEventStream().publish(new GetHotelList(getSelf(), name));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()

                .match(RequestRoom.class, message ->{
                    if(availableRoom == 0) {
                        getSender().tell(new Reply(400,"No available room", message.getClientRequestAdress()), getSelf());
                    } else {
                        failReservation = false;
                        Reservation reservation = new Reservation(reservationCode);
                        confirmedCode = reservationCode;
                        reservations.add(reservation);
                        //report reservation code to sender
                        getSender().tell(new Reply(200,"You reservation code for " + name + " is: " + reservationCode, message.getClientRequestAdress()), getSelf());
                        reservationCode++;
                        availableRoom--;
                        // the hotel after 20 seconds will send itself a message. If the reservation is not confirmed, hotel will cancel the reservation
                        getTimers().startSingleTimer(TICK_KEY, new Tick(getSender(), message.getClientRequestAdress()), Duration.create(20, TimeUnit.SECONDS));
                    }
                })
                .match(Tick.class, message -> {
                    //remove reservation if not confirmed
                    if(findReservation(confirmedCode) != null) {
                        if (!findReservation(confirmedCode).isConfirmed()) {
                            reservations.remove(findReservation(confirmedCode));
                            availableRoom++;
                            message.brokerAddress.tell(new Reply(400,"Not yet confirmed. Your reservation is canceled!",message.client), getSelf());
                        }
                    }
                    failReservation = true;
                })


                .match(GetHotelName.class, message -> {

                    getSender().tell(name, getSelf());
                })
                .match(ConfirmReservation.class, mess -> {
                    if(!failReservation) {
                        if(findReservation(mess.getCode()) != null) {
                            findReservation(mess.getCode()).confirm(true);
                            String positiveMess = "Your reservation for hotel " +name + " has been confirmed!";

                            getSender().tell(new Reply(200,positiveMess,mess.getClientAddress()), getSelf());
//                            log.info("Your reservation has been confirmed");
                        } else {
                            getSender().tell(new Reply(400,"There is no such reservation", mess.getClientAddress()), getSelf());
//                            log.info("Invalid reservation");
                        }
                    } else {
                        getSender().tell("Invalid reservation", getSelf());
//                        log.info("Invalid reservation");
                    }
                })
                .match(CancelReservation.class, message -> {
                    Reservation reservation = findReservation(message.getReservationCode());
                    if(reservation != null){
                        reservations.remove(reservation);
                        availableRoom ++ ;
                        getSender().tell(new Reply(200,"Reservation in the hotel"+name+ "has been succesfully removed", message.getClientAddress()), getSelf());
//                        log.info("reservation removed!");
                    }else {
                        getSender().tell(new Reply(400,"Invalid code", message.getClientAddress()), getSelf());
//                        log.info("Invalid code");
                    }
                })
                .build();
    }
    private static final class Tick{
        ActorRef brokerAddress;
        ActorRef client;
        public Tick(ActorRef brokerAddress, ActorRef client) {
            this.brokerAddress = brokerAddress;
            this.client = client;
        }
    }

    public HotelActor(String name, int nrOfRoom) {
        this.availableRoom = nrOfRoom;
        this.name = name;
        this.nrOfRoom = nrOfRoom;
        log = Logging.getLogger(getContext().getSystem(),this);
    }

    public static Props props(String name, int rooms) {
        return Props.create(HotelActor.class, () -> new HotelActor(name, rooms));
    }

    private Reservation findReservation(int id) {
        for (Reservation reservation: reservations) {
            if(id == reservation.getId()) {
                return reservation;
            }
        }
        return null;
    }
}
