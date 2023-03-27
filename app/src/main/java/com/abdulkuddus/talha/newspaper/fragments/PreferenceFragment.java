package com.abdulkuddus.talha.newspaper.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.abdulkuddus.talha.newspaper.R;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class PreferenceFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_main, null);

        // Find the country preference using it's key and set it's summary.
        Preference countries = findPreference(getString(R.string.pref_key_country));
        bindPreferenceSummaryToValue(countries);

        // Find the maxResults preference using it's key and set it's summary.
        //TODO Use string resources for this
        Preference sources = findPreference(getString(R.string.pref_key_sources));
        bindPreferenceSummaryToValue(sources);
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set this fragment as the OnPreferenceChangeListener.
        preference.setOnPreferenceChangeListener(this);

        // Get the SharedPreferences.
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(preference.getContext());

        // Get the current value of the given preference.
        String preferenceString = sharedPreferences.getString(preference.getKey(), "");

        // Set the summary preference to the value we got.
        onPreferenceChange(preference, preferenceString);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {

            // Cast the preference into a ListPreference (if it is a ListPreference)
            ListPreference listPreference = (ListPreference) preference;

            // Find the index (position) of the entry that was chosen.
            int prefIndex = listPreference.findIndexOfValue(newValue.toString());

            // If it is a valid index, get the list of labels the preference has and set the
            // summary to be the correct label.
            if (prefIndex >= 0) {
                CharSequence[] labels = listPreference.getEntries();
                preference.setSummary(labels[prefIndex]);
            }

        } else {
            if (!newValue.toString().equals("")) preference.setSummary(newValue.toString());
        }
        return true;
    }
}