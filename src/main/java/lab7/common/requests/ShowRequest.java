package lab7.common.requests;

import lab7.common.Response;
import lab7.server.managers.CollectionManager;
import java.util.Collection;

/**
 * Запрос на получение списка всех городов в коллекции.
 * Показывает города всех пользователей.
 * @author AlMuran
 * @version 1.0
 */
public class ShowRequest extends AuthenticatedRequest {

    /**
     * Конструктор запроса на показ.
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public ShowRequest(String login, String password) {
        super(login, password);
    }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        Collection<?> cities = collectionManager.getAllCities();
        if (cities.isEmpty()) {
            return new Response(true, "Коллекция пуста", null);
        }
        return new Response(true, "Список городов:", cities);
    }
}