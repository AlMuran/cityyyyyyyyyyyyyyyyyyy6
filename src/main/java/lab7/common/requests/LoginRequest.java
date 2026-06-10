package lab7.common.requests;

import lab7.common.Response;
import lab7.server.managers.CollectionManager;

/**
 * Запрос на аутентификацию пользователя (вход в систему).
 * @author AlMuran
 * @version 1.0
 */
public class LoginRequest extends AuthenticatedRequest {

    public LoginRequest(String login, String password) {
        super(login, password);
    }

    /**
     * LoginRequest обрабатывается отдельно в CommandExecutor.
     * Этот метод не должен вызываться.
     */
    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        throw new UnsupportedOperationException("LoginRequest обрабатывается отдельно в CommandExecutor");
    }
}