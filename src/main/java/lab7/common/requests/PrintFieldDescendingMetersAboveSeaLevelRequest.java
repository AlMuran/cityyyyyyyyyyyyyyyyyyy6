package lab7.common.requests;

import lab7.common.Response;
import lab7.server.managers.CollectionManager;
import java.util.List;

/**
 * Запрос на вывод всех значений высоты над уровнем моря в порядке убывания.
 * Выводит высоты всех городов всех пользователей.
 * @author AlMuran
 * @version 1.0
 */
public class PrintFieldDescendingMetersAboveSeaLevelRequest extends AuthenticatedRequest {

    /**
     * Конструктор запроса высот.
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public PrintFieldDescendingMetersAboveSeaLevelRequest(String login, String password) {
        super(login, password);
    }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        List<Integer> heights = collectionManager.getMetersAboveSeaLevelDescending();
        if (heights.isEmpty()) {
            return new Response(true, "Нет городов", null);
        }
        return new Response(true, "Высоты в порядке убывания:", heights);
    }
}