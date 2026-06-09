package lab7.common.requests;

public class RegisterRequest extends AuthenticatedRequest {
    public RegisterRequest(String login, String password) {
        super(login, password);
    }
}