package nl.saxion.concurrency.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import nl.saxion.concurrency.Hotel;
import nl.saxion.concurrency.Main;
import nl.saxion.concurrency.messages.HotelList;
import nl.saxion.concurrency.messages.Reply;

import java.util.ArrayList;

public class ClientActor extends AbstractActor {

    //the repplyCode is to see if the request is in a happy path or not. 200 is a successful one, 400 is a fail one
    public static int replyCode;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(HotelList.class, mess -> {
                    ArrayList<Hotel> hotels = mess.getHotelList();
                    for (Hotel hotel : hotels) {
                        System.out.println("Hotel " + hotel.getName() + " (sent by broker " + getSender() + ")");
                    }
                })

                .match(Reply.class, mess -> {
                    replyCode = mess.getCode();
                    System.out.println(mess.getCode()+ " /Answered by " + getSender() + " /Message: " + mess.getMessage());
                })
                .build();
    }

    public ClientActor() {
    }


    public static Props props() {

        return Props.create(ClientActor.class, () -> new ClientActor());
    }
}
