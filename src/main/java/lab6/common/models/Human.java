package lab6.common.models;

import java.io.Serializable;

public class Human implements Serializable {
    private Float height;

    public Human(Float height) {
        this.height = height;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Human{" +
                "height=" + (height != null ? height : "не указан") +
                '}';
    }
}