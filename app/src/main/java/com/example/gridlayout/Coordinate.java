package com.example.gridlayout;

public class Coordinate {
    public int row;
    public int col;

    // Default constructor
    public Coordinate()
    {
        row=-1;
        col=-1;
    }

    public Coordinate(int x, int y){
        row = x;
        col = y;
    }
}
