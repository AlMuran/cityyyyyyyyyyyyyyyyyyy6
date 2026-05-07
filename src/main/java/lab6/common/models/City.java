package lab6.common.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class City implements Comparable<City>, Serializable {
    private long id;
    private String name;
    private Coordinates coordinates;
    private LocalDate creationDate;
    private Double area;
    private int population;
    private int metersAboveSeaLevel;
    private int carCode;
    private Climate climate;
    private StandardOfLiving standardOfLiving;
    private Human governor;


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


    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public int getPopulation() { return population; }
    public void setPopulation(int population) { this.population = population; }

    public int getMetersAboveSeaLevel() { return metersAboveSeaLevel; }
    public void setMetersAboveSeaLevel(int metersAboveSeaLevel) { this.metersAboveSeaLevel = metersAboveSeaLevel; }

    public int getCarCode() { return carCode; }
    public void setCarCode(int carCode) { this.carCode = carCode; }

    public Climate getClimate() { return climate; }
    public void setClimate(Climate climate) { this.climate = climate; }

    public StandardOfLiving getStandardOfLiving() { return standardOfLiving; }
    public void setStandardOfLiving(StandardOfLiving standardOfLiving) { this.standardOfLiving = standardOfLiving; }

    public Human getGovernor() { return governor; }
    public void setGovernor(Human governor) { this.governor = governor; }

    @Override
    public int compareTo(City other) {
        int nameCompare = this.name.compareTo(other.name);
        if (nameCompare != 0) return nameCompare;
        return Long.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City city = (City) o;
        return id == city.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

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