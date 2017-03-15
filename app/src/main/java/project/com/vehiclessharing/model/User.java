package project.com.vehiclessharing.model;

/**
 * Created by Tuan on 13/03/2017.
 */

public class User {
    private String fullName, phoneNumber, sex, email;
    public User(String fullName, String email, String phoneNumber, String sex) {
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public User() {

    }
}
