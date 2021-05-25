package ch.admin.bag.dp3t;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.dpppt.android.sdk.DP3T;
import org.dpppt.android.sdk.backend.ResponseCallback;
import org.dpppt.android.sdk.internal.logger.Logger;

import ch.admin.bag.dp3t.interoperability.InteroperabilityActivity;
import ch.admin.bag.dp3t.util.LanguageUtil;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_screen_settings, rootKey);

        BottomNavigationView navigation = getActivity().findViewById(R.id.fragment_main_navigation_view);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        ListPreference preferenceLanguage = findPreference("preferences_language");
        if (preferenceLanguage != null) {
            preferenceLanguage.setValue(LanguageUtil.getAppLocale(getContext()));
            preferenceLanguage.setOnPreferenceChangeListener((preference, newValue) -> {
                //Save new language preference
                String newLocale = newValue.equals(LanguageUtil.LANGUAGE_MT) ? LanguageUtil.LANGUAGE_MT : LanguageUtil.LANGUAGE_EN;
                LanguageUtil.setAppLocale(getContext(), newLocale);

                //Refresh preferences fragment by simulating navigation change
                navigation.setSelectedItemId(R.id.bottom_nav_settings);
                navigation.getMenu().findItem(navigation.getSelectedItemId()).setTitle(R.string.bottom_nav_tab_preferences);

                //Redundant re-replacement of value in shared preferences, but required.
                return true;
            });
        }

        SwitchPreferenceCompat preferenceWiFiSync = findPreference("preferences_wifi_sync");
        if (preferenceWiFiSync != null) {
            preferenceWiFiSync.setOnPreferenceChangeListener((preference, newValue) -> {
                DP3T.updateWifiSync(getActivity(), (boolean)newValue, new ResponseCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        Logger.i("DP3T Interface", "changed wifi sync setting");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.e("DP3T Interface", "updateWifiSync", throwable);
                        throwable.printStackTrace();
                    }
                });
                return true;
            });
        }

        Preference preferenceExposureDataMethod = findPreference("preferences_exposure_data_method");
        if (preferenceExposureDataMethod != null) {
            preferenceExposureDataMethod.setOnPreferenceClickListener((preference) -> {
                Intent intent = new Intent(getActivity(), InteroperabilityActivity.class);
                startActivity(intent);
                return false;
            });
        }
    }
}
