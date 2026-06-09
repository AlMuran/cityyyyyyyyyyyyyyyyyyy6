package lab7.common.requests;

/**
 * Запрос на поиск города с минимальной координатой X.
 *
 * <p>Запрашивает у сервера город, у которого значение координаты X
 * является наименьшим среди всех городов коллекции.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
 * @see lab7.server.CommandExecutor
 */
public class MinByCoordinatesRequest extends AuthenticatedRequest {
    public MinByCoordinatesRequest(String login, String password) {
        super(login, password);
    }
}