package project.com.vehiclessharing.utils;

import android.graphics.Bitmap;

import com.google.firebase.database.DatabaseError;

import project.com.vehiclessharing.model.User;

/**
 * Created by Hihihehe on 6/3/2017.
 */

public interface MarKerBitmapCallBack {
    void onSuccess(Bitmap bitmap);
    void onError(Exception e);
}
