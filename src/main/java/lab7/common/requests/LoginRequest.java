package lab7.common.requests;

public class LoginRequest extends AuthenticatedRequest {
    public LoginRequest(String login, String password) {
        super(login, password);
    }
}