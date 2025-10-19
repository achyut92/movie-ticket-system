package com.test.cinema.service;

import com.cinema.model.Booking;
import com.cinema.model.Cinema;
import com.cinema.model.Seat;
import com.cinema.service.BookingService;
import com.cinema.store.BookingStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingServiceTest {

    Cinema cinema;
    BookingStore bookingStore;
    BookingService bookingService;

    @BeforeEach
    void setup() {
        int rows = 8;
        int seatsPerRow = 10;
        cinema = new Cinema("Test", rows, seatsPerRow);
        bookingStore = new BookingStore(new ArrayList<>());
        bookingService = new BookingService(cinema, bookingStore);
    }

    @Test
    void testGetAvailableSeats() {
        int seats = bookingService.getAvailableSeats();
        assertEquals(80, seats);
    }

    @Test
    void testCanBookSuccess() {
        boolean canBook = bookingService.canBook(80);
        assertTrue(canBook);
    }

    @Test
    void testCanBookFail() {
        boolean canBook = bookingService.canBook(81);
        assertFalse(canBook);
    }

    @Test
    void testCanBookNegativeFail() {
        boolean canBook = bookingService.canBook(-1);
        assertFalse(canBook);
    }

    @Test
    void testGetAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        assertEquals(0, bookings.size());
    }

    @Test
    void testDefaultSeatAllocation() {
        List<Seat> defaultSeats = bookingService.allocateDefaultSeats(5);
        boolean[][] seating = cinema.getSeating();
        assertEquals(5, defaultSeats.size());
        assertTrue(seating[7][2]);
        assertTrue(seating[7][6]);
        assertFalse(seating[7][1]);
        assertFalse(seating[7][7]);
    }

    @Test
    void testDefaultSeatAllocationConfirm() {
        List<Seat> defaultSeats = bookingService.allocateDefaultSeats(5);
        String bookingId = bookingService.confirmBooking(defaultSeats);
        assertEquals("GIC0001", bookingId);
        List<Booking> bookings = bookingService.getAllBookings();
        assertEquals(1, bookings.size());
        assertEquals(5, bookings.get(0).getAllocatedSeats().size());
    }

    @Test
    void testCheckBookingByValidId() {
        List<Seat> defaultSeats = bookingService.allocateDefaultSeats(5);
        String bookingId = bookingService.confirmBooking(defaultSeats);
        assertEquals("GIC0001", bookingId);
        String confirmId = bookingService.checkBookings(bookingId);
        assertEquals(bookingId, confirmId);
    }

    @Test
    void testCheckBookingByInvalidId() {
        List<Seat> defaultSeats = bookingService.allocateDefaultSeats(5);
        String bookingId = bookingService.confirmBooking(defaultSeats);
        assertEquals("GIC0001", bookingId);
        String confirmId = bookingService.checkBookings("GIC0033");
        assertNull(confirmId);
    }

    @Test
    void testManualSeatAllocation() {
        List<Seat> defaultSeats = bookingService.allocateDefaultSeats(5);
        Seat newSeat = new Seat(6, 2);
        List<Seat> newSeats = bookingService.allocatedSeatsFromPosition(newSeat, 5, defaultSeats);
        boolean[][] seating = cinema.getSeating();
        assertEquals(defaultSeats.size(), newSeats.size());
        assertTrue(seating[6][2]);
        assertTrue(seating[6][6]);
        assertFalse(seating[6][1]);
        assertFalse(seating[6][7]);
    }

    @Test
    void testManualSeatAllocationConfirm() {
        List<Seat> defaultSeats = bookingService.allocateDefaultSeats(5);
        Seat newSeat = new Seat(6, 2);
        List<Seat> newSeats = bookingService.allocatedSeatsFromPosition(newSeat, 5, defaultSeats);
        String bookingId = bookingService.confirmBooking(newSeats);
        assertEquals("GIC0001", bookingId);
        List<Booking> bookings = bookingService.getAllBookings();
        assertEquals(1, bookings.size());
        assertEquals(5, bookings.get(0).getAllocatedSeats().size());
        bookings.get(0).getAllocatedSeats().forEach(seat -> assertEquals(6, seat.getRow()));
    }
}
