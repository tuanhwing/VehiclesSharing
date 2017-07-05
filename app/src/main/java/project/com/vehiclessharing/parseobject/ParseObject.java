package project.com.vehiclessharing.parseobject;

import android.util.Log;

import com.google.gson.Gson;

import project.com.vehiclessharing.model.RequestFromGraber;
import project.com.vehiclessharing.model.RequestFromNeeder;
import project.com.vehiclessharing.model.User;

/**
 * Convert JSON to Java Object
 */

public class ParseObject {
    public static Gson gson = new Gson();
    private static String TAG = "convert_error";

    public static User jsonToUser(String userJson){
        User user = null;
        try{
            user = gson.fromJson(userJson,User.class);
        } catch (Exception e){
            Log.d(TAG,String.valueOf(e.getMessage()));
        }
        return user;
    }

    public static RequestFromGraber jsonToRequestGraber(String requestJson){
        RequestFromGraber requestFromGraber = null;
        try {
            requestFromGraber = gson.fromJson(requestJson,RequestFromGraber.class);
        } catch (Exception e){
            Log.d(TAG,String.valueOf(e.getMessage()));
        }
        return requestFromGraber;
    }

    public static RequestFromNeeder jsonToRequestNeeder(String requestJson){
        RequestFromNeeder requestFromNeeder = null;
        try {
            requestFromNeeder = gson.fromJson(requestJson,RequestFromNeeder.class);
        }catch (Exception e){
            Log.d(TAG,String.valueOf(e.getMessage()));
        }
        return requestFromNeeder;
    }
}
