package nl.saxion.concurrency.messages;

import akka.actor.ActorRef;

public class CancelReservation {
    private String hotelName;
    private int reservationCode;
    ActorRef clientAddress;

    public CancelReservation(String hotelName, int reservationCode, ActorRef clientAddress) {
        this.hotelName = hotelName;
        this.reservationCode = reservationCode;
        this.clientAddress = clientAddress;
    }

    public String getHotelName() {
        return hotelName;
    }


    public int getReservationCode() {
        return reservationCode;
    }

    public ActorRef getClientAddress() {
        return clientAddress;
    }
}
