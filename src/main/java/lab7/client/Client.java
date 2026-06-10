package lab7.client;

import lab7.common.Response;
import lab7.common.requests.CommandRequest;

import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * UDP клиент для отправки запросов на сервер и получения ответов.
 * @author AlMuran
 * @version 1.0
 */
public class Client {

    private final DatagramSocket socket;
    private final InetSocketAddress serverAddress;
    private final int timeout = 2000;
    private final int maxRetries = 3;

    /**
     * Создаёт клиента и подготавливает сокет для обмена с сервером.
     * @param host хост сервера
     * @param port порт сервера
     * @throws SocketException если не удалось создать сокет
     */
    public Client(String host, int port) throws SocketException {
        this.socket = new DatagramSocket();
        this.serverAddress = new InetSocketAddress(host, port);
        socket.setSoTimeout(timeout);
    }

    /**
     * Отправляет запрос на сервер и ожидает ответа.
     * @param request объект запроса
     * @return ответ сервера
     * @throws IOException если ошибка ввода-вывода
     * @throws ClassNotFoundException если десериализация невозможна
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
                System.out.println("Таймаут, повторная отправка... (попытка " + (i + 1) + "/" + maxRetries + ")");
                if (i == maxRetries - 1) {
                    throw new IOException("Сервер не отвечает после " + maxRetries + " попыток", e);
                }
            }
        }
        throw new IOException("Не удалось отправить запрос");
    }

    /** Закрывает сокет клиента. */
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
    }
}