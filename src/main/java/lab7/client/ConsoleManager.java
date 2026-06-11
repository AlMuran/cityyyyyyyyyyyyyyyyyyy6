package lab7.client;

import lab7.client.console.InputHelper;
import lab7.common.CsvParser;
import lab7.common.Response;
import lab7.common.models.*;
import lab7.common.requests.*;

import java.io.*;
import java.util.*;

/**
 * Управление консольным интерфейсом клиента.
 * @author AlMuran
 * @version 1.0
 */
public class ConsoleManager {
    private final Client client;
    private final InputHelper inputHelper;
    private final Deque<String> history = new ArrayDeque<>(6);
    private final Set<String> executingScripts = new HashSet<>();
    private final Scanner scanner;

    private String currentLogin = null;
    private String currentPassword = null;
    private boolean authenticated = false;

    public ConsoleManager(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.inputHelper = new InputHelper(scanner);
    }

    /** Запускает основной цикл консольного интерфейса. */
    public void start() {
        System.out.println("Клиент запущен. Сначала выполните вход или регистрацию.");

        while (!authenticated) {
            System.out.print("Введите команду (login / register / exit): ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Завершение клиента.");
                client.close();
                scanner.close();
                System.exit(0);
            } else if (line.equalsIgnoreCase("login")) {
                doLogin();
            } else if (line.equalsIgnoreCase("register")) {
                doRegister();
            } else {
                System.out.println("Необходимо сначала выполнить login или register");
            }
        }

        System.out.println("Добро пожаловать, " + currentLogin + "!");
        System.out.println("Введите 'help' для списка команд.");

        label:
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String command = parts[0];
            String argument = (parts.length > 1) ? parts[1] : null;

            addToHistory(command);

            switch (command) {
                case "exit":
                    System.out.println("Завершение клиента.");
                    break label;
                case "help":
                    printHelp();
                    break;
                case "history":
                    printHistory();
                    break;
                case "execute_script":
                    if (argument == null) {
                        System.out.println("Ошибка: укажите имя файла.");
                    } else {
                        executeScript(argument);
                    }
                    break;
                default:
                    try {
                        CommandRequest request = buildRequest(command, argument);
                        Response response = client.sendRequest(request);
                        System.out.println(response.getMessage());
                        if (response.getData() != null) {
                            printData(response.getData());
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
            }
        }
        client.close();
        scanner.close();
    }

