package lab7.server.managers;

import lab7.common.models.City;
import lab7.server.database.CityDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Менеджер коллекции городов с синхронизацией через ReentrantLock.
 * @author AlMuran
 * @version 1.0
 */
public class CollectionManager {
    private static final Logger logger = LoggerFactory.getLogger(CollectionManager.class);
    private final ConcurrentHashMap<Long, City> cache = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final CityDAO cityDAO;
    private final LocalDate initializationDate;

    public CollectionManager() {
        this.cityDAO = new CityDAO();
        this.initializationDate = LocalDate.now();
        loadFromDB();
    }

    /** Захватывает блокировку для операций с коллекцией */
    public void lock() { lock.lock(); }

    /** Освобождает блокировку */
    public void unlock() { lock.unlock(); }

    private void loadFromDB() {
        List<City> cities = cityDAO.loadAllCities();
        for (City city : cities) {
            cache.put(city.getId(), city);
        }
    }

    /**
     * Добавляет город в коллекцию.
     * @param city город для добавления
     * @param ownerId ID владельца
     */
    public void addCity(City city, long ownerId) {
        long newId = cityDAO.insertCity(city, ownerId);
        if (newId == -1) {
            throw new RuntimeException("Не удалось добавить город в БД");
        }
        city.setId(newId);
        city.setOwnerId(ownerId);
        lock.lock();
        try {
            cache.put(newId, city);
            logger.info("Пользователь {} добавил город ID {}", ownerId, newId);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Обновляет город.
     * @param city город с новыми данными
     * @param ownerId ID владельца
     */
    public void updateCity(City city, long ownerId) {
        if (city.getId() <= 0) {
            throw new IllegalArgumentException("Город без ID");
        }

        lock.lock();
        try {
            City existing = cache.get(city.getId());
            if (existing == null) {
                throw new RuntimeException("Город с ID " + city.getId() + " не найден");
            }
            if (existing.getOwnerId() != ownerId) {
                throw new RuntimeException("Вы не можете редактировать этот город");
            }
        } finally {
            lock.unlock();
        }

        if (!cityDAO.updateCity(city, ownerId)) {
            throw new RuntimeException("Ошибка обновления города в БД");
        }

        lock.lock();
        try {
            city.setOwnerId(ownerId);
            cache.put(city.getId(), city);
            logger.info("Пользователь {} обновил город ID {}", ownerId, city.getId());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет город.
     * @param cityId ID города
     * @param ownerId ID владельца
     */
    public void removeCity(long cityId, long ownerId) {
        lock.lock();
        try {
            City existing = cache.get(cityId);
            if (existing == null) {
                throw new RuntimeException("Город с ID " + cityId + " не найден");
            }
            if (existing.getOwnerId() != ownerId) {
                throw new RuntimeException("Вы не можете удалить этот город");
            }
        } finally {
            lock.unlock();
        }

        if (!cityDAO.deleteCity(cityId, ownerId)) {
            throw new RuntimeException("Ошибка удаления города из БД");
        }

        lock.lock();
        try {
            cache.remove(cityId);
            logger.info("Пользователь {} удалил город ID {}", ownerId, cityId);
        } finally {
            lock.unlock();
        }
    }

    /** @return все города всех пользователей */
    public Collection<City> getAllCities() {
        lock.lock();
        try {
            return new ArrayList<>(cache.values());
        } finally {
            lock.unlock();
        }
    }

    /** @return общее количество городов */
    public int size() {
        lock.lock();
        try {
            return cache.size();
        } finally {
            lock.unlock();
        }
    }

    /** @return количество городов пользователя */
    public long getUserCitiesCount(long userId) {
        lock.lock();
        try {
            return cache.values().stream().filter(c -> c.getOwnerId() == userId).count();
        } finally {
            lock.unlock();
        }
    }

    /** @return дата инициализации коллекции */
    public LocalDate getInitializationDate() { return initializationDate; }

    /** @return тип коллекции */
    public String getCollectionType() { return "ConcurrentHashMap"; }

    /**
     * Очищает все города пользователя.
     * @return количество удалённых городов
     */
    public int clearUserCities(long userId) {
        List<Long> toRemove = new ArrayList<>();
        lock.lock();
        try {
            for (City city : cache.values()) {
                if (city.getOwnerId() == userId) {
                    toRemove.add(city.getId());
                }
            }
        } finally {
            lock.unlock();
        }

        int removed = 0;
        for (Long id : toRemove) {
            if (cityDAO.deleteCity(id, userId)) {
                lock.lock();
                try {
                    cache.remove(id);
                    removed++;
                } finally {
                    lock.unlock();
                }
            }
        }
        logger.info("Пользователь {} удалил {} своих городов", userId, removed);
        return removed;
    }

    /**
     * Удаляет города, меньшие заданного.
     * @param reference эталонный город
     * @param userId ID пользователя
     * @return количество удалённых городов
     */
    public int removeLowerThan(City reference, long userId) {
        List<Long> toRemove = new ArrayList<>();
        lock.lock();
        try {
            for (City city : cache.values()) {
                if (city.getOwnerId() == userId && city.compareTo(reference) < 0) {
                    toRemove.add(city.getId());
                }
            }
        } finally {
            lock.unlock();
        }

        int removed = 0;
        for (Long id : toRemove) {
            if (cityDAO.deleteCity(id, userId)) {
                lock.lock();
                try {
                    cache.remove(id);
                    removed++;
                } finally {
                    lock.unlock();
                }
            }
        }
        logger.info("Пользователь {} удалил {} городов (меньших заданного)", userId, removed);
        return removed;
    }

    /**
     * Добавляет город, если он больше максимального.
     * @param city город для добавления
     * @param userId ID пользователя
     * @return true если город добавлен
     */
    public boolean addIfMax(City city, long userId) {
        lock.lock();
        try {
            Optional<City> max = cache.values().stream()
                    .filter(c -> c.getOwnerId() == userId)
                    .max(City::compareTo);

            boolean isMax = !max.isPresent() || city.compareTo(max.get()) > 0;

            if (isMax) {
                long newId = cityDAO.insertCity(city, userId);
                if (newId != -1) {
                    city.setId(newId);
                    city.setOwnerId(userId);
                    cache.put(newId, city);
                    logger.info("Пользователь {} добавил максимальный город ID {}", userId, newId);
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет любой город с указанным carCode.
     * @param carCode код автомобиля
     * @param userId ID пользователя
     * @return ID удалённого города или null
     */
    public Long removeAnyByCarCode(int carCode, long userId) {
        Long toRemove = null;
        lock.lock();
        try {
            for (City city : cache.values()) {
                if (city.getOwnerId() == userId && city.getCarCode() == carCode) {
                    toRemove = city.getId();
                    break;
                }
            }
        } finally {
            lock.unlock();
        }

        if (toRemove != null && cityDAO.deleteCity(toRemove, userId)) {
            lock.lock();
            try {
                cache.remove(toRemove);
                logger.info("Пользователь {} удалил город с carCode={}, ID={}", userId, carCode, toRemove);
                return toRemove;
            } finally {
                lock.unlock();
            }
        }
        return null;
    }

    /** @return город с минимальной координатой X */
    public Optional<City> getMinByCoordinates() {
        lock.lock();
        try {
            return cache.values().stream()
                    .min(Comparator.comparing(c -> c.getCoordinates().getX()));
        } finally {
            lock.unlock();
        }
    }

    /** @return список высот в порядке убывания */
    public List<Integer> getMetersAboveSeaLevelDescending() {
        lock.lock();
        try {
            return cache.values().stream()
                    .map(City::getMetersAboveSeaLevel)
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }
}