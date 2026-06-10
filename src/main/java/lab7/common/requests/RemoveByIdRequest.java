package lab7.common.requests;

import lab7.common.Response;
import lab7.server.managers.CollectionManager;

/**
 * Запрос на удаление города по идентификатору.
 * Город может удалить только его создатель.
 * @author AlMuran
 * @version 1.0
 */
public class RemoveByIdRequest extends AuthenticatedRequest {
    private final long id;

    /**
     * Конструктор запроса на удаление.
     * @param id идентификатор удаляемого города
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public RemoveByIdRequest(long id, String login, String password) {
        super(login, password);
        this.id = id;
    }

    /** @return идентификатор удаляемого города */
    public long getId() { return id; }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        try {
            collectionManager.removeCity(id, userId);
            return new Response(true, "Город с ID " + id + " удалён", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
}