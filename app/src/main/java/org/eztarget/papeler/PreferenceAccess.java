package org.eztarget.papeler;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Created by michelsievers on 25/02/2017.
 *
 */

public class PreferenceAccess {

    private static PreferenceAccess instance = null;

//    private Context mAppContext;

    private SharedPreferences mPrefs;

    private PreferenceAccess(@NonNull Context context) {
//        mAppContext = context.getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceAccess with(@NonNull Context context) {
        if (instance == null) {
            instance = new PreferenceAccess(context);
        }
        return instance;
    }

    public boolean getAndUnsetIsFirstTime() {
        final boolean openedBefore = mPrefs.getBoolean("OPENED", false);
        if (!openedBefore) {
            edit("OPENED", true);
        }
        return !openedBefore;
    }

    private void edit(final String key, final boolean value) {
        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
