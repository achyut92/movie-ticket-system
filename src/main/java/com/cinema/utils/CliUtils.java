package com.cinema.utils;

import com.cinema.model.Booking;
import com.cinema.model.Cinema;
import com.cinema.model.Seat;

import java.util.List;
import java.util.stream.Collectors;

public class CliUtils {
    public static void displayMainMenu(String movieTitle, int seatsAvailable) {
        System.out.println("\nWelcome to GIC Cinemas");
        System.out.println("[1] Book tickets for " + movieTitle + " (" + seatsAvailable + " seats available)");
        System.out.println("[2] Check bookings");
        System.out.println("[3] Exit");
    }

    public static boolean display(Cinema cinema, List<Booking> bookings, String bookingId) {

        List<Seat> bookedSeats = bookings.stream()
                .filter(booking -> !booking.getId().equalsIgnoreCase(bookingId))
                .flatMap(booking -> booking.getAllocatedSeats().stream())
                .collect(Collectors.toList());

        int rows = cinema.getNumOfRows();
        int cols = cinema.getSeatsPerRow();
        boolean[][] seats = cinema.getSeating();

        System.out.println(String.format("%" + cols + "s", "SCREEN"));
        System.out.print(" ");
        for (int i = 0; i < cols + 2; i++) System.out.print("-");
        System.out.println();

        for (int r = 0; r < rows; r++) {
            System.out.print((char) ('A' + (rows-1-r)) + " ");
            for (int c = 0; c < cols; c++) {
                boolean seatValue = seats[r][c];
                boolean isBooked = isSeatBooked(bookedSeats, r, c);
                if (!isBooked && seatValue) {
                    System.out.print("o");
                } else if (isBooked && seatValue) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
        System.out.print("  ");
        for (int c = 1; c <= cols; c++) System.out.print(c);
        System.out.println();
        return true;
    }

    private static boolean isSeatBooked(List<Seat> seats, int row, int col) {
        return seats.stream()
                .anyMatch(seat -> seat.getRow() == row && seat.getCol() == col);
    }

    public static Seat parseSeat(String seat, int numOfRows) {
        // Validate pattern: one letter followed by one or more digits
        if (!seat.matches("^[A-Za-z]\\d+$")) {
            System.out.println("Invalid seat format: " + seat);
            return null;
        }

        // Extract parts
        char rowChar = seat.charAt(0);
        String columnPart = seat.substring(1);

        // Convert row: 'A'→n-1, 'B'→n-2 ...
        int row = (numOfRows - 1) - (Character.toUpperCase(rowChar) - 'A');

        // Convert column: convert to int and adjust for 0-based index
        int column = Integer.parseInt(columnPart) - 1;

        return new Seat(row, column);
    }
}
