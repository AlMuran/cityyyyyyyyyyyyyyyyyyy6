package lab7.server.managers;

import lab7.common.models.City;
import lab7.server.database.CityDAO;
import lab7.server.database.DatabaseManager;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class CollectionManager {
    private final ConcurrentHashMap<Long, City> cache = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final CityDAO cityDAO;
    private final LocalDate initializationDate;

    public CollectionManager() {
        this.cityDAO = new CityDAO();
        this.initializationDate = LocalDate.now();
        loadFromDB();
    }

    private void loadFromDB() {
        List<City> cities = cityDAO.loadAllCities();
        for (City city : cities) {
            cache.put(city.getId(), city);
        }
    }

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
        } finally {
            lock.unlock();
        }
    }

    public void updateCity(City city, long ownerId) {
        if (city.getId() <= 0) throw new IllegalArgumentException("Город без id");
        if (!cityDAO.updateCity(city, ownerId)) {
            throw new RuntimeException("Ошибка обновления города в БД (возможно, не ваш город)");
        }

        lock.lock();
        try {
            cache.put(city.getId(), city);
        } finally {
            lock.unlock();
        }
    }

    public void removeCity(long cityId, long ownerId) {
        if (!cityDAO.deleteCity(cityId, ownerId)) {
            throw new RuntimeException("Ошибка удаления из БД (город не найден или не ваш)");
        }
        lock.lock();
        try {
            cache.remove(cityId);
        } finally {
            lock.unlock();
        }
    }

    public Optional<City> getById(long id) {
        return Optional.ofNullable(cache.get(id));
    }

    public Collection<City> getAllCities() {
        return cache.values();
    }

    public int size() { return cache.size(); }
    public LocalDate getInitializationDate() { return initializationDate; }
    public String getCollectionType() { return "ConcurrentHashMap"; }


    public Optional<City> getMinByCoordinates() {
        return cache.values().stream()
                .min(Comparator.comparing(c -> c.getCoordinates().getX()));
    }

    public List<Integer> getMetersAboveSeaLevelDescending() {
        return cache.values().stream()
                .map(City::getMetersAboveSeaLevel)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }
}