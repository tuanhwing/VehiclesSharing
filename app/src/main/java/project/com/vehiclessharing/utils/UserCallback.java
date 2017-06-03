package project.com.vehiclessharing.utils;

import com.google.firebase.database.DatabaseError;

import project.com.vehiclessharing.model.RequestFromGraber;
import project.com.vehiclessharing.model.User;

/**
 * Created by Hihihehe on 5/30/2017.
 */

public interface UserCallback {
    void onSuccess(User user);
    void onError(DatabaseError e);
}
