package lab6.common.requests;

/**
 * Запрос на удаление города из коллекции по его идентификатору.
 *
 * <p>Содержит только идентификатор города, который нужно удалить.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
 * @see lab6.server.CommandExecutor
 */
public class RemoveByIdRequest implements CommandRequest {

    /** Идентификатор города для удаления */
    private final long id;

    /**
     * Создаёт запрос на удаление города по id.
     *
     * @param id идентификатор города, который нужно удалить
     */
    public RemoveByIdRequest(long id) {
        this.id = id;
    }

    /**
     * Возвращает идентификатор города для удаления.
     *
     * @return id города
     */
    public long getId() {
        return id;
    }
}