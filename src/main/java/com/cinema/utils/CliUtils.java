package com.cinema.utils;

import com.cinema.model.Booking;
import com.cinema.model.Cinema;
import com.cinema.model.Seat;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CliUtils {
    private static final Scanner scanner = new Scanner(System.in);

    public static Cinema promptCinemaSetup() {
        System.out.println("Please define movie title and seating map in [Title] [Row] [SeatsPerRow] format:");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;

        String[] parts = input.split(" ");
        if (parts.length != 3) {
            System.out.println("Invalid input format.");
            return null;
        }

        try {
            String movieTitle = parts[0];
            int rows = Integer.parseInt(parts[1]);
            int seatsPerRow = Integer.parseInt(parts[2]);

            if (rows > 26 || seatsPerRow > 50 || rows <= 0 || seatsPerRow <= 0) {
                System.out.println("Invalid dimensions. Max rows: 26, max seats per row: 50.");
                return null;
            }

            /*boolean[][] seatingArr = new boolean[rows][seatsPerRow];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < seatsPerRow; j++) {
                    seatingArr[i][j] = false;
                }
            }*/
            return new Cinema(movieTitle, rows, seatsPerRow);
        } catch (NumberFormatException e) {
            System.out.println("Invalid numbers. Please try again.");
            return null;
        }
    }

    public static void displayMainMenu(String movieTitle, int seatsAvailable) {
        System.out.println("\nWelcome to GIC Cinemas");
        System.out.println("[1] Book tickets for " + movieTitle + " (" + seatsAvailable + " seats available)");
        System.out.println("[2] Check bookings");
        System.out.println("[3] Exit");
    }

    public static int promptMenuSelection() {
        System.out.print("Select option: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static int promptTicketCount() {
        System.out.print("Enter number of tickets to book or enter blank to go back to main menu:\n>");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return -1;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Try again.");
            return promptTicketCount();
        }
    }

    public static Seat promptSeatSelection(int numOfRows) {
        System.out.print("Enter blank to accept seat selection or enter new seating position:\n>");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        return parseSeat(input, numOfRows);
    }

    public static String promptBookingId() {
        System.out.print("Enter booking ID, or enter blank to go back to main menu: \n>");
        return scanner.nextLine().trim();
    }

    public static boolean display(Cinema cinema, List<Booking> bookings, String bookingId) {

        List<Seat> bookedSeats = bookings.stream()
                .filter(booking -> !booking.getId().equalsIgnoreCase(bookingId))
                .flatMap(booking -> booking.getAllocatedSeats().stream())
                .collect(Collectors.toList());

        int rows = cinema.getNumOfRows();
        int cols = cinema.getSeatsPerRow();
        boolean[][] seats = cinema.getSeating();

        System.out.println("Selected Seats:");
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

        if (!seat.matches("^[A-Za-z]\\d+$")) {
            System.out.println("Invalid seat format: " + seat);
            return null;
        }

        char rowChar = seat.charAt(0);
        String columnPart = seat.substring(1);

        int row = (numOfRows - 1) - (Character.toUpperCase(rowChar) - 'A');
        int column = Integer.parseInt(columnPart) - 1;

        return new Seat(row, column);
    }
}