    private void doLogin() {
        System.out.print("Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();
        try {
            LoginRequest request = new LoginRequest(login, password);
            Response response = client.sendRequest(request);
            if (response.isSuccess()) {
                currentLogin = login;
                currentPassword = password;
                authenticated = true;
                System.out.println("Успешный вход.");
            } else {
                System.out.println("Ошибка входа: " + response.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void doRegister() {
        System.out.print("Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();
        try {
            RegisterRequest request = new RegisterRequest(login, password);
            Response response = client.sendRequest(request);
            if (response.isSuccess()) {
                System.out.println("Регистрация успешна. Теперь выполните вход.");
            } else {
                System.out.println("Ошибка регистрации: " + response.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void addToHistory(String cmd) {
        if (history.size() == 6) history.pollFirst();
        history.addLast(cmd);
    }

    private void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("  help                           - справка");
        System.out.println("  history                        - последние 6 команд");
        System.out.println("  execute_script <file>          - выполнить скрипт");
        System.out.println("  exit                           - завершить клиент");
        System.out.println("  info                           - информация о коллекции");
        System.out.println("  show                           - показать все элементы");
        System.out.println("  add                            - добавить город");
        System.out.println("  update <id>                    - обновить город по id");
        System.out.println("  remove_by_id <id>              - удалить по id");
        System.out.println("  clear                          - очистить свои города");
        System.out.println("  add_if_max                     - добавить, если больше максимального");
        System.out.println("  remove_lower                   - удалить все меньшие заданного");
        System.out.println("  remove_any_by_car_code <code>  - удалить город с указанным carCode");
        System.out.println("  min_by_coordinates             - город с минимальной координатой X");
        System.out.println("  print_field_descending_meters_above_sea_level - высоты в порядке убывания");
    }

    private void printHistory() {
        if (history.isEmpty()) {
            System.out.println("История пуста.");
        } else {
            history.forEach(System.out::println);
        }
    }

    private void printData(Object data) {
        if (data instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) data;
            if (collection.isEmpty()) {
                System.out.println("Коллекция пуста");
                return;
            }

            System.out.println("\n--- Города (" + collection.size() + " шт.) ---");
            for (Object obj : collection) {
                if (obj instanceof City) {
                    City city = (City) obj;
                    System.out.printf("%d. %s | Население: %d | Площадь: %.1f | CarCode: %d%n",
                            city.getId(), city.getName(), city.getPopulation(),
                            city.getArea(), city.getCarCode());
                }
            }
            System.out.println();

        } else if (data != null) {
            System.out.println(data);
        }
    }


    private CommandRequest buildRequest(String command, String arg) throws Exception {
        if (!authenticated) {
            throw new IllegalStateException("Не выполнена аутентификация");
        }

        switch (command) {
            case "info":
                return new InfoRequest(currentLogin, currentPassword);
            case "show":
                return new ShowRequest(currentLogin, currentPassword);
            case "clear":
                return new ClearRequest(currentLogin, currentPassword);
            case "min_by_coordinates":
                return new MinByCoordinatesRequest(currentLogin, currentPassword);
            case "print_field_descending_meters_above_sea_level":
                return new PrintFieldDescendingMetersAboveSeaLevelRequest(currentLogin, currentPassword);
            case "add": {
                City city = inputHelper.readCityForAdd();
                return new AddRequest(city, currentLogin, currentPassword);
            }
            case "add_if_max": {
                City city = inputHelper.readCityForAdd();
                return new AddIfMaxRequest(city, currentLogin, currentPassword);
            }
            case "remove_lower": {
                City reference = inputHelper.readCityForAdd();
                return new RemoveLowerRequest(reference, currentLogin, currentPassword);
            }
            case "update": {
                if (arg == null) throw new IllegalArgumentException("Укажите id");
                long id = Long.parseLong(arg);
                City city = inputHelper.readCityForAdd();
                return new UpdateRequest(id, city, currentLogin, currentPassword);
            }
            case "remove_by_id": {
                if (arg == null) throw new IllegalArgumentException("Укажите id");
                long id = Long.parseLong(arg);
                return new RemoveByIdRequest(id, currentLogin, currentPassword);
            }
            case "remove_any_by_car_code": {
                if (arg == null) throw new IllegalArgumentException("Укажите carCode");
                int carCode = Integer.parseInt(arg);
                return new RemoveAnyByCarCodeRequest(carCode, currentLogin, currentPassword);
            }
            default:
                return new UnknownCommandRequest(command, arg, currentLogin, currentPassword);
        }
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

    private CommandRequest buildRequestFromScript(String command, String[] fields) throws Exception {
        if (!authenticated) {
            throw new IllegalStateException("Не выполнена аутентификация");
        }

        switch (command) {
            case "info":
            case "show":
            case "clear":
            case "min_by_coordinates":
            case "print_field_descending_meters_above_sea_level":
                if (fields.length != 1) {
                    throw new IllegalArgumentException("Команда не должна иметь аргументов");
                }
                return buildRequest(command, null);

            case "remove_by_id":
                if (fields.length != 2) throw new IllegalArgumentException("Ожидается id");
                return new RemoveByIdRequest(Long.parseLong(fields[1]), currentLogin, currentPassword);

            case "remove_any_by_car_code":
                if (fields.length != 2) throw new IllegalArgumentException("Ожидается carCode");
                return new RemoveAnyByCarCodeRequest(Integer.parseInt(fields[1]), currentLogin, currentPassword);

            case "add":
            case "add_if_max":
            case "remove_lower":
                if (fields.length != 11) throw new IllegalArgumentException("Ожидается 10 полей города");
                City city = parseCityFromFields(fields, 1);
                if (command.equals("add")) {
                    return new AddRequest(city, currentLogin, currentPassword);
                } else if (command.equals("add_if_max")) {
                    return new AddIfMaxRequest(city, currentLogin, currentPassword);
                } else {
                    return new RemoveLowerRequest(city, currentLogin, currentPassword);
                }

            case "update":
                if (fields.length != 12) throw new IllegalArgumentException("Ожидается id и 10 полей города");
                long updateId = Long.parseLong(fields[1]);
                City updateCity = parseCityFromFields(fields, 2);
                return new UpdateRequest(updateId, updateCity, currentLogin, currentPassword);

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
}