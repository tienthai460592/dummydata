package nl.saxion.concurrency.messages;

import akka.actor.ActorRef;

import java.util.ArrayList;

public class CreateReservation {

    ArrayList<String> hotelLists;
    ActorRef clientAddress;
    public CreateReservation(ArrayList<String> hotelLists, ActorRef clientAddress) {
        this.clientAddress = clientAddress;
        this.hotelLists = hotelLists;
    }

    public ArrayList<String> getHotelLists() {
        return hotelLists;
    }

    public ActorRef getClientAddress() {
        return clientAddress;
    }
}
