package com.bookstore.source.entity;

public class Admin {

    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String address;
    private String email;
    private String personalnumber;

    public Admin(String username, String password, String firstname, String lastname, String address, String email, String personalnumber) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.email = email;
        this.personalnumber = personalnumber;
    }

    public Admin() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPersonalnumber() {
        return personalnumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPersonalnumber(String personalNumber) {
        this.personalnumber = personalNumber;
    }

    public String toString() {
        return "\tFirst name: " + this.firstname + " \n\tLast Name: " + this.lastname + " \n\tAddress: " + this.address + " \n\tEmail address:" + this.email + " \n\tPersonal number: " + this.personalnumber;
    }

}
