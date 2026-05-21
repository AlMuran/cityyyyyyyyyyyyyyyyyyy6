package lab6.common.requests;

import lab6.common.models.City;

/**
 * Запрос на удаление всех городов, меньших заданного эталона.
 *
 * <p>Содержит эталонный город {@link City}, с которым сравниваются
 * все города в коллекции. Города, меньшие эталона (согласно методу
 * {@link City#compareTo(City)}), будут удалены.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
 * @see lab6.server.CommandExecutor
 */
public class RemoveLowerRequest implements CommandRequest {

    /** Эталонный город для сравнения */
    private final City reference;

    /**
     * Создаёт запрос на удаление меньших городов.
     *
     * @param reference эталонный город (города меньше этого будут удалены)
     */
    public RemoveLowerRequest(City reference) {
        this.reference = reference;
    }

    /**
     * Возвращает эталонный город.
     *
     * @return объект {@link City} для сравнения
     */
    public City getReference() {
        return reference;
    }
}