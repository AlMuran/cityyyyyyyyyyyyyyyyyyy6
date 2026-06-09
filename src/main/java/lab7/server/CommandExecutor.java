package lab7.server;

import lab7.common.Response;
import lab7.common.requests.*;
import lab7.common.models.City;
import lab7.server.managers.CollectionManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Исполнитель команд на сервере.
 *
 * <p>Содержит логику выполнения всех доступных команд. Получает запрос от клиента,
 * определяет его тип и вызывает соответствующий метод-обработчик.</p>
 *
 * <p>Доступные команды:
 * <ul>
 *   <li>add - добавление города</li>
 *   <li>update - обновление города</li>
 *   <li>remove_by_id - удаление по id</li>
 *   <li>remove_any_by_car_code - удаление по carCode</li>
 *   <li>remove_lower - удаление меньших</li>
 *   <li>add_if_max - добавление максимального</li>
 *   <li>clear - очистка коллекции</li>
 *   <li>info - информация о коллекции</li>
 *   <li>show - показать все элементы</li>
 *   <li>min_by_coordinates - поиск по минимальной координате</li>
 *   <li>print_field_descending_meters_above_sea_level - вывод высот по убыванию</li>
 * </ul>
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see CollectionManager
 * @see FileManager
 * @see CommandRequest
 */
public class CommandExecutor {

    /** Менеджер коллекции для работы с городами */
    private final CollectionManager collectionManager;

    /** Менеджер файлов для сохранения данных */
    private final FileManager fileManager;

