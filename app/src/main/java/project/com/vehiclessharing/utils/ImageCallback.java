package project.com.vehiclessharing.utils;

/**
 * Created by Tuan on 29/05/2017.
 */

public interface ImageCallback {
    void onSuccess(String url);
    void onError(Exception e);
}
