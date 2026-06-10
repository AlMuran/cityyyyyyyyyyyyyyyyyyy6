package lab7.common.requests;
/**
 * Базовый абстрактный класс для всех запросов, требующих аутентификации.
 * Содержит логин и пароль пользователя.
 * @author AlMuran
 * @version 1.0
 */
public abstract class AuthenticatedRequest implements CommandRequest {
    protected String login;
    protected String password;

    protected AuthenticatedRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /** @return логин пользователя */
    public String getLogin() { return login; }

    /** @return пароль пользователя */
    public String getPassword() { return password; }

    /** @param login новый логин */
    public void setLogin(String login) { this.login = login; }

    /** @param password новый пароль */
    public void setPassword(String password) { this.password = password; }
}