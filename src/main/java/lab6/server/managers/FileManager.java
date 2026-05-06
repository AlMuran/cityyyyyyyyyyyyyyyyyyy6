package lab6.server.managers;

import lab6.common.CsvParser;
import lab6.common.models.*;

import java.io.*;
import java.time.LocalDate;
import java.util.HashSet;

public class FileManager {
    private final String filename;

    public FileManager(String filename) {
        this.filename = filename;
    }

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
        Float height = parts[11].isEmpty() ? null : Float.parseFloat(parts[11]);

        Coordinates coords = new Coordinates(coordX, coordY);
        Human governor = new Human(height);
        return new City(id, name, coords, creationDate, area, population, meters, carCode, climate, sol, governor);
    }

    public void save(HashSet<City> cities) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename))) {
            for (City city : cities) {
                writer.write(cityToCsv(city));
                writer.write(System.lineSeparator());
            }
        }
    }

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