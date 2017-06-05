package project.com.vehiclessharing.utils;

import com.google.firebase.database.DatabaseError;

import java.util.concurrent.ExecutionException;

import project.com.vehiclessharing.model.RequestFromGraber;
import project.com.vehiclessharing.model.User;

/**
 * Created by Hihihehe on 5/30/2017.
 */

public interface UserCallback {
    void onSuccess(User user) throws ExecutionException, InterruptedException;
    void onError(DatabaseError e);
}
