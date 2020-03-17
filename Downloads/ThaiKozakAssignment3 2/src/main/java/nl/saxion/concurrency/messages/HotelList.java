package nl.saxion.concurrency.messages;
import nl.saxion.concurrency.Hotel;

import java.util.ArrayList;

public class HotelList {
    private ArrayList<Hotel> hotelList;

    public HotelList(ArrayList<Hotel> hotelList) {
        this.hotelList = hotelList;
    }

    public ArrayList getHotelList() {
        return hotelList;
    }
}
