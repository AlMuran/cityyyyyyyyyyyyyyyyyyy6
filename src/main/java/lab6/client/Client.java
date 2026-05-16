package lab6.client;

import lab6.common.Response;
import lab6.common.requests.CommandRequest;

import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * UDP-клиент для отправки запросов на сервер и получения ответов.
 * <p>
 * Клиент автоматически повторяет отправку при таймауте (до 3 раз).
 * Для передачи используется Java-сериализация объектов.
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see Server
 * @see CommandRequest
 * @see Response
 */
public class Client {

    /** Сокет для обмена данными по UDP */
    private final DatagramSocket socket;

    /** Адрес сервера (хост + порт) */
    private final InetSocketAddress serverAddress;

    /** Таймаут ожидания ответа в миллисекундах */
    private final int timeout = 2000;

    /** Максимальное количество повторных попыток при таймауте */
    private final int maxRetries = 3;

    /**
     * Создаёт клиента и подготавливает сокет для обмена с указанным сервером.
     *
     * @param host имя или IP-адрес сервера (например, "localhost" или "192.168.1.1")
     * @param port порт сервера (должен быть от 0 до 65535)
     * @throws SocketException если не удалось создать сокет (например, проблемы с сетью)
     */
    public Client(String host, int port) throws SocketException {
        this.socket = new DatagramSocket();
        this.serverAddress = new InetSocketAddress(host, port);
        socket.setSoTimeout(timeout);
    }

    /**
     * Отправляет запрос на сервер и ожидает ответа.
     * <p>
     * При таймауте выполняет повторные попытки (максимум {@value #maxRetries}).
     * Если после всех попыток ответ не получен, выбрасывается исключение.
     * </p>
     *
     * @param request объект запроса (должен реализовывать CommandRequest и быть сериализуемым)
     * @return ответ сервера с результатом выполнения команды
     * @throws IOException если ошибка ввода-вывода или сервер не отвечает
     * @throws ClassNotFoundException если десериализация ответа невозможна
     */
    public Response sendRequest(CommandRequest request) throws IOException, ClassNotFoundException {
        byte[] sendData = serialize(request);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress);

        for (int i = 0; i < maxRetries; i++) {
            socket.send(sendPacket);
            try {
                byte[] buffer = new byte[65507];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);
                byte[] receivedData = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
                return (Response) deserialize(receivedData);
            } catch (SocketTimeoutException e) {
                System.out.println("Таймаут, повторная отправка... (попытка " + (i+1) + "/" + maxRetries + ")");
                if (i == maxRetries - 1) {
                    throw new IOException("Сервер не отвечает после " + maxRetries + " попыток", e);
                }
            }
        }
        throw new IOException("Не удалось отправить запрос");
    }

    /**
     * Сериализует объект в массив байтов.
     *
     * @param obj объект для сериализации (должен реализовывать Serializable)
     * @return массив байтов, представляющий объект
     * @throws IOException при ошибке сериализации
     */
    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    /**
     * Десериализует объект из массива байтов.
     *
     * @param data массив байтов, полученный от сервера
     * @return восстановленный объект
     * @throws IOException при ошибке чтения данных
     * @throws ClassNotFoundException если класс объекта не найден в classpath
     */
    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
    }

    /**
     * Закрывает сокет клиента.
     * <p>
     * Метод безопасно закрывает сокет, если он открыт.
     * </p>
     */
    public void close() {
        if (socket != null && !socket.isClosed()) socket.close();
    }
}