package lab7.common.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Представляет город со всеми его характеристиками для хранения в коллекции.
 * <p>
 * Класс содержит информацию о городе: уникальный идентификатор, название,
 * координаты, дату создания, площадь, население, высоту над уровнем моря,
 * код автомобиля, климат, уровень жизни и губернатора.
 * </p>
 * <p>
 * Города сравниваются сначала по названию, затем по id.
 * </p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see Coordinates
 * @see Human
 * @see Climate
 * @see StandardOfLiving
 */
public class City implements Comparable<City>, Serializable {

    /** Уникальный идентификатор города */
    private long id;

    /** Название города (не может быть пустым) */
    private String name;

    /** Координаты города на карте */
    private Coordinates coordinates;

    /** Дата создания записи о городе */
    private LocalDate creationDate;

    /** Площадь города в квадратных километрах (должна быть > 0) */
    private Double area;

    /** Численность населения города (должна быть > 0) */
    private int population;

    /** Высота города над уровнем моря в метрах */
    private int metersAboveSeaLevel;

    /** Код автомобиля региона (от 1 до 1000) */
    private int carCode;

    /** Тип климата города */
    private Climate climate;

    /** Уровень жизни в городе */
    private StandardOfLiving standardOfLiving;

    /** Губернатор города */
    private Human governor;

    private long ownerId;

    /**
     * Создаёт новый город без указания id и даты создания.
     * <p>
     * Этот конструктор используется клиентом при добавлении нового города.
     * Id и дата создания будут назначены сервером автоматически.
     * </p>
     *
     * @param name название города (не может быть null или пустым)
     * @param coordinates координаты города (не может быть null)
     * @param area площадь города (должна быть > 0)
     * @param population население города (должно быть > 0)
     * @param metersAboveSeaLevel высота над уровнем моря
     * @param carCode код автомобиля (от 1 до 1000)
     * @param climate климат из перечисления Climate
     * @param standardOfLiving уровень жизни из перечисления StandardOfLiving
     * @param governor губернатор города (может быть null)
     */
    public City(String name, Coordinates coordinates, Double area, int population,
                int metersAboveSeaLevel, int carCode, Climate climate,
                StandardOfLiving standardOfLiving, Human governor) {
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        this.population = population;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.carCode = carCode;
        this.climate = climate;
        this.standardOfLiving = standardOfLiving;
        this.governor = governor;
    }

