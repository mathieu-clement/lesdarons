package ch.eifr.lesdarons.pizzaorders.webservice.orm.entities;

import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Address;

import javax.persistence.*;

@Entity
@Table(name = "addresses")
public class AddressEntity implements Address {
    private long id;
    private String firstName;
    private String lastName;
    private String streetName;
    private String houseNumber; // can include letters, can be null
    private String postalCode;  // can include letters, can be null
    private String city;
    private String country;

    public AddressEntity() {
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    @Override
    @Column
    public String getFirstName() {
        return firstName;
    }

    @Override
    @Column
    public String getLastName() {
        return lastName;
    }

    @Override
    @Column
    public String getStreetName() {
        return streetName;
    }

    @Override
    @Column
    public String getHouseNumber() {
        return houseNumber;
    }

    @Override
    @Column
    public String getPostalCode() {
        return postalCode;
    }

    @Override
    @Column
    public String getCity() {
        return city;
    }

    @Override
    @Column
    public String getCountry() {
        return country;
    }
}
