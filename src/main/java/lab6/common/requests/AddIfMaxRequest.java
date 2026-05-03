package lab6.common.requests;

import lab6.common.models.City;

public class AddIfMaxRequest implements CommandRequest {
    private final City city;

    public AddIfMaxRequest(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }
}