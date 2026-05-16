package lab6.client.console;

import lab6.common.models.*;
import java.util.Scanner;
/**
 * Вспомогательный класс для чтения данных города с консоли с проверкой корректности.
 * Содержит приватные методы для чтения строк, чисел, перечислений с повторными запросами при ошибках.
 */
public class InputHelper {
    private final Scanner scanner;
    /**
     * Создаёт помощник ввода.
     * @param scanner источник ввода (обычно {@code System.in})
     */
    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Ошибка: строка не может быть пустой.");
        }
    }

    private Long readLong(String prompt, boolean nullable) {
        while (true) {
            System.out.print(prompt);
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

    private double readDouble(String prompt, double max, boolean checkMax) {
        while (true) {
            System.out.print(prompt);
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

    private Double readDoubleArea(String prompt) {
        while (true) {
            System.out.print(prompt);
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

    private int readIntPopulation(String prompt) {
        while (true) {
            System.out.print(prompt);
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

    private int readIntCarCode(String prompt) {
        while (true) {
            System.out.print(prompt);
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

    private Float readFloatHeight(String prompt) {
        while (true) {
            System.out.print(prompt);
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

    private <T extends Enum<T>> T readEnum(Class<T> enumClass, String prompt) {
        System.out.println("Доступные значения: " + String.join(", ",
                java.util.Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toArray(String[]::new)));
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return Enum.valueOf(enumClass, input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: введите одно из предложенных значений.");
            }
        }
    }
    /**
     * Читает с консоли все поля города и возвращает готовый объект {@link City}.
     * Поля: название, координаты (x>0, y≤793), площадь (>0), население (>0),
     * высота над уровнем моря (целое), carCode (1..1000), климат, уровень жизни,
     * рост губернатора (опционально, >0).
     * @return новый город, созданный на основе введённых данных
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
}