package lab6.common.requests;

import lab6.common.models.City;

public class RemoveLowerRequest implements CommandRequest {
    private final City reference;

    public RemoveLowerRequest(City reference) {
        this.reference = reference;
    }

    public City getReference() {
        return reference;
    }
}