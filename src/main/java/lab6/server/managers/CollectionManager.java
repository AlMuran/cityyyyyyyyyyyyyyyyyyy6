package lab6.server.managers;

import lab6.common.models.City;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Управляет коллекцией городов на сервере.
 *
 * <p>Класс инкапсулирует работу с коллекцией {@link HashSet} городов,
 * предоставляя методы для добавления, удаления, поиска и получения информации.</p>
 *
 * <p>Особенности:
 * <ul>
 *   <li>Автоматическая генерация уникальных id для новых городов</li>
 *   <li>Фиксация даты инициализации менеджера</li>
 *   <li>Методы для поиска минимального по координатам</li>
 *   <li>Методы для получения отсортированных значений высот</li>
 * </ul>
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see City
 * @see FileManager
 */
public class CollectionManager {

    /** Коллекция городов (используется HashSet для уникальности по id) */
    private final HashSet<City> cities = new HashSet<>();

    /** Дата инициализации менеджера (время запуска сервера) */
    private final LocalDate initializationDate;

    /** Следующий доступный id для нового города */
    private long nextId = 1;

    /**
     * Создаёт менеджер коллекции.
     * <p>Устанавливает дату инициализации на текущую дату.</p>
     */
    public CollectionManager() {
        this.initializationDate = LocalDate.now();
    }

    /**
     * Добавляет город в коллекцию.
     *
     * @param city город для добавления
     */
    public void addCity(City city) {
        cities.add(city);
    }

    /**
     * Удаляет город из коллекции.
     *
     * @param city город для удаления
     */
    public void removeCity(City city) {
        cities.remove(city);
    }

    /**
     * Удаляет город по идентификатору.
     *
     * @param id идентификатор города для удаления
     */
    public void removeById(long id) {
        cities.removeIf(c -> c.getId() == id);
    }

    /**
     * Возвращает город по идентификатору.
     *
     * @param id идентификатор города
     * @return Optional с городом, если найден, иначе пустой Optional
     */
    public Optional<City> getById(long id) {
        return cities.stream().filter(c -> c.getId() == id).findFirst();
    }

    /**
     * Возвращает город с минимальной координатой X.
     *
     * @return Optional с городом, если коллекция не пуста
     */
    public Optional<City> getMinByCoordinates() {
        return cities.stream().min(Comparator.comparing(c -> c.getCoordinates().getX()));
    }

    /**
     * Очищает коллекцию (удаляет все города).
     */
    public void clear() {
        cities.clear();
    }

    /**
     * Проверяет, существует ли город с указанным id.
     *
     * @param id идентификатор для проверки
     * @return true, если город существует
     */
    public boolean containsId(long id) {
        return cities.stream().anyMatch(c -> c.getId() == id);
    }

    /**
     * Генерирует новый уникальный идентификатор.
     * <p>Проверяет, не занят ли следующий id, при необходимости увеличивает.</p>
     *
     * @return новый уникальный id
     */
    public long generateId() {
        while (containsId(nextId)) {
            nextId++;
        }
        return nextId++;
    }

    /**
     * Возвращает все города коллекции.
     *
     * @return HashSet всех городов
     */
    public HashSet<City> getCities() {
        return cities;
    }

    /**
     * Возвращает дату инициализации менеджера.
     *
     * @return дата создания менеджера
     */
    public LocalDate getInitializationDate() {
        return initializationDate;
    }

    /**
     * Возвращает количество городов в коллекции.
     *
     * @return размер коллекции
     */
    public int size() {
        return cities.size();
    }

    /**
     * Возвращает тип коллекции (для команды info).
     *
     * @return полное имя класса коллекции
     */
    public String getCollectionType() {
        return cities.getClass().getName();
    }

    /**
     * Возвращает список высот над уровнем моря в порядке убывания.
     *
     * @return список высот (metersAboveSeaLevel) всех городов, отсортированный по убыванию
     */
    public List<Integer> getMetersAboveSeaLevelDescending() {
        return cities.stream()
                .map(City::getMetersAboveSeaLevel)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает максимальный город в коллекции.
     *
     * @return Optional с максимальным городом (согласно compareTo)
     */
    public Optional<City> getMaxCity() {
        return cities.stream().max(Comparator.naturalOrder());
    }
}