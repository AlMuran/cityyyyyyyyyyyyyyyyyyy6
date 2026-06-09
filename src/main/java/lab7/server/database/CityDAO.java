package lab7.server.database;

import lab7.common.models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CityDAO {

    public long insertCity(City city, long ownerId) {
        String sql = "INSERT INTO cities (name, coord_x, coord_y, creation_date, area, population, " +
                "meters_above_sea_level, car_code, climate, standard_of_living, governor_height, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, city.getName());
            stmt.setLong(2, city.getCoordinates().getX());
            stmt.setDouble(3, city.getCoordinates().getY());
            stmt.setObject(4, LocalDate.now());
            stmt.setDouble(5, city.getArea());
            stmt.setInt(6, city.getPopulation());
            stmt.setInt(7, city.getMetersAboveSeaLevel());
            stmt.setInt(8, city.getCarCode());
            stmt.setString(9, city.getClimate().name());
            stmt.setString(10, city.getStandardOfLiving().name());
            stmt.setObject(11, city.getGovernor().getHeight());
            stmt.setLong(12, ownerId);

            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public boolean updateCity(City city, long ownerId) {
        String sql = "UPDATE cities SET name=?, coord_x=?, coord_y=?, area=?, population=?, " +
                "meters_above_sea_level=?, car_code=?, climate=?, standard_of_living=?, governor_height=? " +
                "WHERE id=? AND user_id=?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, city.getName());
            stmt.setLong(2, city.getCoordinates().getX());
            stmt.setDouble(3, city.getCoordinates().getY());
            stmt.setDouble(4, city.getArea());
            stmt.setInt(5, city.getPopulation());
            stmt.setInt(6, city.getMetersAboveSeaLevel());
            stmt.setInt(7, city.getCarCode());
            stmt.setString(8, city.getClimate().name());
            stmt.setString(9, city.getStandardOfLiving().name());
            stmt.setObject(10, city.getGovernor().getHeight());
            stmt.setLong(11, city.getId());
            stmt.setLong(12, ownerId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean deleteCity(long cityId, long ownerId) {
        String sql = "DELETE FROM cities WHERE id=? AND user_id=?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, cityId);
            stmt.setLong(2, ownerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<City> loadAllCities() {
        List<City> cities = new ArrayList<>();
        String sql = "SELECT id, name, coord_x, coord_y, creation_date, area, population, " +
                "meters_above_sea_level, car_code, climate, standard_of_living, governor_height, user_id " +
                "FROM cities";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Coordinates coords = new Coordinates(
                        rs.getLong("coord_x"),
                        rs.getDouble("coord_y"));

                Human governor = new Human(
                        rs.getObject("governor_height") != null ? rs.getFloat("governor_height") : null);
                City city = new City(
                        rs.getLong("id"),
                        rs.getString("name"),
                        coords,
                        rs.getObject("creation_date", LocalDate.class),
                        rs.getDouble("area"),
                        rs.getInt("population"),
                        rs.getInt("meters_above_sea_level"),
                        rs.getInt("car_code"),
                        Climate.valueOf(rs.getString("climate")),
                        StandardOfLiving.valueOf(rs.getString("standard_of_living")),
                        governor,
                        rs.getLong("user_id"));
                cities.add(city);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }
}