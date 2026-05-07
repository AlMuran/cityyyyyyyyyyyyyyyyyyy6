package lab6.common.requests;

public class RemoveAnyByCarCodeRequest implements CommandRequest {
    private final int carCode;

    public RemoveAnyByCarCodeRequest(int carCode) {
        this.carCode = carCode;
    }

    public int getCarCode() {
        return carCode;
    }
}