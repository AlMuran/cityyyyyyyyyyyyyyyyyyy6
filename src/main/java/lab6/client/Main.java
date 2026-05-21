package lab6.client;

/**
 * Главный класс клиентского приложения.
 *
 * <p>Точка входа в клиентскую часть приложения. Читает аргументы командной строки
 * (хост и порт сервера), создаёт экземпляр клиента и запускает консольный интерфейс.</p>
 *
 * <p>Аргументы командной строки:
 * <ul>
 *   <li><strong>args[0]</strong> - хост сервера (по умолчанию "localhost")</li>
 *   <li><strong>args[1]</strong> - порт сервера (по умолчанию 5555)</li>
 * </ul>
 * </p>
 *
 * <p>Пример запуска:
 * <pre>
 * java lab6.client.Main localhost 5555
 * java lab6.client.Main 192.168.1.100 8080
 * </pre>
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see Client
 * @see ConsoleManager
 */
public class Main {

    /**
     * Точка входа в клиентское приложение.
     *
     * <p>Последовательность действий:
     * <ol>
     *   <li>Парсит аргументы командной строки для получения хоста и порта</li>
     *   <li>Создаёт экземпляр UDP-клиента</li>
     *   <li>Создаёт консольный менеджер и запускает его</li>
     *   <li>В случае ошибки выводит стек трейс</li>
     * </ol>
     * </p>
     *
     * @param args аргументы командной строки (хост, порт)
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5555;

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Неверный порт, используется порт по умолчанию 5555");
            }
        }

        try {
            Client client = new Client(host, port);
            ConsoleManager console = new ConsoleManager(client);
            console.start();
        } catch (Exception e) {
            System.err.println("Ошибка клиента: " + e.getMessage());
            e.printStackTrace();
        }
    }
}