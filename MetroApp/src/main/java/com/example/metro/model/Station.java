package com.example.metro.model;

public class Station {
    public int id;
    public String name;
    public int line_id;
    public double pos_x;
    public double pos_y;

    public Station() {
    }

    public Station(int id, String name, int line_id, double pos_x, double pos_y) {
        this.id = id;
        this.name = name;
        this.line_id = line_id;
        this.pos_x = pos_x;
        this.pos_y = pos_y;
    }
}
