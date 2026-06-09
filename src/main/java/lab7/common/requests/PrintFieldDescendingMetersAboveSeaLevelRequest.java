package lab7.common.requests;

/**
 * Запрос на вывод всех значений высоты над уровнем моря в порядке убывания.
 *
 * <p>Запрашивает у сервера список значений {@code metersAboveSeaLevel}
 * всех городов, отсортированный по убыванию.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
 * @see lab7.server.CommandExecutor
 */
public class PrintFieldDescendingMetersAboveSeaLevelRequest extends AuthenticatedRequest {
    public PrintFieldDescendingMetersAboveSeaLevelRequest(String login, String password) {
        super(login, password);
    }
}