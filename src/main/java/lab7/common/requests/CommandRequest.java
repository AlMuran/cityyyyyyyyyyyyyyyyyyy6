package lab7.common.requests;

import lab7.common.Response;
import lab7.server.managers.CollectionManager;
import java.io.Serializable;

/**
 * Интерфейс всех команд.Содержит метод execute для паттерна Команда.
 * @author AlMuran
 * @version 1.0
 */
public interface CommandRequest extends Serializable {

    /**
     * Выполняет команду на сервере.
     * @param collectionManager менеджер коллекции
     * @param userId идентификатор пользователя
     * @return ответ сервера
     */
    Response execute(CollectionManager collectionManager, long userId);
}
