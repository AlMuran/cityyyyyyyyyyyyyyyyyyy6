package lab7.client.console;

import lab7.common.models.*;

import java.util.Scanner;

/**
 * Вспомогательный класс для чтения данных города с консоли.
 * @author AlMuran
 * @version 1.0
 */
public class InputHelper {

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Читает с консоли все поля города и возвращает объект City.
     * @return новый объект City
     */
    public City readCityForAdd() {
        String name = readNonEmptyString("Введите название города: ");

        System.out.println("Ввод координат:");
        Long x = readLong("  x (целое >0): ", false);
        double y = readDouble("  y (макс 793): ", 793, true);
        Coordinates coords = new Coordinates(x, y);

        Double area = readDoubleArea("Введите площадь (число >0): ");
        int population = readIntPopulation("Введите население (целое >0): ");

        System.out.print("Введите высоту над уровнем моря (целое): ");
        int meters;
        try {
            meters = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите целое число. Установлено значение 0.");
            meters = 0;
        }

        int carCode = readIntCarCode("Введите carCode (1..1000): ");
        Climate climate = readEnum(Climate.class, "Введите климат: ");
        StandardOfLiving sol = readEnum(StandardOfLiving.class, "Введите уровень жизни: ");

        System.out.println("Ввод губернатора:");
        Float height = readFloatHeight("  Рост (число >0, можно пусто): ");
        Human governor = new Human(height);

        return new City(name, coords, area, population, meters, carCode, climate, sol, governor);
    }

    private String readNonEmptyString(String data) {
        while (true) {
            System.out.print(data);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Ошибка: строка не может быть пустой.");
        }
    }

    private Long readLong(String data, boolean nullable) {
        while (true) {
            System.out.print(data);
            String input = scanner.nextLine().trim();
            if (nullable && input.isEmpty()) return null;
            try {
                long value = Long.parseLong(input);
                if (value > 0) return value;
                System.out.println("Ошибка: число должно быть > 0.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    private double readDouble(String data, double max, boolean checkMax) {
        while (true) {
            System.out.print(data);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (checkMax && value > max) {
                    System.out.println("Ошибка: значение не должно превышать " + max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }

    private Double readDoubleArea(String data) {
        while (true) {
            System.out.print(data);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value > 0) return value;
                System.out.println("Ошибка: площадь должна быть > 0.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }

    private int readIntPopulation(String data) {
        while (true) {
            System.out.print(data);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) return value;
                System.out.println("Ошибка: население должно быть > 0.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    private int readIntCarCode(String data) {
        while (true) {
            System.out.print(data);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0 && value <= 1000) return value;
                System.out.println("Ошибка: carCode должен быть > 0 и <= 1000.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    private Float readFloatHeight(String data) {
        while (true) {
            System.out.print(data);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return null;
            try {
                float value = Float.parseFloat(input);
                if (value > 0) return value;
                System.out.println("Ошибка: рост должен быть > 0.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }

    private <T extends Enum<T>> T readEnum(Class<T> enumClass, String data) {
        T[] constants = enumClass.getEnumConstants();
        System.out.print("Доступные значения: ");
        for (int i = 0; i < constants.length; i++) {
            System.out.print(constants[i].name());
            if (i < constants.length - 1) System.out.print(", ");
        }
        System.out.println();

        while (true) {
            System.out.print(data);
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return Enum.valueOf(enumClass, input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: введите одно из предложенных значений.");
            }
        }
    }
}