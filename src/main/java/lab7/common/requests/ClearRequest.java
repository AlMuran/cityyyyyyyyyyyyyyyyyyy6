package lab7.common.requests;

import lab7.common.Response;
import lab7.server.managers.CollectionManager;

/**
 * Запрос на удаление всех городов, принадлежащих текущему пользователю.
 * Города других пользователей остаются нетронутыми.
 * @author AlMuran
 * @version 1.0
 */
public class ClearRequest extends AuthenticatedRequest {

    /**
     * Конструктор запроса на очистку.
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public ClearRequest(String login, String password) {
        super(login, password);
    }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        int count = collectionManager.clearUserCities(userId);
        return new Response(true, "Удалено ваших городов: " + count, null);
    }
}
