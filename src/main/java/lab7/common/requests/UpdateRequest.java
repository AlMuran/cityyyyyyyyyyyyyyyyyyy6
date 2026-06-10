package lab7.common.requests;

import lab7.common.Response;
import lab7.common.models.City;
import lab7.server.managers.CollectionManager;

/**
 * Запрос на обновление существующего города.
 * Город может обновить только его создатель.
 * @author AlMuran
 * @version 1.0
 */
public class UpdateRequest extends AuthenticatedRequest {
    private final long id;
    private final City city;

    /**
     * Конструктор запроса на обновление.
     * @param id идентификатор обновляемого города
     * @param city новые данные города
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public UpdateRequest(long id, City city, String login, String password) {
        super(login, password);
        this.id = id;
        this.city = city;
    }

    /** @return идентификатор обновляемого города */
    public long getId() { return id; }

    /** @return новые данные города */
    public City getCity() { return city; }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        try {
            city.setId(id);
            collectionManager.updateCity(city, userId);
            return new Response(true, "Город с ID " + id + " обновлён", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
}