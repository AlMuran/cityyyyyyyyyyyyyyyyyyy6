package lab6.common.requests;

/**
 * Запрос для неизвестной команды.
 *
 * <p>Создаётся клиентом, когда пользователь ввёл команду,
 * которая не распознана. Содержит название команды и её аргумент
 * для возможной обработки или логирования.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandRequest
 * @see lab6.server.CommandExecutor#execute(CommandRequest)
 */
public class UnknownCommandRequest implements CommandRequest {

    /** Название неизвестной команды */
    private final String commandName;

    /** Аргумент команды (может быть null) */
    private final String argument;

    /**
     * Создаёт запрос для неизвестной команды.
     *
     * @param commandName название команды, которую не распознали
     * @param argument аргумент команды (может быть null)
     */
    public UnknownCommandRequest(String commandName, String argument) {
        this.commandName = commandName;
        this.argument = argument;
    }

    /**
     * Возвращает название неизвестной команды.
     *
     * @return название команды
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Возвращает аргумент команды.
     *
     * @return аргумент (может быть null)
     */
    public String getArgument() {
        return argument;
    }
}