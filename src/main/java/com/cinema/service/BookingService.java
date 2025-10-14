package com.cinema.service;

import com.cinema.store.BookingStore;
import com.cinema.model.Booking;
import com.cinema.model.Cinema;
import com.cinema.model.Seat;
import com.cinema.utils.CliUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BookingService {

    private final Cinema cinema;
    private final Scanner scanner;
    private final BookingStore bookingStore;

    public BookingService(Cinema cinema, BookingStore bookingStore) {
        this.cinema = cinema;
        this.bookingStore = bookingStore;
        this.scanner = new Scanner(System.in);
    }

    public Integer allocateDefaultSeats() {
        while (true) {
            System.out.print("Enter number of tickets to book or enter blank to go back to main menu:\n>");
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                return null;
            }

            int numTickets = Integer.parseInt(input);
            int availableSeats = cinema.getAvailableSeats();
            if (numTickets > availableSeats) {
                System.out.println(String.format("Sorry, there are only %d seats available.", availableSeats));
                continue;
            }

            List<Seat> allocatedSeats = new ArrayList<>(numTickets);
            int rows = cinema.getNumOfRows();

            int remainderSeatsToAllocate = numTickets;

            for (int currentRow = rows - 1 ; currentRow >= 0 && remainderSeatsToAllocate > 0; currentRow--)
            {
                int before = allocatedSeats.size();
                fillRowFromMiddle(currentRow, remainderSeatsToAllocate, allocatedSeats);
                remainderSeatsToAllocate -= (allocatedSeats.size() - before);
            }
            CliUtils.display(cinema, bookingStore.getBookings(), "");

            while (true) {
                System.out.print("Enter blank to accept seat selection or enter new seating position:\n>");
                String seatPosition = scanner.nextLine();
                if (seatPosition.isEmpty()) {
                    confirmBooking(allocatedSeats);
                    break;
                } else {
                    deallocateDefaultSeats(allocatedSeats);
                    allocatedSeats = new ArrayList<>(numTickets);
                    allocatedSeatsFromPosition(seatPosition, numTickets, allocatedSeats);
                }
            }
            CliUtils.display(cinema, bookingStore.getBookings(), "");
        }
    }

    private void deallocateDefaultSeats(List<Seat> allocatedSeats) {
        allocatedSeats.forEach(seat -> {
            cinema.setSeatUnoccupied(seat.getRow(), seat.getCol());
        });
    }

    private void allocatedSeatsFromPosition(String seatPosition, int numTickets, List<Seat> allocatedSeats) {
        int maxRows = cinema.getNumOfRows();
        Seat newSeatPosition = CliUtils.parseSeat(seatPosition, maxRows);
        if (newSeatPosition == null){
            System.out.println("Invalid seat position");
            return;
        }
        int remainderSeatsToAllocate = numTickets;
        int startingRow = newSeatPosition.getRow();
        int startingCol = newSeatPosition.getCol();

        // fill to the right on the starting row
        for (int currentCol = startingCol; currentCol < cinema.getSeatsPerRow() && remainderSeatsToAllocate > 0; currentCol++)
        {
            if (!cinema.isSeatOccupied(startingRow, currentCol))
            {
                int before = allocatedSeats.size();
                allocatedSeats.add(new Seat(startingRow, currentCol));
                cinema.setSeatOccupied(startingRow, currentCol);
                remainderSeatsToAllocate -= (allocatedSeats.size() - before);
            }
        }

        // middle out for subsequent rows
        for (int currentRow = startingRow - 1; currentRow >= 0 && remainderSeatsToAllocate > 0; currentRow--)
        {
            int before = allocatedSeats.size();
            fillRowFromMiddle(currentRow, remainderSeatsToAllocate, allocatedSeats);
            remainderSeatsToAllocate -= (allocatedSeats.size() - before);
        }
    }

    private void fillRowFromMiddle(int row,int remainderSeatsToAllocate, List<Seat> seatsToAllocate) {
        int startingSeatColumnToAllocate = (cinema.getSeatsPerRow() - 1) / 2;
        for (int currentIteration = 0; currentIteration < cinema.getSeatsPerRow() && remainderSeatsToAllocate > 0; currentIteration++) {
            int offset = (currentIteration + 1) / 2;
            int seatCol = startingSeatColumnToAllocate;
            if (currentIteration != 0) {
                if (currentIteration % 2 == 0) {
                    seatCol -= offset;
                } else {
                    seatCol += offset;
                }
            }

            if (!cinema.isSeatOccupied(row, seatCol)) {
                seatsToAllocate.add(new Seat(row, seatCol));
                cinema.setSeatOccupied(row, seatCol);
                remainderSeatsToAllocate--;
            }
        }
    }

    private void confirmBooking(List<Seat> allocatedSeats) {
        Booking booking = new Booking();
        booking.setId(String.format("GIC%04d", bookingStore.getBookings().size()+1));
        booking.setAllocatedSeats(allocatedSeats);
        bookingStore.save(booking);

        System.out.println("Booking id: " + booking.getId() + " is confirmed.");
    }

    public void checkBookings() {
        System.out.print("Enter booking ID, or enter blank to go back to main menu: \n>");
        String id = scanner.next();
        Optional<Booking> booking = bookingStore.getBookings().stream()
                .filter(b -> b.getId().equalsIgnoreCase(id))
                .findFirst();
        if (booking.isPresent()) {
            System.out.println("Booking ID: " + booking.get().getId());
            System.out.println("Selected seats:");
            CliUtils.display(cinema, bookingStore.getBookings(), id);
        } else {
            System.out.println("No bookings found for the given ID");
        }
    }
}
