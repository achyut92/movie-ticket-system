package com.cinema.service;

import com.cinema.store.BookingStore;
import com.cinema.model.Booking;
import com.cinema.model.Cinema;
import com.cinema.model.Seat;
import com.cinema.utils.CliUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingService {

    private final Cinema cinema;
    private final BookingStore bookingStore;

    public BookingService(Cinema cinema, BookingStore bookingStore) {
        this.cinema = cinema;
        this.bookingStore = bookingStore;
    }

    public boolean canBook(int ticketCount) {
        int available = cinema.getAvailableSeats();
        return ticketCount > 0 && ticketCount <= available;
    }

    public int getAvailableSeats() {
        return cinema.getAvailableSeats();
    }

    public List<Booking> getAllBookings() {
        return bookingStore.getBookings();
    }

    public List<Seat> allocateDefaultSeats(int numTickets) {

            List<Seat> allocatedSeats = new ArrayList<>(numTickets);
            int rows = cinema.getNumOfRows();

            int remainderSeatsToAllocate = numTickets;

            for (int currentRow = rows - 1 ; currentRow >= 0 && remainderSeatsToAllocate > 0; currentRow--)
            {
                int before = allocatedSeats.size();
                fillRowFromMiddle(currentRow, remainderSeatsToAllocate, allocatedSeats);
                remainderSeatsToAllocate -= (allocatedSeats.size() - before);
            }
        return allocatedSeats;
    }

    private void deallocateDefaultSeats(List<Seat> allocatedSeats) {
        allocatedSeats.forEach(seat -> {
            cinema.setSeatUnoccupied(seat.getRow(), seat.getCol());
        });
    }

    public List<Seat> allocatedSeatsFromPosition(Seat newSeatPosition, int numTickets, List<Seat> allocatedSeats) {

        deallocateDefaultSeats(allocatedSeats);
        allocatedSeats = new ArrayList<>(numTickets);
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
        return allocatedSeats;
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

    public String confirmBooking(List<Seat> allocatedSeats) {
        Booking booking = new Booking();
        booking.setId(String.format("GIC%04d", bookingStore.getBookings().size()+1));
        booking.setAllocatedSeats(allocatedSeats);
        bookingStore.save(booking);
        return booking.getId();
    }

    public String checkBookings(String id) {
        List<Booking> bookings = getAllBookings();
        Optional<Booking> booking = bookings.stream()
                .filter(b -> b.getId().equalsIgnoreCase(id))
                .findFirst();
        return booking.isPresent() ? booking.get().getId() : null;
    }
}
