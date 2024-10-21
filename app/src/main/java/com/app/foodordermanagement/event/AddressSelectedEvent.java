package com.app.foodordermanagement.event;

import com.app.foodordermanagement.model.Address;

public class AddressSelectedEvent {

    private Address address;

    public AddressSelectedEvent(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
