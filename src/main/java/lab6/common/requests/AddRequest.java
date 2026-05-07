package lab6.common.requests;

import lab6.common.models.City;

public class AddRequest implements CommandRequest {
    private final City city;

    public AddRequest(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }
}