package project.com.vehiclessharing.model;

import io.realm.RealmObject;

/**
 * Created by Tuan on 09/05/2017.
 */

public class BirthdayOnDevice extends RealmObject {
    int day;
    int month;
    int year;

    public BirthdayOnDevice() {
    }

    public BirthdayOnDevice(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
