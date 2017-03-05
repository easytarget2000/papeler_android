package org.eztarget.papeler;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by michelsievers on 23/01/2017.
 */

public class MyPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

//        Preference circlePreference = getPreferenceScreen().findPreference("numberOfCircles");

    }

}
