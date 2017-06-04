package project.com.vehiclessharing.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tuan on 09/05/2017.
 */

public class UserOnDevice extends RealmObject{
    @PrimaryKey
    private String userId;

    private InformationUserOnDivce user;

    public UserOnDevice() {
    }

    public UserOnDevice(String userId, InformationUserOnDivce user) {
        this.userId = userId;
        this.user = user;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public InformationUserOnDivce getUser() {
        return user;
    }

    public void setUser(InformationUserOnDivce user) {
        this.user = user;
    }
}
