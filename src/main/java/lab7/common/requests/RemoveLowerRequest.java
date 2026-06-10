package lab7.common.requests;

import lab7.common.Response;
import lab7.common.models.City;
import lab7.server.managers.CollectionManager;

/**
 * Запрос на удаление всех городов, которые меньше заданного эталона.
 * Удаляются только города текущего пользователя.
 * @author AlMuran
 * @version 1.0
 */
public class RemoveLowerRequest extends AuthenticatedRequest {
    private final City reference;

    /**
     * Конструктор запроса на удаление меньших.
     * @param reference эталонный город для сравнения
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public RemoveLowerRequest(City reference, String login, String password) {
        super(login, password);
        this.reference = reference;
    }

    /** @return эталонный город */
    public City getReference() { return reference; }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        int removed = collectionManager.removeLowerThan(reference, userId);
        return new Response(true, "Удалено городов: " + removed, null);
    }
}