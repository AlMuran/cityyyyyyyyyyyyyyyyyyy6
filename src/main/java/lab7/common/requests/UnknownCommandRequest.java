package lab7.common.requests;

import lab7.common.Response;
import lab7.server.managers.CollectionManager;

/**
 * Запрос для неизвестной команды.
 * Возвращает сообщение об ошибке с предложением ввести help.
 * @author AlMuran
 * @version 1.0
 */
public class UnknownCommandRequest extends AuthenticatedRequest {
    private final String commandName;
    private final String argument;

    /**
     * Конструктор запроса неизвестной команды.
     * @param commandName название неизвестной команды
     * @param argument аргумент команды (может быть null)
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public UnknownCommandRequest(String commandName, String argument, String login, String password) {
        super(login, password);
        this.commandName = commandName;
        this.argument = argument;
    }

    /** @return название неизвестной команды */
    public String getCommandName() { return commandName; }

    /** @return аргумент команды (может быть null) */
    public String getArgument() { return argument; }

    @Override
    public Response execute(CollectionManager collectionManager, long userId) {
        if ("exit".equalsIgnoreCase(commandName)) {
            System.exit(0);
            return new Response(true, "Сервер остановлен", null);
        }
        return new Response(false, "бебебе нет такой команды: " + commandName, null);
    }
}
