package com.twilio.verification.sample.settings;


import android.app.ActionBar;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.StringRes;
import android.view.MenuItem;
import android.view.View;

import com.twilio.verification.sample.R;
import com.twilio.verification.sample.utils.MessageHelper;

import okhttp3.HttpUrl;

public class SettingsActivity extends AppCompatPreferenceActivity {
    public static final String TOKEN_ENDPOINT_PREF = "token_endpoint";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public static class SettingsFragment extends PreferenceFragment {

        private MessageHelper messageHelper;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            messageHelper = new MessageHelper();

            EditTextPreference endpointPreference = (EditTextPreference) getPreferenceScreen().findPreference(TOKEN_ENDPOINT_PREF);
            endpointPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    boolean validEndpoint = isValidEndpoint(newValue);
                    if (!validEndpoint) {
                        showErrorMessage(R.string.settings_invalid_endpoint);
                    }

                    return validEndpoint;
                }
            });
        }

        private void showErrorMessage(@StringRes int stringId) {
            if (getActivity() == null) {
                return;
            }

            View rootView = getActivity().findViewById(android.R.id.content);

            if (rootView == null) {
                return;
            }

            messageHelper.show(rootView, stringId);
        }

        private boolean isValidEndpoint(Object newValue) {
            if (newValue == null) {
                return false;
            }
            HttpUrl newEndpoint = HttpUrl.parse(newValue.toString());
            return newEndpoint != null;
        }

        @Override
        public void onStop() {
            messageHelper.dismiss();
            super.onStop();
        }
    }
}
