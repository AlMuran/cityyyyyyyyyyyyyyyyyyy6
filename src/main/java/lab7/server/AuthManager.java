package lab7.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Утилитарный класс для хэширования паролей алгоритмом SHA-224.
 * @author AlMuran
 * @version 1.0
 */
public class AuthManager {

    /**
     * Хэширует пароль алгоритмом SHA-224.
     * @param password пароль в открытом виде
     * @return хэш пароля в виде шестнадцатеричной строки
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-224");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-224 не поддерживается", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
