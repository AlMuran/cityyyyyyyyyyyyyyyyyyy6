package lab6.common.models;

import java.io.Serializable;

/**
 * Представляет губернатора города.
 *
 * <p>Содержит информацию о росте губернатора. Рост является опциональным полем
 * (может быть не указан).</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see City
 */
public class Human implements Serializable {

    /** Рост губернатора в сантиметрах. Может быть null, если не указан. */
    private Float height;

    /**
     * Создаёт нового губернатора с указанным ростом.
     *
     * @param height рост в сантиметрах (может быть null, если рост не указан)
     */
    public Human(Float height) {
        this.height = height;
    }

    /**
     * Возвращает рост губернатора.
     *
     * @return рост в сантиметрах, или null если не указан
     */
    public Float getHeight() {
        return height;
    }

    /**
     * Устанавливает рост губернатора.
     *
     * @param height новый рост в сантиметрах (может быть null)
     */
    public void setHeight(Float height) {
        this.height = height;
    }

    /**
     * Возвращает строковое представление губернатора.
     *
     * @return строка с информацией о росте (или "не указан", если рост null)
     */
    @Override
    public String toString() {
        return "Human{" +
                "height=" + (height != null ? height : "не указан") +
                '}';
    }
}