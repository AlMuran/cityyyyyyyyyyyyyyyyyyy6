package lab6.common.requests;

import lab6.common.models.City;

/**
 * Запрос на добавление города, если он превышает максимальный в коллекции.
 *
 * <p>Содержит город, который нужно добавить. Город будет добавлен только
 * в том случае, если он больше всех существующих городов в коллекции
 * (согласно методу {@link City#compareTo(City)}).</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
 * @see lab6.server.CommandExecutor#
 */
public class AddIfMaxRequest implements CommandRequest {

    /** Город для добавления (если он максимальный) */
    private final City city;

    /**
     * Создаёт запрос на условное добавление города.
     *
     * @param city город, который нужно добавить при выполнении условия
     */
    public AddIfMaxRequest(City city) {
        this.city = city;
    }

    /**
     * Возвращает город для добавления.
     *
     * @return объект {@link City}
     */
    public City getCity() {
        return city;
    }
}