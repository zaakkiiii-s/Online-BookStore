package model;

public class Customer {
    private int customerID;
    private String nameSurname;
    private String email;
    private String phoneNumber;

    public Customer(int customerID, String nameSurname, String email, String phoneNumber) {
        this.customerID = customerID;
        this.nameSurname = nameSurname;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getCustomerID() { return customerID; }
    public String getNameSurname() { return nameSurname; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    @Override
    public String toString() {
        return customerID + ": " + nameSurname + " | " + email + " | " + phoneNumber;
    }
}

