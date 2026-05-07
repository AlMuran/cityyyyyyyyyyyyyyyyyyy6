package lab6.server;

import lab6.common.Response;
import lab6.common.requests.CommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final int port;
    private final CommandExecutor executor;
    private volatile boolean running = true;

    public Server(int port, CommandExecutor executor) {
        this.port = port;
        this.executor = executor;
    }

    public void start() throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(port));
        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
        logger.info("Сервер запущен на порту {}", port);

        while (running) {
            selector.select(100);
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                if (!key.isValid()) continue;
                if (key.isReadable()) {
                    DatagramChannel ch = (DatagramChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(65507);
                    SocketAddress clientAddr = ch.receive(buffer);
                    if (clientAddr != null) {
                        buffer.flip();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        logger.debug("Получено {} байт от {}", data.length, clientAddr);
                        try {
                            CommandRequest request = (CommandRequest) deserialize(data);
                            logger.info("Запрос: {} от {}", request.getClass().getSimpleName(), clientAddr);
                            Response response = executor.execute(request);
                            byte[] respData = serialize(response);
                            ch.send(ByteBuffer.wrap(respData), clientAddr);
                            logger.debug("Отправлен ответ клиенту {}", clientAddr);
                        } catch (Exception e) {
                            logger.error("Ошибка обработки запроса от {}: {}", clientAddr, e.getMessage());
                            Response errorResp = new Response(false, "Ошибка сервера: " + e.getMessage(), null);
                            byte[] errData = serialize(errorResp);
                            ch.send(ByteBuffer.wrap(errData), clientAddr);
                        }
                    }
                }
            }
        }
        channel.close();
        selector.close();
        logger.info("Сервер остановлен");
    }

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
}