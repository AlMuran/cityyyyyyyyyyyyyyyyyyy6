package lab7.server;

import lab7.server.managers.CollectionManager;
import lab7.common.models.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Главный класс серверного приложения.
 *
 * <p>Точка входа в серверную часть приложения. Отвечает за:
 * <ul>
 *   <li>Чтение переменной окружения CITY_FILE с путём к файлу данных</li>
 *   <li>Загрузку коллекции из файла при старте</li>
 *   <li>Установку хука для автоматического сохранения при завершении</li>
 *   <li>Запуск консольного потока для команд save и exit</li>
 *   <li>Запуск UDP-сервера на указанном порту</li>
 * </ul>
 * </p>
 *
 * <p>Переменные окружения:
 * <ul>
 *   <li><strong>CITY_FILE</strong> - путь к CSV-файлу с данными городов (обязательна)</li>
 * </ul>
 * </p>
 *
 * <p>Аргументы командной строки:
 * <ul>
 *   <li><strong>args[0]</strong> - порт сервера (по умолчанию 5555)</li>
 * </ul>
 * </p>
 *
 * <p>Команды серверной консоли:
 * <ul>
 *   <li><strong>save</strong> - принудительное сохранение коллекции в файл</li>
 *   <li><strong>exit</strong> - сохранение коллекции и завершение сервера</li>
 * </ul>
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see Server
 * @see CommandExecutor
 * @see CollectionManager
 * @see FileManager
 */
public class Main {

    /** Логгер для записи событий сервера */
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Точка входа в серверное приложение.
     *
     * <p>Последовательность действий:
     * <ol>
     *   <li>Проверяет наличие переменной окружения CITY_FILE</li>
     *   <li>Создаёт менеджеры коллекции и файлов</li>
     *   <li>Загружает города из файла (если файл существует)</li>
     *   <li>Устанавливает хук для сохранения при завершении</li>
     *   <li>Запускает поток для обработки серверных команд (save/exit)</li>
     *   <li>Создаёт исполнитель команд и запускает UDP-сервер</li>
     * </ol>
     * </p>
     *
     * @param args аргументы командной строки (порт сервера)
     */
    public static void main(String[] args) {
        String filename = System.getenv("CITY_FILE");
        if (filename == null) {
            logger.error("Переменная окружения CITY_FILE не установлена");
            System.exit(1);
        }

        CollectionManager collectionManager = new CollectionManager();
        FileManager fileManager = new FileManager(filename);

        try {
            HashSet<City> loaded = fileManager.load();
            for (City city : loaded) {
                collectionManager.addCity(city);
            }
            logger.info("Загружено {} городов из файла {}", collectionManager.size(), filename);
        } catch (IOException e) {
            logger.error("Ошибка загрузки файла: {}", e.getMessage());
            logger.info("Начинаем с пустой коллекции");
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                fileManager.save(collectionManager.getCities());
                logger.info("Коллекция сохранена перед завершением сервера.");
            } catch (IOException e) {
                logger.error("Ошибка сохранения коллекции при завершении: {}", e.getMessage());
            }
        }));

        Thread serverCommandThread = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    String line = scanner.nextLine().trim();
                    if (line.equalsIgnoreCase("save")) {
                        try {
                            fileManager.save(collectionManager.getCities());
                            logger.info("Коллекция сохранена по команде save");
                        } catch (IOException e) {
                            logger.error("Ошибка сохранения по команде save: {}", e.getMessage());
                        }
                    } else if (line.equalsIgnoreCase("exit")) {
                        logger.info("Получена команда exit, завершение сервера...");
                        try {
                            fileManager.save(collectionManager.getCities());
                            logger.info("Коллекция сохранена перед выходом.");
                        } catch (IOException e) {
                            logger.error("Ошибка сохранения при exit: {}", e.getMessage());
                        }
                        System.exit(0);
                    } else {
                        System.out.println("Нет такой команды, бебебе.\nесть только save и exit");
                        logger.warn("Неизвестная серверная команда: {}", line);
                    }
                }
            }
        });
        serverCommandThread.setDaemon(true);
        serverCommandThread.start();


        int port = 5555;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.warn("Неверный порт, используется порт по умолчанию {}", port);
            }
        }


        CommandExecutor executor = new CommandExecutor(collectionManager, fileManager);
        Server server = new Server(port, executor);
        try {
            server.start();
        } catch (IOException e) {
            logger.error("Ошибка запуска сервера: {}", e.getMessage());
            System.exit(1);
        }
    }
}