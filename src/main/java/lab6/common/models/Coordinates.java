package lab6.common.models;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private Long x;
    private double y;

    public Coordinates(Long x, double y) {
        this.x = x;
        this.y = y;
    }

    public Long getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}