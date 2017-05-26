package project.com.vehiclessharing.utils;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;

import project.com.vehiclessharing.model.RequestFromNeeder;

/**
 * Created by Hihihehe on 5/25/2017.
 */

public interface RequestFromNeederCallback {
    void onSuccess(RequestFromNeeder requestFromNeeder);
    void onError(DatabaseError e);
}
