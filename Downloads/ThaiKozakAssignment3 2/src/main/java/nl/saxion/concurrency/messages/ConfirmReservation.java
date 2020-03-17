package nl.saxion.concurrency.messages;

import akka.actor.ActorRef;

public class ConfirmReservation {
    private String hotelName;
    private int code;
    private ActorRef clientAddress;

    public ConfirmReservation(String hotelName, int code, ActorRef clientAddress) {
        this.hotelName = hotelName;
        this.code = code;
        this.clientAddress = clientAddress;
    }

    public String getHotelName() {
        return hotelName;
    }

    public int getCode() {
        return code;
    }

    public ActorRef getClientAddress() {
        return clientAddress;
    }
}
