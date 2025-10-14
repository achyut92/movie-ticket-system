package com.cinema;

import com.cinema.model.Cinema;
import com.cinema.service.BookingService;
import com.cinema.store.BookingStore;
import com.cinema.utils.CliUtils;

import java.util.ArrayList;
import java.util.Scanner;

public class TicketingSystem {
    private static final Scanner scanner = new Scanner(System.in);

    void startTicketingSystem() {
        System.out.println("Please define movie title and seating map in [Title] [Row] [SeatsPerRow] format:");
        String input = scanner.nextLine();
        String[] parts = input.split(" ");
        Cinema cinema = null;
        if (parts.length == 3) {
            String movieTitle = parts[0];
            int rows = Integer.parseInt(parts[1]);
            int seatsPerRow = Integer.parseInt(parts[2]);
            if (rows > 26 || seatsPerRow > 50) {
                System.out.println("Exceeded maximum limits: 26 rows and 50 seats per rows.");
            }
            boolean[][] seatingArr = new boolean[rows][seatsPerRow];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < seatsPerRow; j++) {
                    seatingArr[i][j] = false;
                }
            }
            cinema = new Cinema(movieTitle, rows, seatsPerRow, seatingArr);
        }

        if (cinema != null) {
            BookingStore bookingStore = new BookingStore(new ArrayList<>());
            BookingService bookingService = new BookingService(cinema, bookingStore);
            while (true) {
                CliUtils.displayMainMenu(cinema.getTitle(), cinema.getAvailableSeats());
                Scanner scanner = new Scanner(System.in);
                String selectedOption = scanner.nextLine();
                switch (selectedOption) {
                    case "1":
                        bookingService.allocateDefaultSeats();
                        break;
                    case "2":
                        bookingService.checkBookings();
                        break;
                    case "3":
                        System.out.println("Thank you for using the Cinema system. Bye!");
                        return;
                    default:
                        System.out.println("Invalid selection. Please try again.");
                }
            }
        }
    }

    public static void main(String[] args) {
        TicketingSystem ticketingSystem = new TicketingSystem();
        ticketingSystem.startTicketingSystem();
    }
}

