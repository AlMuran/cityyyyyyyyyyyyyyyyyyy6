package lab7.server;

import lab7.server.database.DatabaseManager;
import lab7.server.database.UserDAO;
import lab7.server.managers.CollectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Главный класс серверного приложения.
 * @author AlMuran
 * @version 1.0
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        int port = 5555;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.warn("Неверный порт, используется 5555");
            }
        }

        try {

            DatabaseManager.getInstance();
            logger.info("Подключение к БД установлено");


            UserDAO userDAO = new UserDAO();
            CollectionManager collectionManager = new CollectionManager();
            CommandExecutor executor = new CommandExecutor(userDAO, collectionManager);


            Server server = new Server(port, executor);
            server.start();

        } catch (Exception e) {
            logger.error("Ошибка запуска сервера: {}", e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

}