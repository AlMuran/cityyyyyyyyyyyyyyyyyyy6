package lab7.common.requests;

import lab7.common.Response;
import lab7.server.managers.CollectionManager;

/**
 * Запрос на получение информации о коллекции.
 * Возвращает тип коллекции, дату инициализации, общее количество элементов
 * и количество элементов текущего пользователя.
 * @author AlMuran
 * @version 1.0
 */
public class InfoRequest extends AuthenticatedRequest {

    /**
     * Конструктор запроса информации.
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public InfoRequest(String login, String password) {
        super(login, password);
    }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        String info = String.format(
                "Тип: %s\nДата инициализации: %s\nВсего элементов: %d\nВаших элементов: %d",
                collectionManager.getCollectionType(),
                collectionManager.getInitializationDate(),
                collectionManager.size(),
                collectionManager.getUserCitiesCount(userId)
        );
        return new Response(true, info, null);
    }
}