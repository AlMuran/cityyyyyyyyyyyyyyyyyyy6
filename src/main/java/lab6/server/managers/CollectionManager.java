package lab6.server.managers;

import lab6.common.models.City;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private final HashSet<City> cities = new HashSet<>();
    private final LocalDate initializationDate;
    private long nextId = 1;

    public CollectionManager() {
        this.initializationDate = LocalDate.now();
    }

    public void addCity(City city) {
        cities.add(city);
    }

    public void removeCity(City city) {
        cities.remove(city);
    }

    public void removeById(long id) {
        cities.removeIf(c -> c.getId() == id);
    }

    public Optional<City> getById(long id) {
        return cities.stream().filter(c -> c.getId() == id).findFirst();
    }

    public Optional<City> getMinByCoordinates() {
        return cities.stream().min(Comparator.comparing(c -> c.getCoordinates().getX()));
    }

    public void clear() {
        cities.clear();
    }

    public boolean containsId(long id) {
        return cities.stream().anyMatch(c -> c.getId() == id);
    }

    public long generateId() {
        while (containsId(nextId)) {
            nextId++;
        }
        return nextId++;
    }

    public HashSet<City> getCities() {
        return cities;
    }

    public LocalDate getInitializationDate() {
        return initializationDate;
    }

    public int size() {
        return cities.size();
    }

    public String getCollectionType() {
        return cities.getClass().getName();
    }

    public List<Integer> getMetersAboveSeaLevelDescending() {
        return cities.stream()
                .map(City::getMetersAboveSeaLevel)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public Optional<City> getMaxCity() {
        return cities.stream().max(Comparator.naturalOrder());
    }
}