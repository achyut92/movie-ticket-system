package com.cinema.store;

import com.cinema.model.Booking;

import java.util.List;

public class BookingStore {
    List<Booking> bookings;

    public BookingStore(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void save(Booking booking) {
        this.bookings.add(booking);
    }
}
