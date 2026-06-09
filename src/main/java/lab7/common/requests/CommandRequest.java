package lab7.common.requests;

import java.io.Serializable;

/**
 * Маркерный интерфейс для всех командных запросов.
 *
 * <p>Все классы, представляющие команды клиента, должны реализовывать этот интерфейс.
 * Это позволяет унифицированно обрабатывать запросы на сервере и передавать их по сети.</p>
 *
 * <p>Интерфейс расширяет {@link Serializable}, чтобы объекты команд можно было
 * сериализовать и отправлять по UDP.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see AddRequest
 * @see UpdateRequest
 * @see RemoveByIdRequest
 * @see lab7.server.CommandExecutor
 */
public interface CommandRequest extends Serializable {

}