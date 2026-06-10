package lab7.common.requests;

import lab7.common.Response;
import lab7.common.models.City;
import lab7.server.managers.CollectionManager;

/**
 * Запрос на добавление города, если он больше максимального города в коллекции.
 * Сравнение происходит только среди городов текущего пользователя.
 * @author AlMuran
 * @version 1.0
 */
public class AddIfMaxRequest extends AuthenticatedRequest {
    private final City city;

    /**
     * Конструктор запроса на добавление с условием.
     * @param city город для добавления
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public AddIfMaxRequest(City city, String login, String password) {
        super(login, password);
        this.city = city;
    }

    /** @return город для добавления */
    public City getCity() { return city; }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        try {
            boolean added = collectionManager.addIfMax(city, userId);
            if (added) {
                return new Response(true, "Город добавлен как максимальный с ID: " + city.getId(), null);
            } else {
                return new Response(false, "Город не является максимальным", null);
            }
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
}