package project.com.vehiclessharing.model;

import java.io.Serializable;

/**
 * Created by Hihihehe on 6/6/2017.
 */

public class HistoryOfGraber implements Serializable {
    private String userId;//userId of needer
    private String sourceLocation;
    private String destinationLocation;
    private String day;
    private int rating;
    private String comment;

    public HistoryOfGraber() {
    }

    public HistoryOfGraber(String userId, String sourceLocation, String destinationLocation, String day, int rating, String comment) {
        this.userId = userId;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.day = day;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
