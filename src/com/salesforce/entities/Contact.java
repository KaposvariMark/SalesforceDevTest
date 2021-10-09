package com.salesforce.entities;

public class Contact {

    private String id;

    private String accountID;

    private String firstName;

    private String lastName;

    public Contact(String id, String accountID, String firstName, String lastName) {
        this.id = id;
        this.accountID = accountID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
