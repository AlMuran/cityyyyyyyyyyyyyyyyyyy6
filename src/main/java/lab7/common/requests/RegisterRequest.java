package lab7.common.requests;

import lab7.common.Response;
import lab7.server.managers.CollectionManager;

/**
 * Запрос на регистрацию нового пользователя.
 * @author AlMuran
 * @version 1.0
 */
public class RegisterRequest extends AuthenticatedRequest {

    public RegisterRequest(String login, String password) {
        super(login, password);
    }

    /**
     * RegisterRequest обрабатывается отдельно в CommandExecutor.
     * Этот метод не должен вызываться.
     */
    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        throw new UnsupportedOperationException("RegisterRequest обрабатывается отдельно в CommandExecutor");
    }
}