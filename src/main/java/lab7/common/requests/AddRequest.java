package lab7.common.requests;

import lab7.common.Response;
import lab7.common.models.City;
import lab7.server.managers.CollectionManager;

/**
 * Запрос на добавление нового города в коллекцию.
 * Город будет добавлен с автоматической генерацией ID и даты создания.
 * @author AlMuran
 * @version 1.0
 */
public class AddRequest extends AuthenticatedRequest {
    private final City city;

    /**
     * Конструктор запроса на добавление.
     * @param city город для добавления
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public AddRequest(City city, String login, String password) {
        super(login, password);
        this.city = city;
    }

    /** @return город для добавления */
    public City getCity() { return city; }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        try {
            collectionManager.addCity(city, userId);
            return new Response(true, "Город добавлен с ID: " + city.getId(), null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
}