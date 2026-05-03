package lab6.common.requests;

public class RemoveByIdRequest implements CommandRequest {
    private final long id;

    public RemoveByIdRequest(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}