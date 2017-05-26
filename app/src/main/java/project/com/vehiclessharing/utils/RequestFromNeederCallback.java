package project.com.vehiclessharing.utils;

import com.google.firebase.database.DatabaseError;

import project.com.vehiclessharing.model.RequestFromNeeder;

/**
 * Created by Tuan on 25/05/2017.
 */

public interface RequestFromNeederCallback {
    void onSuccess(RequestFromNeeder requestFromNeeder);
    void onError(DatabaseError e);
}
