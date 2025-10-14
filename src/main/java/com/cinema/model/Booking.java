package com.cinema.model;

import java.util.List;

public class Booking {
    private String id;
    private List<Seat> allocatedSeats;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Seat> getAllocatedSeats() {
        return allocatedSeats;
    }

    public void setAllocatedSeats(List<Seat> allocatedSeats) {
        this.allocatedSeats = allocatedSeats;
    }
}
