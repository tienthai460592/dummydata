package nl.saxion.concurrency.messages;

import akka.actor.ActorRef;

public class GetHotelList {
    private ActorRef hotel;
    private String name;

    public GetHotelList(ActorRef ref, String name) {
        this.hotel = ref;
        this.name = name;
    }

    public ActorRef getRef() {
        return hotel;
    }

    public GetHotelList() {
    }

    public String getName() {
        return name;
    }

}
