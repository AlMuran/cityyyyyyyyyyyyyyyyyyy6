package lab7.server.utils;

import lab7.common.models.*;
import lab7.server.database.CityDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Генератор тестовых городов для базы данных.
 * @author AlMuran
 * @version 1.0
 */
public class DataGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DataGenerator.class);
    private static final Random random = new Random();

    private static final String[] CITY_NAMES = {
            "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург",
            "Казань", "Нижний Новгород", "Челябинск", "Омск",
            "Самара", "Ростов-на-Дону", "Уфа", "Красноярск"
    };

    private static final Climate[] CLIMATES = Climate.values();
    private static final StandardOfLiving[] STANDARDS = StandardOfLiving.values();

    /**
     * Генерирует указанное количество городов для пользователя.
     * @param ownerId ID пользователя
     * @param count количество городов
     */
    public static void generateCities(long ownerId, int count) {
        CityDAO cityDAO = new CityDAO();
        int success = 0;

        logger.info("Начало генерации {} городов для пользователя {}", count, ownerId);

        for (int i = 0; i < count; i++) {
            City city = generateRandomCity();
            long id = cityDAO.insertCity(city, ownerId);
            if (id != -1) {
                success++;
                if (success % 50 == 0) {
                    logger.info("Сгенерировано {}/{} городов", success, count);
                }
            }
        }

        logger.info("Генерация завершена. Добавлено {} из {} городов", success, count);
    }

    private static City generateRandomCity() {
        String name = CITY_NAMES[random.nextInt(CITY_NAMES.length)]
                + "_" + System.currentTimeMillis()
                + "_" + random.nextInt(10000);

        Long x = (long) (random.nextInt(1000) + 1);
        double y = random.nextDouble() * 793;
        Double area = random.nextDouble() * 10000 + 1;
        int population = random.nextInt(15000000) + 1000;
        int meters = random.nextInt(5000);
        int carCode = random.nextInt(1000) + 1;

        Climate climate = CLIMATES[random.nextInt(CLIMATES.length)];
        StandardOfLiving sol = STANDARDS[random.nextInt(STANDARDS.length)];
        Float height = random.nextBoolean() ? (float) (random.nextInt(200) + 50) : null;

        return new City(name, new Coordinates(x, y), area, population,
                meters, carCode, climate, sol, new Human(height));
    }
}
