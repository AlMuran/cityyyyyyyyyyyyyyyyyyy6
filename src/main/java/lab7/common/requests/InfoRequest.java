package lab7.common.requests;

/**
 * Запрос на получение информации о коллекции.
 *
 * <p>Запрашивает у сервера информацию о коллекции: тип коллекции,
 * дату инициализации, количество элементов.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
 * @see lab7.server.CommandExecutor
 */
public class InfoRequest extends AuthenticatedRequest {
    public InfoRequest(String login, String password) {
        super(login, password);
    }
}