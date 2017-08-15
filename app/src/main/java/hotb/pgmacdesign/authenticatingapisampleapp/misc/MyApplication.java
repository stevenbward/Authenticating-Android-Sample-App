package hotb.pgmacdesign.authenticatingapisampleapp.misc;

import android.content.Context;
import android.location.Location;
import android.support.multidex.MultiDexApplication;

/**
 * Created by pmacdowell on 2017-08-04.
 */

public class MyApplication extends MultiDexApplication {

    //Instance of the application
    private static MyApplication sInstance;
    //Context
    private static Context context;
    //Shared preferences wrapper class
    private static SharedPrefs sp;
    //Latitude and Longitude for location purposes
    private static double lastKnownLat, lastKnownLng;
    //Google Location object
    private static Location location;

    /**
     * Constructor
     */
    public MyApplication(){
        super();
    }

    /**
     * Get an instance of the application. This will cascade down and define/ initialize
     * other variables like context as well.
     * @return {@link MyApplication}
     */
    public static synchronized MyApplication getInstance(){
        if(sInstance == null) {
            sInstance = new MyApplication();
        }
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.sInstance = this;
        context = getContext();
        sp = getSharedPrefsInstance();
    }

    /**
     * Get context, if it is null, get an instance first and then return it
     * @return Context
     */
    public static synchronized Context getContext(){
        getInstance();
        if(context == null){
            MyApplication.context = getInstance().getApplicationContext();
        }
        return context;
    }

    /**
     * Build and return a shared prefs instance state.
     * @return {@link SharedPrefs}
     */
    public static synchronized SharedPrefs getSharedPrefsInstance(){
        getInstance();
        if(sp == null){
            sp = SharedPrefs.getSharedPrefsInstance(getContext(),
                    Constants.SHARED_PREFS_NAME);
        }
        return sp;
    }


}

