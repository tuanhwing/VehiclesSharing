package project.com.vehiclessharing.application;

import android.app.Application;
import android.app.Notification;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Tuan on 09/05/2017.
 */

public class ApplicationController extends Application {
    /**
     * Common shared preference.
     */
    public static SharedPreferences sharedPreferences;
    private static ApplicationController sInstance;//A singleton instance of the application class for easy access in other places
    public static ArrayList<Notification> listNotification = new ArrayList<>();//Storage notification when

    @Override
    public void onCreate() {
        super.onCreate();
        // initialize sharedPreferences instance
        sharedPreferences = getSharedPreferences( getPackageName() + "_storage", MODE_PRIVATE);

        Realm.init(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);

        // initialize the singleton
        sInstance = this;
    }

    /**
     * @return co.pixelmatter.meme.ApplicationController singleton instance
     */
    public static synchronized ApplicationController getInstance() {
        return sInstance;
    }


}
