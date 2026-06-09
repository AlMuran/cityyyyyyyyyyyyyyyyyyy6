package lab7.common;

import java.io.Serializable;

/**
 * Ответ сервера на запрос клиента.
 *
 * <p>Содержит три компонента:
 * <ul>
 *   <li><strong>success</strong> - флаг успешного выполнения команды</li>
 *   <li><strong>message</strong> - текстовое сообщение для пользователя</li>
 *   <li><strong>data</strong> - дополнительные данные (коллекция, объект и т.д.)</li>
 * </ul>
 * </p>
 *
 * <p>Примеры использования:
 * <pre>
 * // Успешное выполнение без данных
 * new Response(true, "Город добавлен", null);
 *
 * // Успешное выполнение с данными
 * new Response(true, "Список городов:", citiesList);
 *
 * // Ошибка
 * new Response(false, "Город с таким id не найден", null);
 * </pre>
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see lab7.client.Client
 */
public class Response implements Serializable {

    /** Флаг успешности выполнения команды */
    private final boolean success;

    /** Сообщение для пользователя */
    private final String message;

    /** Дополнительные данные (может быть null) */
    private final Object data;

    /**
     * Создаёт новый ответ сервера.
     *
     * @param success true, если команда выполнена успешно
     * @param message текстовое сообщение для вывода пользователю
     * @param data дополнительные данные (список городов, один город и т.д.)
     */
    public Response(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Возвращает флаг успешности выполнения команды.
     *
     * @return true, если команда выполнена успешно
     */
    public boolean isSuccess() { return success; }

    /**
     * Возвращает сообщение для пользователя.
     *
     * @return текстовое сообщение
     */
    public String getMessage() { return message; }

    /**
     * Возвращает дополнительные данные.
     *
     * @return данные (может быть null, Collection, City и т.д.)
     */
    public Object getData() { return data; }
}