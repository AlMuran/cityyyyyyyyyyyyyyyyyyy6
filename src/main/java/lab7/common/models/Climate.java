package lab7.common.models;

import java.io.Serializable;

/**
 * Перечисление возможных типов климата для города.
 *
 * <p>Доступные значения:
 * <ul>
 *   <li>{@link #RAIN_FOREST} - тропический лес</li>
 *   <li>{@link #MEDITERRANIAN} - средиземноморский климат</li>
 *   <li>{@link #TUNDRA} - тундра</li>
 * </ul>
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see City
 */
public enum Climate implements Serializable {
    /** Тропический лес (влажный и жаркий климат) */
    RAIN_FOREST,

    /** Средиземноморский климат (мягкая зима, тёплое лето) */
    MEDITERRANIAN,

    /** Тундра (холодный климат с вечной мерзлотой) */
    TUNDRA
}