package com.cinema.model;

public class Seat {
    private int row;
    private int col;

    public Seat(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
