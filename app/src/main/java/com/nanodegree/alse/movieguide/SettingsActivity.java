package com.nanodegree.alse.movieguide;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by aharyadi on 4/12/16.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        bindSummary(findPreference(getString(R.string.pref_sort_key)));

    }
    public void bindSummary(Preference preference){
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));

    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String stringValue = newValue.toString();
        ListPreference listPreference = (ListPreference)preference;
        int indexValue = listPreference.findIndexOfValue(stringValue);
        if(indexValue >= 0){
            listPreference.setSummary(listPreference.getEntries()[indexValue]);
        }
        return true;
    }

}
