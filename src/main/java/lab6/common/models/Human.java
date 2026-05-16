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
        if (height != null) {
            return "Human{height=" + height + "}";
        } else {
            return "Human{height=не указан}";
        }
    }
}