    /**
     * Создаёт город с указанием всех полей, включая id и дату создания.
     * <p>
     * Этот конструктор используется сервером при загрузке городов из файла.
     * </p>
     *
     * @param id уникальный идентификатор города
     * @param name название города
     * @param coordinates координаты города
     * @param creationDate дата создания записи
     * @param area площадь города
     * @param population население города
     * @param metersAboveSeaLevel высота над уровнем моря
     * @param carCode код автомобиля
     * @param climate климат
     * @param standardOfLiving уровень жизни
     * @param governor губернатор
     */
    public City(long id, String name, Coordinates coordinates, LocalDate creationDate,
                Double area, int population, int metersAboveSeaLevel, int carCode,
                Climate climate, StandardOfLiving standardOfLiving, Human governor) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.area = area;
        this.population = population;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.carCode = carCode;
        this.climate = climate;
        this.standardOfLiving = standardOfLiving;
        this.governor = governor;
    }

    public City(long id, String name, Coordinates coordinates, LocalDate creationDate,
                Double area, int population, int metersAboveSeaLevel, int carCode,
                Climate climate, StandardOfLiving standardOfLiving, Human governor,
                long ownerId) {
        this(id, name, coordinates, creationDate, area, population, metersAboveSeaLevel, carCode, climate, standardOfLiving, governor);
        this.ownerId = ownerId;
    }
    /**
     * Возвращает уникальный идентификатор города.
     *
     * @return id города
     */
    public long getId() { return id; }

    /**
     * Устанавливает уникальный идентификатор города.
     *
     * @param id новый идентификатор
     */
    public void setId(long id) { this.id = id; }

    /**
     * Возвращает название города.
     *
     * @return название города
     */
    public String getName() { return name; }

    /**
     * Устанавливает название города.
     *
     * @param name новое название
     */
    public void setName(String name) { this.name = name; }

    /**
     * Возвращает координаты города.
     *
     * @return объект Coordinates с координатами
     */
    public Coordinates getCoordinates() { return coordinates; }

    /**
     * Устанавливает координаты города.
     *
     * @param coordinates новые координаты
     */
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    /**
     * Возвращает дату создания записи о городе.
     *
     * @return дата создания
     */
    public LocalDate getCreationDate() { return creationDate; }

    /**
     * Устанавливает дату создания записи о городе.
     *
     * @param creationDate новая дата
     */
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    /**
     * Возвращает площадь города.
     *
     * @return площадь в кв. км
     */
    public Double getArea() { return area; }

    /**
     * Устанавливает площадь города.
     *
     * @param area новая площадь
     */
    public void setArea(Double area) { this.area = area; }

    /**
     * Возвращает население города.
     *
     * @return количество жителей
     */
    public int getPopulation() { return population; }

    /**
     * Устанавливает население города.
     *
     * @param population новое население
     */
    public void setPopulation(int population) { this.population = population; }

    /**
     * Возвращает высоту города над уровнем моря.
     *
     * @return высота в метрах
     */
    public int getMetersAboveSeaLevel() { return metersAboveSeaLevel; }

    /**
     * Устанавливает высоту города над уровнем моря.
     *
     * @param metersAboveSeaLevel новая высота
     */
    public void setMetersAboveSeaLevel(int metersAboveSeaLevel) { this.metersAboveSeaLevel = metersAboveSeaLevel; }

    /**
     * Возвращает код автомобиля региона.
     *
     * @return код автомобиля (1-1000)
     */
    public int getCarCode() { return carCode; }

    /**
     * Устанавливает код автомобиля региона.
     *
     * @param carCode новый код
     */
    public void setCarCode(int carCode) { this.carCode = carCode; }

    /**
     * Возвращает тип климата города.
     *
     * @return климат из перечисления Climate
     */
    public Climate getClimate() { return climate; }

    /**
     * Устанавливает тип климата города.
     *
     * @param climate новый климат
     */
    public void setClimate(Climate climate) { this.climate = climate; }

    /**
     * Возвращает уровень жизни в городе.
     *
     * @return уровень жизни из перечисления StandardOfLiving
     */
    public StandardOfLiving getStandardOfLiving() { return standardOfLiving; }

    /**
     * Устанавливает уровень жизни в городе.
     *
     * @param standardOfLiving новый уровень жизни
     */
    public void setStandardOfLiving(StandardOfLiving standardOfLiving) { this.standardOfLiving = standardOfLiving; }

    /**
     * Возвращает губернатора города.
     *
     * @return объект Human с информацией о губернаторе
     */
    public Human getGovernor() { return governor; }

    /**
     * Устанавливает губернатора города.
     *
     * @param governor новый губернатор
     */
    public void setGovernor(Human governor) { this.governor = governor; }

    public long getOwnerId() { return ownerId; }
    public void setOwnerId(long ownerId) { this.ownerId = ownerId; }
    /**
     * Сравнивает два города.
     * <p>
     * Сравнение происходит сначала по названию (лексикографически),
     * затем по id, если названия равны.
     * </p>
     *
     * @param other город для сравнения
     * @return отрицательное число, если текущий город меньше,
     *         положительное, если больше, 0 если равны
     */
    @Override
    public int compareTo(City other) {
        int nameCompare = this.name.compareTo(other.name);
        if (nameCompare != 0) return nameCompare;
        return Long.compare(this.id, other.id);
    }

    /**
     * Проверяет, равны ли два города.
     * <p>
     * Города считаются равными, если у них одинаковый id.
     * </p>
     *
     * @param o объект для сравнения
     * @return true если города имеют одинаковый id, иначе false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City city = (City) o;
        return id == city.id;
    }

    /**
     * Возвращает хеш-код города на основе его id.
     *
     * @return хеш-код города
     */
    @Override
    public int hashCode() { return Objects.hash(id); }

    /**
     * Возвращает строковое представление города.
     * <p>
     * Строка содержит все поля города в удобочитаемом формате.
     * </p>
     *
     * @return строковое представление города
     */
    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", area=" + area +
                ", population=" + population +
                ", metersAboveSeaLevel=" + metersAboveSeaLevel +
                ", carCode=" + carCode +
                ", climate=" + climate +
                ", standardOfLiving=" + standardOfLiving +
                ", governor=" + governor +
                '}';
    }
}