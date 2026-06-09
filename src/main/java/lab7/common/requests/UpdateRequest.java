package lab7.common.requests;

import lab7.common.models.City;

/**
 * Запрос на обновление существующего города в коллекции.
 *
 * <p>Содержит идентификатор города, который нужно обновить,
 * и новый объект {@link City} с обновлёнными данными.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
 * @see lab7.server.CommandExecutor
 */
public class UpdateRequest extends AuthenticatedRequest {

    /** Идентификатор города, который нужно обновить */
    private final long id;

    /** Новые данные города */
    private final City city;

    /**
     * Создаёт запрос на обновление города.
     *
     * @param id идентификатор существующего города
     * @param city новый объект города с обновлёнными данными
     */
    public UpdateRequest(long id, City city, String login, String password) {
        super(login, password);
        this.id = id;
        this.city = city;
    }

    /**
     * Возвращает идентификатор города для обновления.
     *
     * @return id города
     */
    public long getId() {
        return id;
    }

    /**
     * Возвращает новые данные города.
     *
     * @return объект {@link City} с обновлёнными данными
     */
    public City getCity() {
        return city;
    }
}