    /**
     * Создаёт исполнитель команд.
     *
     * @param collectionManager менеджер коллекции городов
     * @param fileManager менеджер для работы с файлом сохранения
     */
    public CommandExecutor(CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    /**
     * Обрабатывает входящий запрос и возвращает ответ.
     *
     * <p>Метод определяет тип команды через {@code instanceof}
     * и вызывает соответствующий приватный метод-обработчик.</p>
     *
     * @param request запрос от клиента
     * @return ответ с результатом выполнения
     */
    public Response execute(CommandRequest request) {
        if (request instanceof UnknownCommandRequest) {
            return new Response(false, "Нет такой команды, бебебе.\nВведите help.", null);
        }
        if (request instanceof AddRequest) return handleAdd((AddRequest) request);
        if (request instanceof UpdateRequest) return handleUpdate((UpdateRequest) request);
        if (request instanceof RemoveByIdRequest) return handleRemoveById((RemoveByIdRequest) request);
        if (request instanceof RemoveAnyByCarCodeRequest) return handleRemoveAnyByCarCode((RemoveAnyByCarCodeRequest) request);
        if (request instanceof RemoveLowerRequest) return handleRemoveLower((RemoveLowerRequest) request);
        if (request instanceof AddIfMaxRequest) return handleAddIfMax((AddIfMaxRequest) request);
        if (request instanceof ClearRequest) return handleClear();
        if (request instanceof InfoRequest) return handleInfo();
        if (request instanceof ShowRequest) return handleShow();
        if (request instanceof MinByCoordinatesRequest) return handleMinByCoordinates();
        if (request instanceof PrintFieldDescendingMetersAboveSeaLevelRequest)
            return handlePrintFieldDescendingMetersAboveSeaLevel();
        return new Response(false, "Неизвестная команда", null);
    }

    /**
     * Сохраняет коллекцию в файл.
     * <p>Вызывается после каждого изменения коллекции.</p>
     */
    private void saveCollection() {
        try {
            fileManager.save(collectionManager.getCities());
        } catch (Exception e) {
            System.err.println("Ошибка автосохранения: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает команду добавления города.
     *
     * @param req запрос с городом для добавления
     * @return ответ с сообщением об успехе и присвоенным id
     */
    private Response handleAdd(AddRequest req) {
        City city = req.getCity();
        city.setId(collectionManager.generateId());
        city.setCreationDate(LocalDate.now());
        collectionManager.addCity(city);
        saveCollection();
        return new Response(true, "Город добавлен с id=" + city.getId(), null);
    }

    /**
     * Обрабатывает команду обновления города.
     *
     * @param req запрос с id и новыми данными города
     * @return ответ с сообщением об успехе или ошибке
     */
    private Response handleUpdate(UpdateRequest req) {
        if (!collectionManager.containsId(req.getId())) {
            return new Response(false, "Элемент с id=" + req.getId() + " не найден.", null);
        }
        City oldCity = collectionManager.getById(req.getId()).get();
        City newData = req.getCity();
        City updatedCity = new City(
                oldCity.getId(),
                newData.getName(),
                newData.getCoordinates(),
                oldCity.getCreationDate(),
                newData.getArea(),
                newData.getPopulation(),
                newData.getMetersAboveSeaLevel(),
                newData.getCarCode(),
                newData.getClimate(),
                newData.getStandardOfLiving(),
                newData.getGovernor()
        );
        collectionManager.removeById(req.getId());
        collectionManager.addCity(updatedCity);
        saveCollection();
        return new Response(true, "Город с id=" + req.getId() + " обновлён.", null);
    }

    /**
     * Обрабатывает команду удаления города по id.
     *
     * @param req запрос с id города для удаления
     * @return ответ с сообщением об успехе или ошибке
     */
    private Response handleRemoveById(RemoveByIdRequest req) {
        if (!collectionManager.containsId(req.getId())) {
            return new Response(false, "Элемент с id=" + req.getId() + " не найден.", null);
        }
        collectionManager.removeById(req.getId());
        saveCollection();
        return new Response(true, "Элемент с id=" + req.getId() + " удалён.", null);
    }

    /**
     * Обрабатывает команду удаления любого города с указанным carCode.
     *
     * @param req запрос с кодом автомобиля
     * @return ответ с сообщением об успехе или ошибке
     */
    private Response handleRemoveAnyByCarCode(RemoveAnyByCarCodeRequest req) {
        var city = collectionManager.getCities().stream()
                .filter(c -> c.getCarCode() == req.getCarCode())
                .findAny();
        if (city.isPresent()) {
            collectionManager.removeCity(city.get());
            saveCollection();
            return new Response(true, "Удалён город с id=" + city.get().getId(), null);
        } else {
            return new Response(false, "Город с carCode=" + req.getCarCode() + " не найден.", null);
        }
    }

    /**
     * Обрабатывает команду удаления всех городов, меньших заданного.
     *
     * @param req запрос с эталонным городом
     * @return ответ с количеством удалённых элементов
     */
    private Response handleRemoveLower(RemoveLowerRequest req) {
        int initialSize = collectionManager.size();
        collectionManager.getCities().removeIf(city -> city.compareTo(req.getReference()) < 0);
        int removed = initialSize - collectionManager.size();
        if (removed > 0) saveCollection();
        return new Response(true, "Удалено элементов: " + removed, null);
    }

    /**
     * Обрабатывает команду добавления города, если он больше всех существующих.
     *
     * @param req запрос с городом для добавления
     * @return ответ с сообщением об успехе или отказе
     */
    private Response handleAddIfMax(AddIfMaxRequest req) {
        City newCity = req.getCity();
        boolean isMax = collectionManager.getMaxCity()
                .map(max -> newCity.compareTo(max) > 0)
                .orElse(true);
        if (isMax) {
            newCity.setId(collectionManager.generateId());
            newCity.setCreationDate(LocalDate.now());
            collectionManager.addCity(newCity);
            saveCollection();
            return new Response(true, "Город добавлен с id=" + newCity.getId(), null);
        } else {
            return new Response(false, "Город не превышает максимальный. Добавление отменено.", null);
        }
    }

    /**
     * Обрабатывает команду очистки коллекции.
     *
     * @return ответ с сообщением об успехе
     */
    private Response handleClear() {
        collectionManager.clear();
        saveCollection();
        return new Response(true, "Коллекция очищена.", null);
    }

    /**
     * Обрабатывает команду получения информации о коллекции.
     *
     * @return ответ с типом коллекции, датой создания и количеством элементов
     */
    private Response handleInfo() {
        String info = "Тип коллекции: " + collectionManager.getCollectionType() +
                "\nДата инициализации: " + collectionManager.getInitializationDate() +
                "\nКоличество элементов: " + collectionManager.size();
        return new Response(true, info, null);
    }

    /**
     * Обрабатывает команду показа всех элементов коллекции.
     *
     * @return ответ со списком всех городов, отсортированных по естественному порядку
     */
    private Response handleShow() {
        if (collectionManager.size() == 0) {
            return new Response(true, "Коллекция пуста.", null);
        }
        List<City> sorted = collectionManager.getCities().stream()
                .sorted()
                .collect(Collectors.toList());
        return new Response(true, "Элементы коллекции:", sorted);
    }

    /**
     * Обрабатывает команду поиска города с минимальной координатой X.
     *
     * @return ответ с городом или сообщением, что коллекция пуста
     */
    private Response handleMinByCoordinates() {
        return collectionManager.getMinByCoordinates()
                .map(city -> new Response(true, "Город с минимальной координатой X:", city))
                .orElse(new Response(false, "Коллекция пуста.", null));
    }

    /**
     * Обрабатывает команду вывода высот над уровнем моря в порядке убывания.
     *
     * @return ответ со списком высот
     */
    private Response handlePrintFieldDescendingMetersAboveSeaLevel() {
        List<Integer> heights = collectionManager.getMetersAboveSeaLevelDescending();
        if (heights.isEmpty()) {
            return new Response(true, "Нет городов с указанной высотой (или коллекция пуста).", null);
        }
        return new Response(true, "Высоты в порядке убывания:", heights);
    }
}