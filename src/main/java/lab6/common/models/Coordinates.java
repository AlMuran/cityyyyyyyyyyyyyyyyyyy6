package lab6.common.models;

import java.io.Serializable;

/**
 * Представляет координаты города на карте.
 *
 * <p>Координаты состоят из двух компонентов:
 * <ul>
 *   <li><strong>x</strong> - целочисленная координата (должна быть > 0)</li>
 *   <li><strong>y</strong> - вещественная координата (максимальное значение 793)</li>
 * </ul>
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see City
 */
public class Coordinates implements Serializable {

    /** Координата X. Должна быть > 0. */
    private Long x;

    /** Координата Y. Не должна превышать 793. */
    private double y;

    /**
     * Создаёт новый объект координат.
     *
     * @param x координата X (должна быть > 0)
     * @param y координата Y (максимальное значение 793)
     */
    public Coordinates(Long x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Возвращает координату X.
     *
     * @return значение координаты X
     */
    public Long getX() {
        return x;
    }

    /**
     * Возвращает координату Y.
     *
     * @return значение координаты Y
     */
    public double getY() {
        return y;
    }

    /**
     * Возвращает строковое представление координат.
     *
     * @return строка в формате "Coordinates{x=..., y=...}"
     */
    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}