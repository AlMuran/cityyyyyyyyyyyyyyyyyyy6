package lab7.common.requests;

/**
 * Запрос на полную очистку коллекции.
 *
 * <p>Не содержит никаких дополнительных данных — достаточно самого факта
 * существования запроса, чтобы сервер понял команду.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
 * @see lab7.server.CommandExecutor
 */
public class ClearRequest extends AuthenticatedRequest {
    public ClearRequest(String login, String password) {
        super(login, password);
    }
}