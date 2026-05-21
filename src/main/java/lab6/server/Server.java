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

/**
 * UDP-сервер для обработки запросов клиентов.
 *
 * <p>Сервер работает в неблокирующем режиме с использованием {@link DatagramChannel}
 * и {@link Selector}. Это позволяет обрабатывать несколько клиентов одновременно
 * в одном потоке.</p>
 *
 * <p>Алгоритм работы:
 * <ol>
 *   <li>Открывается DatagramChannel в неблокирующем режиме</li>
 *   <li>Канал привязывается к указанному порту</li>
 *   <li>Создаётся Selector для отслеживания событий</li>
 *   <li>В бесконечном цикле ожидаются входящие пакеты</li>
 *   <li>Каждый полученный запрос десериализуется и передаётся в CommandExecutor</li>
 *   <li>Результат выполнения сериализуется и отправляется обратно клиенту</li>
 * </ol>
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CommandExecutor
 * @see CommandRequest
 * @see Response
 */
public class Server {

    /** Логгер для записи событий сервера */
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    /** Порт, на котором сервер слушает запросы */
    private final int port;

    /** Исполнитель команд (обрабатывает логику команд) */
    private final CommandExecutor executor;

    /** Флаг работы сервера (true - работает, false - остановлен) */
    private volatile boolean running = true;

    /**
     * Создаёт сервер с указанным портом и исполнителем команд.
     *
     * @param port порт для прослушивания (должен быть от 0 до 65535)
     * @param executor объект, выполняющий команды
     */
    public Server(int port, CommandExecutor executor) {
        this.port = port;
        this.executor = executor;
    }

    /**
     * Запускает сервер и начинает обработку входящих запросов.
     *
     * <p>Метод работает в бесконечном цикле, ожидая входящие UDP-пакеты.
     * Для остановки сервера нужно вызвать {@link #stop()}.</p>
     *
     * <p>Максимальный размер UDP-пакета: 65507 байт.</p>
     *
     * @throws IOException если не удалось открыть порт, создать канал или селектор
     */
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

    /**
     * Останавливает сервер.
     * <p>Устанавливает флаг running в false, что приводит к выходу из основного цикла.</p>
     */
    public void stop() {
        running = false;
    }

    /**
     * Сериализует объект в массив байтов.
     *
     * @param obj объект для сериализации (должен реализовывать Serializable)
     * @return массив байтов
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
     * @param data массив байтов для десериализации
     * @return восстановленный объект
     * @throws IOException при ошибке чтения
     * @throws ClassNotFoundException если класс объекта не найден
     */
    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
    }
}