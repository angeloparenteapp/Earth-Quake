package com.angeloparenteapp.earthquake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme();

        setContentView(R.layout.settings_activity);
    }

    public void setTheme(){
        SharedPreferences themePreference = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = themePreference.getString(getString(R.string.theme_picker_key),
                getString(R.string.theme_picker_default));

        if (theme.equals("Light")) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            final Preference date = findPreference(getString(R.string.date_picker_key));
            bindPreferenceSummaryToValue(date);

            final SwitchPreference myLocation = (SwitchPreference) findPreference(getString(R.string.use_my_location_key));
            myLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (myLocation.isChecked()) {
                        date.setEnabled(false);
                        return true;
                    } else {
                        date.setEnabled(true);
                        return false;
                    }
                }
            });

            if (myLocation.isChecked()) {
                date.setEnabled(false);
            } else {
                date.setEnabled(true);
            }

            Preference theme = findPreference(getString(R.string.theme_picker_key));
            bindPreferenceSummaryToValue(theme);

            theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    getActivity().recreate();
                    return true;
                }
            });
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
            }

            return true;
        }
    }
}