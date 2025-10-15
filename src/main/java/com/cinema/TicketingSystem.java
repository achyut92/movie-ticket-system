package com.cinema;

import com.cinema.model.Cinema;
import com.cinema.model.Seat;
import com.cinema.service.BookingService;
import com.cinema.store.BookingStore;
import com.cinema.utils.CliUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TicketingSystem {
    private BookingService bookingService;
    private Cinema cinema;
    void startTicketingSystem() {
        cinema = CliUtils.promptCinemaSetup();
        if (cinema == null) {
            System.out.println("Cinema setup cancelled. Exiting...");
            return;
        }

        BookingStore bookingStore = new BookingStore(new ArrayList<>());
        bookingService = new BookingService(cinema, bookingStore);
        boolean running = true;
        while (running) {
            int seatsAvailable = cinema.getAvailableSeats();
            CliUtils.displayMainMenu(cinema.getTitle(), seatsAvailable);
            int option = CliUtils.promptMenuSelection();
            switch (option) {
                case 1 -> handleBookingFlow();
                case 2 -> handleCheckBookings();
                case 3 -> {
                    System.out.println("Thank you for using GIC Cinemas. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleCheckBookings() {
        String id = CliUtils.promptBookingId();
        String bookingId = bookingService.checkBookings(id);
        if (Objects.nonNull(bookingId)) {
            System.out.println("Booking ID: " + bookingId);
            CliUtils.display(cinema, bookingService.getAllBookings(), id);
        } else {
            System.out.println("No bookings found for the given ID");
        }
    }

    private void handleBookingFlow() {
        int ticketCount = CliUtils.promptTicketCount();
        if (ticketCount <= 0) {
            return;
        }

        if (!bookingService.canBook(ticketCount)) {
            System.out.println(String.format("Sorry, there are only %d seats available.", bookingService.getAvailableSeats()));
            return;
        }

        List<Seat> allocatedSeats = bookingService.allocateDefaultSeats(ticketCount);
        CliUtils.display(cinema, bookingService.getAllBookings(), "");
        List<Seat> newSeats = allocatedSeats;
        String bookingId = null;
        while (Objects.isNull(bookingId)) {
            Seat seat = CliUtils.promptSeatSelection(cinema.getNumOfRows());
            if (seat != null) {
                newSeats = bookingService.allocatedSeatsFromPosition(seat, ticketCount, newSeats);
                CliUtils.display(cinema, bookingService.getAllBookings(), "");
                continue;
            }
            bookingId = bookingService.confirmBooking(newSeats);
        }
        System.out.println(String.format("Booking id: %s is confirmed.", bookingId));
        CliUtils.display(cinema, bookingService.getAllBookings(), "");
    }

    public static void main(String[] args) {
        TicketingSystem ticketingSystem = new TicketingSystem();
        ticketingSystem.startTicketingSystem();
    }
}

