package lab7.common.requests;

/**
 * Запрос на удаление любого города с указанным кодом автомобиля.
 *
 * <p>Содержит код автомобиля (carCode). Если в коллекции есть хотя бы один
 * город с таким кодом, будет удалён один (любой) из них.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
  */
public class RemoveAnyByCarCodeRequest extends AuthenticatedRequest {

    /** Код автомобиля (должен быть в диапазоне 1-1000) */
    private final int carCode;

    /**
     * Создаёт запрос на удаление города по carCode.
     *
     * @param carCode код автомобиля для поиска
     */
    public RemoveAnyByCarCodeRequest(int carCode, String login, String password) {
        super(login, password);
        this.carCode = carCode;
    }

    /**
     * Возвращает код автомобиля для поиска.
     *
     * @return код автомобиля
     */
    public int getCarCode() {
        return carCode;
    }
}