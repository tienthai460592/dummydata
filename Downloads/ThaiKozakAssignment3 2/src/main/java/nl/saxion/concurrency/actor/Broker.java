package nl.saxion.concurrency.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import nl.saxion.concurrency.Hotel;
import nl.saxion.concurrency.messages.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;

public class Broker extends AbstractActor {

    ArrayList<Hotel> hotels = new ArrayList<>();

    public Broker() {
        getContext().getSystem().getEventStream().subscribe(getSelf(), GetHotelList.class);
    }

    public enum Message {
        GET_HOTELS
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, message -> {
                    switch (message) {
                        case GET_HOTELS:
                            getSender().tell(new HotelList(hotels), getSelf());
                            break;
                        default:
                            System.out.println("No such request");
                            break;
                    }
                })
                .match(GetHotelList.class, mess -> {
                    Hotel hotel = new Hotel(mess.getName(),mess.getRef());
                    if(!hotels.contains(hotel)){
                        hotels.add(hotel);
                    }
                } )
                .match(ConfirmReservation.class, message -> {
                    ActorRef hotel = findHotel(message.getHotelName());
                    if (hotel != null) {
//                        log.info("Confirmation succeeded");
                        hotel.tell(new ConfirmReservation(message.getHotelName(), message.getCode(), message.getClientAddress()), getSelf());
                    } else {
                        getSender().tell(new Reply(400,"Hotel not found", getSender()), getSelf());
                    }
                })
                .match(Reply.class, mess -> {
                    mess.getRecipient().tell(new Reply(mess.getCode(),mess.getMessage(), mess.getRecipient()), getSelf());
                })
                .match(CreateReservation.class, message -> {
                    ArrayList<String> hotelNameLists = message.getHotelLists();
                    for (String hotelName : hotelNameLists) {
                        ActorRef hotel = findHotel(hotelName);
                        if (hotel != null) {
                            hotel.tell(new RequestRoom(message.getClientAddress()), getSelf());
                        } else {
                            getSender().tell(new Reply(400,"No such hotel", getSender()), getSelf());
                        }
                    }
                })
                .match(CancelReservation.class, message -> {

                    ActorRef hotel = findHotel(message.getHotelName());
                    Timeout timeout = new Timeout(Duration.create(10, "seconds"));
                    if (hotel != null) {
                        Future<Object> future;
                        future = Patterns.ask(hotel, message, timeout);
                        getSender().tell(Await.result(future, timeout.duration()), getSelf());
                    } else {
                        getSender().tell(new Reply(400,"Hotel not found", getSender()), getSelf());
                    }
                })
                .build();
    }

    private ActorRef findHotel(String name) {

        for (Hotel hotel : hotels) {
            if (hotel.getName().equalsIgnoreCase(name)) {
                return hotel.getRef();
            }
        }
        return null;
    }

}




