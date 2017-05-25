package project.com.vehiclessharing.utils;

import com.google.firebase.database.DatabaseError;

import project.com.vehiclessharing.model.RequestFromGraber;
import project.com.vehiclessharing.model.RequestFromNeeder;

/**
 * Created by Hihihehe on 5/24/2017.
 */

public interface RequestFromGraberCallback {
    void onSuccess(RequestFromGraber requestFromGraber);
    void onError(DatabaseError e);
}
