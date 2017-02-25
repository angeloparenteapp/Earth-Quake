package com.angeloparenteapp.earthquake;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        final int minPort = 1;
        final int maxPort = 19999;

        final int minPortMag = 1;
        final int maxPortMag = 10;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMagnitude);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference limit = findPreference(getString(R.string.settings_earthquakes_displayed_key));
            bindPreferenceSummaryToValue(limit);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");

            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;

                int prefIndex = listPreference.findIndexOfValue(stringValue);

                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                if (preference.getKey().equals("min_magnitude")) {
                    int val = Integer.parseInt(newValue.toString());
                    if ((val >= minPortMag) && (val <= maxPortMag)) {
                        preference.setSummary(stringValue);
                        return true;
                    } else {
                        // invalid you can show invalid message
                        Toast.makeText(getActivity(), "Min 1 max 10", Toast.LENGTH_LONG).show();
                        return false;
                    }

                } else if (preference.getKey().equals("numb_earthquakes")) {
                    int val = Integer.parseInt(newValue.toString());
                    if ((val >= minPort) && (val <= maxPort)) {
                        preference.setSummary(stringValue);
                        return true;
                    } else {
                        // invalid you can show invalid message
                        Toast.makeText(getActivity(), "Min 1 max 19999", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            }
            return true;
        }
    }
}