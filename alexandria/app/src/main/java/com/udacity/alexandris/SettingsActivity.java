package com.udacity.alexandris;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


import com.udacity.alexandria.R;
/**
 * Created by saj on 27/01/15.
 */
public class SettingsActivity extends PreferenceActivity  implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int position = Integer.parseInt(prefs.getString("pref_startFragment", "0"));
        prefs.edit().putInt(MainActivity.STATE_SELECTED_POSITION,position);
        return true;
    }
}
