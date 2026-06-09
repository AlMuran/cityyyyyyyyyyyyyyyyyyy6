package lab7.server.database;

import lab7.common.models.User;
import lab7.server.AuthManager;

import java.sql.*;
import java.util.Optional;

public class UserDAO {


    public Optional<User> findByLogin(String login) {
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getLong("id"),
                        rs.getString("login"),
                        rs.getString("password_hash")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public boolean registerUser(String login, String password) {
        String hashed = AuthManager.hashPassword(password);
        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, hashed);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {

            if (e.getSQLState().equals("23505")) {
                return false;
            }
            e.printStackTrace();
        }
        return false;
    }


    public boolean authenticate(String login, String password) {
        Optional<User> user = findByLogin(login);
        if (user.isPresent()) {
            String hashed = AuthManager.hashPassword(password);
            return hashed.equals(user.get().getPasswordHash());
        }
        return false;
    }


    public long getUserIdByLogin(String login) {
        String sql = "SELECT id FROM users WHERE login = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
