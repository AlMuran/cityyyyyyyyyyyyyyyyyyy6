package lab7.server;

import lab7.common.Response;
import lab7.common.requests.*;
import lab7.server.database.UserDAO;
import lab7.server.managers.CollectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Выполнитель команд. Обрабатывает аутентификацию и делегирует выполнение команд.
 * @author AlMuran
 * @version 1.0
 */
public class CommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
    private final UserDAO userDAO;
    private final CollectionManager collectionManager;

    public CommandExecutor(UserDAO userDAO, CollectionManager collectionManager) {
        this.userDAO = userDAO;
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет полученную команду.
     * @param request запрос от клиента
     * @return ответ сервера
     */
    public Response execute(CommandRequest request) {
        logger.debug("Получен запрос: {}", request.getClass().getSimpleName());


        if (request instanceof LoginRequest) {
            return handleLogin((LoginRequest) request);
        }


        if (request instanceof RegisterRequest) {
            return handleRegister((RegisterRequest) request);
        }


        if (!(request instanceof AuthenticatedRequest)) {
            return new Response(false, "Ошибка: запрос не содержит данных аутентификации", null);
        }

        AuthenticatedRequest authReq = (AuthenticatedRequest) request;
        long userId = userDAO.authenticateAndGetId(authReq.getLogin(), authReq.getPassword());

        if (userId == -1) {
            logger.warn("Ошибка аутентификации: {}", authReq.getLogin());
            return new Response(false, "Неверный логин или пароль. Выполните login", null);
        }


        return request.execute(collectionManager, userId);
    }

    private Response handleLogin(LoginRequest req) {
        long userId = userDAO.authenticateAndGetId(req.getLogin(), req.getPassword());
        if (userId != -1) {
            logger.info("Пользователь {} авторизован", req.getLogin());
            return new Response(true, "Авторизация успешна. Добро пожаловать!", null);
        } else {
            logger.warn("Неудачная попытка входа: {}", req.getLogin());
            return new Response(false, "Неверный логин или пароль", null);
        }
    }

    private Response handleRegister(RegisterRequest req) {
        boolean registered = userDAO.registerUser(req.getLogin(), req.getPassword());
        if (registered) {
            logger.info("Зарегистрирован новый пользователь: {}", req.getLogin());
            return new Response(true, "Регистрация успешна! Теперь выполните login", null);
        } else {
            logger.warn("Попытка регистрации существующего логина: {}", req.getLogin());
            return new Response(false, "Пользователь с таким логином уже существует", null);
        }
    }
}