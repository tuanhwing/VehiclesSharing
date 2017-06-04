package project.com.vehiclessharing.utils;

import com.google.firebase.database.DatabaseError;

import project.com.vehiclessharing.model.RequestFromGraber;

/**
 * Created by Tuan on 24/05/2017.
 */

public interface RequestFromGraberCallback {
    void onSuccess(RequestFromGraber requestFromGraber);
    void onError(DatabaseError e);
}
