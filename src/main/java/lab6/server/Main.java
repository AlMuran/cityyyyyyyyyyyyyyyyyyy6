package lab6.server;

import lab6.server.managers.CollectionManager;
import lab6.server.managers.FileManager;
import lab6.common.models.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // 1. Чтение пути к файлу из переменной окружения
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


        Thread saveCommandThread = new Thread(() -> {
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
                    }
                }
            }
        });
        saveCommandThread.setDaemon(true);
        saveCommandThread.start();

        int port = 8080;
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