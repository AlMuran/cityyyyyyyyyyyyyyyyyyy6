package lab7.server;

import lab7.common.Response;
import lab7.common.requests.CommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UDP сервер для приёма и обработки запросов от клиентов.
 * Многопоточность: Thread (чтение) -> CachedThreadPool (обработка) -> FixedThreadPool (отправка)
 * @author AlMuran
 * @version 1.0
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final int port;
    private final CommandExecutor executor;
    private volatile boolean running = true;

    private final ExecutorService cachedPool = Executors.newCachedThreadPool();
    private final ExecutorService fixedPool = Executors.newFixedThreadPool(10);

    public Server(int port, CommandExecutor executor) {
        this.port = port;
        this.executor = executor;
    }

    /**
     * Запускает сервер и начинает прослушивание порта.
     * @throws IOException если не удалось открыть сокет
     */
    public void start() throws IOException {
        DatagramSocket socket = new DatagramSocket(port);
        logger.info("Сервер запущен на порту {}", port);

        while (running) {
            byte[] buffer = new byte[65507];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(receivePacket);


            new Thread(() -> {
                byte[] data = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
                SocketAddress clientAddress = receivePacket.getSocketAddress();


                cachedPool.submit(() -> {
                    try {
                        CommandRequest request = (CommandRequest) deserialize(data);
                        Response response = executor.execute(request);


                        fixedPool.submit(() -> {
                            try {
                                byte[] respData = serialize(response);
                                DatagramPacket sendPacket = new DatagramPacket(respData, respData.length, clientAddress);
                                socket.send(sendPacket);
                                logger.debug("Ответ отправлен клиенту {}", clientAddress);
                            } catch (IOException e) {
                                logger.error("Ошибка отправки ответа: {}", e.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        logger.error("Ошибка обработки запроса: {}", e.getMessage());
                        fixedPool.submit(() -> {
                            try {
                                Response errorResp = new Response(false, "Ошибка сервера: " + e.getMessage(), null);
                                byte[] errData = serialize(errorResp);
                                DatagramPacket errPacket = new DatagramPacket(errData, errData.length, clientAddress);
                                socket.send(errPacket);
                            } catch (IOException ex) {
                                logger.error("Ошибка отправки ошибки: {}", ex.getMessage());
                            }
                        });
                    }
                });
            }).start();
        }
        socket.close();
        shutdown();
    }

    /**
     * Останавливает сервер.
     */
    public void stop() {
        running = false;
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

    private void shutdown() {
        cachedPool.shutdown();
        fixedPool.shutdown();
        logger.info("Сервер остановлен");
    }
}