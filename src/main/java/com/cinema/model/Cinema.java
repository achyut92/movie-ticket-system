package com.cinema.model;

public class Cinema {
    private String title;
    private int numOfRows;
    private int seatsPerRow;
    private boolean[][] seating;

    public Cinema(String title, int numOfRows, int seatsPerRow, boolean[][] seating) {
        this.title = title;
        this.numOfRows = numOfRows;
        this.seatsPerRow = seatsPerRow;
        this.seating = seating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public void setSeatsPerRow(int seatsPerRow) {
        this.seatsPerRow = seatsPerRow;
    }

    public boolean[][] getSeating() {
        return seating;
    }

    public void setSeating(boolean[][] seating) {
        this.seating = seating;
    }

    public int getAvailableSeats() {
        int count = 0;
        for (boolean[] row : seating)
            for (boolean seat : row)
                if (!seat) count++;
        return count;
    }

    public boolean isSeatOccupied(int row, int col) {
        return this.seating[row][col];
    }

    public void setSeatOccupied(int row, int col) {
        this.seating[row][col] = true;
    }

    public void setSeatUnoccupied(int row, int col) {
        this.seating[row][col] = false;
    }
}