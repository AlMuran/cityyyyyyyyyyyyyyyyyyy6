package lab6.common.requests;

import lab6.common.models.City;

public class UpdateRequest implements CommandRequest {
    private final long id;
    private final City city;

    public UpdateRequest(long id, City city) {
        this.id = id;
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public City getCity() {
        return city;
    }
}