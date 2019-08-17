package com.bookstore.source.entity;

public class User {

    private String firstname;
    private String lastname;
    private String address;
    private String email;
    private String userid;
    private String personalnumber;

    public User(String firstname, String lastname, String address, String email, String personalnumber, String userid) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.email = email;
        this.personalnumber = personalnumber;
        this.userid = userid;
    }

    public User() {
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

    public String getUserid() {
        return userid;
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

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String toString() {
        return "\tFirst name: " + this.firstname + " \n\tLast Name: " + this.lastname + " \n\tAddress: " + this.address + " \n\tEmail address:" + this.email + " \n\tPersonal number: " + this.personalnumber + " \n\tUser ID: " + this.userid;
    }
}
