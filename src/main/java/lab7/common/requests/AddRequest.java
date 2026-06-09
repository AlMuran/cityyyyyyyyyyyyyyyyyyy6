package lab7.common.requests;

import lab7.common.models.City;

/**
 * Запрос на добавление нового города в коллекцию.
 *
 * <p>Содержит объект {@link City}, который клиент хочет добавить.
 * Сервер самостоятельно назначит id и дату создания.</p>
 *
 * @author AlMuarn
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
 * @see lab7.server.CommandExecutor
 */
public class AddRequest extends AuthenticatedRequest {

    /** Город для добавления в коллекцию */
    private final City city;

    /**
     * Создаёт запрос на добавление города.
     *
     * @param city город, который нужно добавить (не может быть null)
     */
    public AddRequest(City city, String login, String password) {
        super(login, password);
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