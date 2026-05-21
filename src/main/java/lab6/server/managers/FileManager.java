package lab6.server.managers;

import lab6.common.CsvParser;
import lab6.common.models.*;

import java.io.*;
import java.time.LocalDate;
import java.util.HashSet;

/**
 * Управляет загрузкой и сохранением коллекции в CSV-файл.
 *
 * <p>Чтение и запись данных осуществляется в формате CSV с поддержкой экранирования.
 * Формат строки: 12 полей, разделённых запятыми.</p>
 *
 * <p>Порядок полей при сохранении/загрузке:
 * <ol>
 *   <li>id - уникальный идентификатор</li>
 *   <li>name - название города</li>
 *   <li>coordX - координата X</li>
 *   <li>coordY - координата Y</li>
 *   <li>creationDate - дата создания</li>
 *   <li>area - площадь</li>
 *   <li>population - население</li>
 *   <li>meters - высота над уровнем моря</li>
 *   <li>carCode - код автомобиля</li>
 *   <li>climate - климат</li>
 *   <li>standardOfLiving - уровень жизни</li>
 *   <li>height - рост губернатора (может быть пустым)</li>
 * </ol>
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CollectionManager
 * @see CsvParser
 */
public class FileManager {

    /** Путь к файлу для сохранения/загрузки данных */
    private final String filename;

    /**
     * Создаёт менеджер файлов.
     *
     * @param filename путь к CSV-файлу (из переменной окружения CITY_FILE)
     */
    public FileManager(String filename) {
        this.filename = filename;
    }

    /**
     * Загружает коллекцию городов из файла.
     *
     * <p>Если файл не существует, создаётся пустая коллекция.
     * Строки с ошибками парсинга пропускаются с выводом предупреждения.</p>
     *
     * @return HashSet загруженных городов (может быть пустым)
     * @throws IOException при ошибке чтения файла
     */
    public HashSet<City> load() throws IOException {
        HashSet<City> cities = new HashSet<>();
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Файл не найден, создана пустая коллекция.");
            return cities;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    City city = parseCity(line);
                    cities.add(city);
                } catch (Exception e) {
                    System.err.println("Ошибка парсинга строки: " + line + " -> " + e.getMessage());
                }
            }
        }
        return cities;
    }

    /**
     * Парсит CSV-строку и создаёт объект City.
     *
     * @param csvLine строка в CSV-формате (12 полей)
     * @return объект города
     * @throws IllegalArgumentException если количество полей не равно 12
     */
    private City parseCity(String csvLine) {
        String[] parts = CsvParser.parseCsvLine(csvLine);
        if (parts.length != 12) {
            throw new IllegalArgumentException("Неверное количество полей (ожидается 12)");
        }

        long id = Long.parseLong(parts[0]);
        String name = parts[1];
        Long coordX = Long.parseLong(parts[2]);
        double coordY = Double.parseDouble(parts[3]);
        LocalDate creationDate = LocalDate.parse(parts[4]);
        Double area = Double.parseDouble(parts[5]);
        int population = Integer.parseInt(parts[6]);
        int meters = Integer.parseInt(parts[7]);
        int carCode = Integer.parseInt(parts[8]);
        Climate climate = Climate.valueOf(parts[9]);
        StandardOfLiving sol = StandardOfLiving.valueOf(parts[10]);

        Float height;
        if (parts[11].isEmpty()) {
            height = null;
        } else {
            height = Float.parseFloat(parts[11]);
        }

        Coordinates coords = new Coordinates(coordX, coordY);
        Human governor = new Human(height);
        return new City(id, name, coords, creationDate, area, population, meters, carCode, climate, sol, governor);
    }

    /**
     * Сохраняет коллекцию городов в файл.
     *
     * <p>Каждый город записывается в отдельную строку в CSV-формате.
     * Поля, содержащие запятые или кавычки, экранируются.</p>
     *
     * @param cities коллекция городов для сохранения
     * @throws IOException при ошибке записи в файл
     */
    public void save(HashSet<City> cities) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename))) {
            for (City city : cities) {
                writer.write(cityToCsv(city));
                writer.write(System.lineSeparator());
            }
        }
    }

    /**
     * Преобразует город в CSV-строку.
     *
     * @param city город для преобразования
     * @return строка в CSV-формате (12 полей)
     */
    private String cityToCsv(City city) {
        return String.join(",",
                String.valueOf(city.getId()),
                CsvParser.escapeCsv(city.getName()),
                String.valueOf(city.getCoordinates().getX()),
                String.valueOf(city.getCoordinates().getY()),
                city.getCreationDate().toString(),
                String.valueOf(city.getArea()),
                String.valueOf(city.getPopulation()),
                String.valueOf(city.getMetersAboveSeaLevel()),
                String.valueOf(city.getCarCode()),
                city.getClimate().name(),
                city.getStandardOfLiving().name(),
                city.getGovernor().getHeight() == null ? "" : String.valueOf(city.getGovernor().getHeight())
        );
    }
}