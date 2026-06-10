package lab7.server.database;

import lab7.common.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с городами в базе данных.
 * @author AlMuran
 * @version 1.0
 */
public class CityDAO {
    private static final Logger logger = LoggerFactory.getLogger(CityDAO.class);

    /**
     * Вставляет новый город в БД.
     * @param city город для вставки
     * @param ownerId ID владельца
     * @return сгенерированный ID или -1 в случае ошибки
     */
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

            Float height = city.getGovernor().getHeight();
            if (height != null) {
                stmt.setFloat(11, height);
            } else {
                stmt.setNull(11, Types.REAL);
            }
            stmt.setLong(12, ownerId);

            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    logger.debug("Вставлен город с ID: {}", id);
                    return id;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка вставки города: {}", e.getMessage());
        }
        return -1;
    }

    /**
     * Обновляет существующий город в БД.
     * @param city город с новыми данными
     * @param ownerId ID владельца (для проверки прав)
     * @return true если обновление успешно
     */
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

            Float height = city.getGovernor().getHeight();
            if (height != null) {
                stmt.setFloat(10, height);
            } else {
                stmt.setNull(10, Types.REAL);
            }
            stmt.setLong(11, city.getId());
            stmt.setLong(12, ownerId);

            int updated = stmt.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            logger.error("Ошибка обновления города: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Удаляет город из БД.
     * @param cityId ID города
     * @param ownerId ID владельца (для проверки прав)
     * @return true если удаление успешно
     */
    public boolean deleteCity(long cityId, long ownerId) {
        String sql = "DELETE FROM cities WHERE id=? AND user_id=?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, cityId);
            stmt.setLong(2, ownerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления города: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Загружает все города из БД.
     * @return список всех городов
     */
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
            logger.info("Загружено {} городов из БД", cities.size());
        } catch (SQLException e) {
            logger.error("Ошибка загрузки городов: {}", e.getMessage());
        }
        return cities;
    }
}