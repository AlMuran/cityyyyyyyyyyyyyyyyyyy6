package lab6.client;

import lab6.client.console.InputHelper;
import lab6.common.CsvParser;
import lab6.common.Response;
import lab6.common.models.*;
import lab6.common.requests.*;

import java.io.*;
import java.util.*;

public class ConsoleManager {
    private final Client client;
    private final InputHelper inputHelper;
    private final Deque<String> history = new ArrayDeque<>(6);
    private final Set<String> executingScripts = new HashSet<>();
    private final Scanner scanner;

    public ConsoleManager(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.inputHelper = new InputHelper(scanner);
    }

    public void start() {
        System.out.println("Клиент запущен. Введите 'help' для списка команд.");
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+", 2);
            String command = parts[0];
            String argument = parts.length > 1 ? parts[1] : null;

            addToHistory(command);

            if (command.equals("exit")) {
                System.out.println("Завершение клиента.");
                break;
            } else if (command.equals("help")) {
                printHelp();
            } else if (command.equals("history")) {
                printHistory();
            } else if (command.equals("execute_script")) {
                if (argument == null) System.out.println("Ошибка: укажите имя файла.");
                else executeScript(argument);
            } else {
                try {
                    CommandRequest request = buildRequestFromUserInput(command, argument);
                    Response response = client.sendRequest(request);
                    System.out.println(response.getMessage());
                    if (response.getData() != null) printData(response.getData());
                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
        client.close();
        scanner.close();
    }

    private void addToHistory(String cmd) {
        if (history.size() == 6) history.pollFirst();
        history.addLast(cmd);
    }

    private void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("  help                           : вывести справку");
        System.out.println("  history                        : показать последние 6 команд");
        System.out.println("  execute_script <file>          : выполнить скрипт (CSV с экранированием)");
        System.out.println("  exit                           : завершить клиент");
        System.out.println("  info                           : информация о коллекции");
        System.out.println("  show                           : показать все элементы");
        System.out.println("  add                            : добавить город");
        System.out.println("  update <id>                    : обновить город по id");
        System.out.println("  remove_by_id <id>              : удалить по id");
        System.out.println("  clear                          : очистить коллекцию");
        System.out.println("  add_if_max                     : добавить, если больше максимального");
        System.out.println("  remove_lower                   : удалить все меньшие заданного");
        System.out.println("  remove_any_by_car_code <code>  : удалить город с указанным carCode");
        System.out.println("  min_by_coordinates             : город с минимальной координатой X");
        System.out.println("  print_field_descending_meters_above_sea_level : высоты в порядке убывания");
    }

    private void printHistory() {
        if (history.isEmpty()) System.out.println("История пуста.");
        else history.forEach(System.out::println);
    }

    private void executeScript(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Файл не найден.");
            return;
        }
        if (executingScripts.contains(filename)) {
            System.out.println("Рекурсия в скрипте " + filename + " запрещена.");
            return;
        }
        executingScripts.add(filename);
        try (Scanner fileScanner = new Scanner(file)) {
            int lineNumber = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                lineNumber++;
                if (line.trim().isEmpty() || line.trim().startsWith("#")) continue;
                System.out.println("> " + line);

                try {
                    String[] fields = CsvParser.parseCsvLine(line);
                    if (fields.length == 0) continue;
                    String cmd = fields[0];

                    // Поддержка вложенных execute_script
                    if (cmd.equals("execute_script")) {
                        if (fields.length != 2) {
                            System.out.println("Ошибка: execute_script требует имя файла.");
                            continue;
                        }
                        executeScript(fields[1]);
                        continue;
                    }

                    CommandRequest request = buildRequestFromScript(cmd, fields);
                    Response response = client.sendRequest(request);
                    System.out.println(response.getMessage());
                    if (response.getData() != null) printData(response.getData());
                } catch (Exception e) {
                    System.out.println("Ошибка в скрипте (строка " + lineNumber + "): " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден.");
        } finally {
            executingScripts.remove(filename);
        }
    }

    private CommandRequest buildRequestFromUserInput(String command, String arg) throws Exception {
        switch (command) {
            case "info": return new InfoRequest();
            case "show": return new ShowRequest();
            case "clear": return new ClearRequest();
            case "min_by_coordinates": return new MinByCoordinatesRequest();
            case "print_field_descending_meters_above_sea_level":
                return new PrintFieldDescendingMetersAboveSeaLevelRequest();
            case "add": {
                City city = inputHelper.readCityForAdd();
                return new AddRequest(city);
            }
            case "add_if_max": {
                City city = inputHelper.readCityForAdd();
                return new AddIfMaxRequest(city);
            }
            case "remove_lower": {
                City reference = inputHelper.readCityForAdd();
                return new RemoveLowerRequest(reference);
            }
            case "update": {
                if (arg == null) throw new IllegalArgumentException("Укажите id");
                long id = Long.parseLong(arg);
                City city = inputHelper.readCityForAdd();
                return new UpdateRequest(id, city);
            }
            case "remove_by_id": {
                if (arg == null) throw new IllegalArgumentException("Укажите id");
                long id = Long.parseLong(arg);
                return new RemoveByIdRequest(id);
            }
            case "remove_any_by_car_code": {
                if (arg == null) throw new IllegalArgumentException("Укажите carCode");
                int carCode = Integer.parseInt(arg);
                return new RemoveAnyByCarCodeRequest(carCode);
            }
            default:
                return new UnknownCommandRequest(command, arg);
        }
    }

    private CommandRequest buildRequestFromScript(String command, String[] fields) throws Exception {
        switch (command) {
            case "info":
            case "show":
            case "clear":
            case "min_by_coordinates":
            case "print_field_descending_meters_above_sea_level":
                if (fields.length != 1) throw new IllegalArgumentException("Команда не должна иметь аргументов");
                return buildRequestFromUserInput(command, null);

            case "remove_by_id":
                if (fields.length != 2) throw new IllegalArgumentException("Ожидается id");
                long id = Long.parseLong(fields[1]);
                return new RemoveByIdRequest(id);

            case "remove_any_by_car_code":
                if (fields.length != 2) throw new IllegalArgumentException("Ожидается carCode");
                int code = Integer.parseInt(fields[1]);
                return new RemoveAnyByCarCodeRequest(code);

            case "add":
            case "add_if_max":
            case "remove_lower":
                if (fields.length != 11) throw new IllegalArgumentException("Ожидается 10 полей города");
                City city = parseCityFromFields(fields, 1);
                if (command.equals("add")) return new AddRequest(city);
                if (command.equals("add_if_max")) return new AddIfMaxRequest(city);
                return new RemoveLowerRequest(city);

            case "update":
                if (fields.length != 12) throw new IllegalArgumentException("Ожидается id и 10 полей города");
                long updateId = Long.parseLong(fields[1]);
                City updateCity = parseCityFromFields(fields, 2);
                return new UpdateRequest(updateId, updateCity);

            default:
                throw new IllegalArgumentException("Неизвестная команда: " + command);
        }
    }

    private City parseCityFromFields(String[] fields, int start) throws Exception {
        try {
            String name = fields[start];
            Long x = Long.parseLong(fields[start + 1]);
            double y = Double.parseDouble(fields[start + 2]);
            Double area = Double.parseDouble(fields[start + 3]);
            int population = Integer.parseInt(fields[start + 4]);
            int meters = Integer.parseInt(fields[start + 5]);
            int carCode = Integer.parseInt(fields[start + 6]);
            Climate climate = Climate.valueOf(fields[start + 7].toUpperCase());
            StandardOfLiving sol = StandardOfLiving.valueOf(fields[start + 8].toUpperCase());
            Float height = fields[start + 9].equals("null") ? null : Float.parseFloat(fields[start + 9]);
            Coordinates coords = new Coordinates(x, y);
            Human governor = new Human(height);
            return new City(name, coords, area, population, meters, carCode, climate, sol, governor);
        } catch (Exception e) {
            throw new Exception("Ошибка парсинга города: " + e.getMessage());
        }
    }

    private void printData(Object data) {
        if (data instanceof Collection<?>) {
            for (Object obj : (Collection<?>) data) {
                System.out.println(obj);
            }
        } else if (data != null) {
            System.out.println(data);
        }
    }
}