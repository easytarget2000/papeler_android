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

    static PreferenceAccess with(@NonNull Context context) {
        if (instance == null) {
            instance = new PreferenceAccess(context);
        }
        return instance;
    }

    boolean getAndUnsetIsFirstTime() {
        final boolean openedBefore = mPrefs.getBoolean("OPENED", false);
        if (!openedBefore) {
            edit("OPENED", true);
        }
        return !openedBefore;
    }

    void setHasBackgroundImage(final boolean hasNewImage) {
        edit("HAS_IMAGE", hasNewImage);
        if (hasNewImage) {
            queueNewBackgroundImage();
        }
    }

    boolean hasBackgroundImage() {
        return mPrefs.getBoolean("HAS_IMAGE", false);
    }

    void acknowledgeNewBackgroundImage() {
        edit("NEW_IMAGE", false);
    }

    void queueNewBackgroundImage() {
        edit("NEW_IMAGE", true);
    }

    boolean hasNewBackgroundImage() {
        return mPrefs.getBoolean("NEW_IMAGE", false);
    }

    private void edit(final String key, final boolean value) {
        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void edit(final String key, final String value) {
        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
