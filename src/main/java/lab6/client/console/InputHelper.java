package lab6.client.console;

import lab6.common.models.*;

import java.util.Scanner;

/**
 * Вспомогательный класс для чтения данных города с консоли с проверкой корректности.
 *
 * <p>Содержит методы для чтения различных типов данных с повторными запросами
 * при ошибках ввода. Все методы обеспечивают валидацию вводимых значений.</p>
 *
 * <p>Валидация полей города:
 * <ul>
 *   <li>Название: не может быть пустым</li>
 *   <li>Координата X: целое число > 0</li>
 *   <li>Координата Y: число ≤ 793</li>
 *   <li>Площадь: число > 0</li>
 *   <li>Население: целое число > 0</li>
 *   <li>carCode: целое число от 1 до 1000</li>
 *   <li>Климат: одно из значений перечисления Climate</li>
 *   <li>Уровень жизни: одно из значений перечисления StandardOfLiving</li>
 *   <li>Рост губернатора: число > 0 (опционально, можно оставить пустым)</li>
 * </ul>
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see City
 */
public class InputHelper {

    /** Сканер для чтения ввода с консоли */
    private final Scanner scanner;

    /**
     * Создаёт помощник ввода.
     *
     * @param scanner источник ввода (обычно {@code System.in})
     */
    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Читает непустую строку с консоли.
     *
     * @param prompt приглашение для ввода
     * @return введённая непустая строка
     */
    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Ошибка: строка не может быть пустой.");
        }
    }

    /**
     * Читает целое число типа Long с консоли.
     *
     * @param prompt приглашение для ввода
     * @param nullable разрешено ли пустое значение (null)
     * @return введённое число или null, если nullable true и ввод пуст
     */
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

    /**
     * Читает вещественное число типа double с консоли.
     *
     * @param prompt приглашение для ввода
     * @param max максимально допустимое значение
     * @param checkMax нужно ли проверять максимальное значение
     * @return введённое число
     */
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

    /**
     * Читает площадь города (число > 0).
     *
     * @param prompt приглашение для ввода
     * @return введённая площадь
     */
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

    /**
     * Читает население города (целое число > 0).
     *
     * @param prompt приглашение для ввода
     * @return введённое население
     */
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

    /**
     * Читает код автомобиля (целое число от 1 до 1000).
     *
     * @param prompt приглашение для ввода
     * @return введённый код автомобиля
     */
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

    /**
     * Читает рост губернатора (число > 0, опционально).
     *
     * @param prompt приглашение для ввода
     * @return введённый рост или null, если ввод пуст
     */
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

    /**
     * Читает значение перечисления с консоли.
     *
     * <p>Выводит доступные значения перечисления и запрашивает ввод до тех пор,
     * пока не будет введено корректное значение.</p>
     *
     * @param enumClass класс перечисления
     * @param prompt приглашение для ввода
     * @param <T> тип перечисления
     * @return выбранное значение перечисления
     */
    private <T extends Enum<T>> T readEnum(Class<T> enumClass, String prompt) {
        // Выводим доступные значения
        T[] constants = enumClass.getEnumConstants();
        System.out.print("Доступные значения: ");
        for (int i = 0; i < constants.length; i++) {
            System.out.print(constants[i].name());
            if (i < constants.length - 1) System.out.print(", ");
        }
        System.out.println();

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
     *
     * <p>Последовательность ввода полей:
     * <ol>
     *   <li>Название города</li>
     *   <li>Координаты: x (>0) и y (≤793)</li>
     *   <li>Площадь (>0)</li>
     *   <li>Население (>0)</li>
     *   <li>Высота над уровнем моря (целое число)</li>
     *   <li>carCode (1-1000)</li>
     *   <li>Климат (выбор из списка)</li>
     *   <li>Уровень жизни (выбор из списка)</li>
     *   <li>Рост губернатора (>0, можно оставить пустым)</li>
     * </ol>
     * </p>
     *
     * @return новый объект City, созданный на основе введённых данных
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