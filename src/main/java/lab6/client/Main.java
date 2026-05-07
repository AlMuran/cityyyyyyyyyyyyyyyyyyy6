package lab6.client;

public class Main {
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