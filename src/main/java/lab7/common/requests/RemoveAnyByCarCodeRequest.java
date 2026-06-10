package lab7.common.requests;

import lab7.common.Response;
import lab7.server.managers.CollectionManager;

/**
 * Запрос на удаление любого города с указанным кодом автомобиля.
 * Удаляется первый найденный город текущего пользователя.
 * @author AlMuran
 * @version 1.0
 */
public class RemoveAnyByCarCodeRequest extends AuthenticatedRequest {
    private final int carCode;

    /**
     * Конструктор запроса на удаление по carCode.
     * @param carCode код автомобиля (от 1 до 1000)
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public RemoveAnyByCarCodeRequest(int carCode, String login, String password) {
        super(login, password);
        this.carCode = carCode;
    }

    /** @return код автомобиля */
    public int getCarCode() { return carCode; }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        Long removedId = collectionManager.removeAnyByCarCode(carCode, userId);
        if (removedId != null) {
            return new Response(true, "Удалён город с ID " + removedId, null);
        } else {
            return new Response(false, "Город с таким carCode не найден", null);
        }
    }
}
