package lab7.common.requests;

public abstract class AuthenticatedRequest implements CommandRequest {
    protected String login;
    protected String password;

    protected AuthenticatedRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public void setLogin(String login) { this.login = login; }
    public void setPassword(String password) { this.password = password; }
}