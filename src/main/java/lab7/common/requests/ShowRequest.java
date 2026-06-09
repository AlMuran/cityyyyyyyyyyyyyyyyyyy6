package lab7.common.requests;

/**
 * Запрос на получение всех элементов коллекции.
 *
 * <p>Запрашивает у сервера список всех городов в коллекции,
 * отсортированных в естественном порядке.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
 * @see lab7.server.CommandExecutor
 */
public class ShowRequest extends AuthenticatedRequest {
    public ShowRequest(String login, String password) {
        super(login, password);
    }
}