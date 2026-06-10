package lab7.common.requests;

import lab7.common.Response;
import lab7.common.models.City;
import lab7.server.managers.CollectionManager;
import java.util.Optional;

/**
 * Запрос на поиск города с минимальной координатой X.
 * Возвращает город среди всех пользователей.
 * @author AlMuran
 * @version 1.0
 */
public class MinByCoordinatesRequest extends AuthenticatedRequest {

    /**
     * Конструктор запроса минимальной координаты.
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public MinByCoordinatesRequest(String login, String password) {
        super(login, password);
    }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        Optional<City> city = collectionManager.getMinByCoordinates();
        if (city.isPresent()) {
            return new Response(true, "Город с минимальной координатой X:", city.get());
        } else {
            return new Response(false, "Коллекция пуста", null);
        }
    }
}