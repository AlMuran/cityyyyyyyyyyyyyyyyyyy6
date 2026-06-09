package lab7.common.models;

import java.io.Serializable;

/**
 * Перечисление возможных уровней жизни в городе.
 *
 * <p>Доступные значения в порядке убывания уровня жизни:
 * <ul>
 *   <li>{@link #ULTRA_HIGH} - очень высокий уровень жизни</li>
 *   <li>{@link #HIGH} - высокий уровень жизни</li>
 *   <li>{@link #MEDIUM} - средний уровень жизни</li>
 *   <li>{@link #VERY_LOW} - очень низкий уровень жизни</li>
 *   <li>{@link #ULTRA_LOW} - крайне низкий уровень жизни</li>
 * </ul>
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see City
 */
public enum StandardOfLiving implements Serializable {
    /** Очень высокий уровень жизни */
    ULTRA_HIGH,

    /** Высокий уровень жизни */
    HIGH,

    /** Средний уровень жизни */
    MEDIUM,

    /** Очень низкий уровень жизни */
    VERY_LOW,

    /** Крайне низкий уровень жизни */
    ULTRA_LOW
}