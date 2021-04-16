package ch.admin.bag.dp3t.interoperability;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import org.dpppt.android.sdk.DP3T;
import org.dpppt.android.sdk.backend.ResponseCallback;
import org.dpppt.android.sdk.internal.logger.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ch.admin.bag.dp3t.R;
import ch.admin.bag.dp3t.home.model.InteroperabilityMode;
import ch.admin.bag.dp3t.networking.models.EUSharingCountryModel;
import ch.admin.bag.dp3t.storage.SecureStorage;
import ch.admin.bag.dp3t.util.LanguageUtil;

public class InteroperabilityFragment extends PreferenceFragmentCompat {

    public static InteroperabilityFragment newInstance() {
        return new InteroperabilityFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_screen_interoperability, rootKey);

        final String language = LanguageUtil.getAppLocale(getContext());
        final SecureStorage secureStorage = SecureStorage.getInstance(getContext());

        Preference preferencePrivacy = findPreference("preferences_static_privacy");
        if (preferencePrivacy != null) {
            preferencePrivacy.setOnPreferenceClickListener((preference) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.onboarding_disclaimer_legal_button_url)));
                startActivity(browserIntent);
                return false;
            });
        }

        Preference preferenceFaq = findPreference("preferences_static_faq");
        if (preferenceFaq != null) {
            preferenceFaq.setOnPreferenceClickListener((preference) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.faq_button_url)));
                startActivity(browserIntent);
                return false;
            });
        }

        SwitchPreference preferenceEU = findPreference("preferences_covid_interop_eu");
        SwitchPreference preferenceCountries = findPreference("preferences_covid_interop_countries");
        MultiSelectListPreference preferenceCountriesSelection = findPreference("preferences_covid_interop_countries_selection");
        SwitchPreference preferenceMalta = findPreference("preferences_covid_interop_malta");

        //Update country mode if changes are detected
        if(secureStorage.getConfigInteroperabilityMode() == InteroperabilityMode.COUNTRIES_UPDATE_PENDING) {
            secureStorage.setConfigInteroperabilityMode(InteroperabilityMode.COUNTRIES);
            //Empty user choice
            preferenceCountriesSelection.setValues(new HashSet<>());
        }

        //Show country selection option if current mode is for countries
        if(preferenceCountries.isChecked()){
            preferenceCountriesSelection.setVisible(true);
        }

        if (secureStorage.getConfigInteroperabilityPossible()) {
            preferenceEU.setOnPreferenceChangeListener((preference, value) -> {
                if ((Boolean) value) {
                    preferenceCountries.setChecked(false);
                    preferenceCountriesSelection.setVisible(false);
                    preferenceMalta.setChecked(false);
                    secureStorage.setConfigInteroperabilityMode(InteroperabilityMode.EU);
                } else {
                    secureStorage.setConfigInteroperabilityMode(InteroperabilityMode.DISABLED);
                }
                updateInteropMode(preference, value);

                return true;
            });

            preferenceCountries.setOnPreferenceChangeListener((preference, value) -> {
                if ((Boolean) value) {
                    preferenceEU.setChecked(false);
                    preferenceMalta.setChecked(false);
                    preferenceCountriesSelection.setVisible(true);
                    secureStorage.setConfigInteroperabilityMode(InteroperabilityMode.COUNTRIES);
                } else {
                    preferenceCountriesSelection.setVisible(false);
                    secureStorage.setConfigInteroperabilityMode(InteroperabilityMode.DISABLED);
                }
                updateInteropMode(preference, value);

                return true;
            });

            //Retrieve and process EU countries into entry/value arrays
            EUSharingCountryModel[] euSharingCountries = secureStorage.getConfigInteroperabilityCountries();

            List<CharSequence> euSharingCountryEntries = new ArrayList<>();
            List<CharSequence> euSharingCountryValues = new ArrayList<>();

            for (EUSharingCountryModel euSharingCountry : euSharingCountries) {
                if (language.equals(LanguageUtil.LANGUAGE_MT) && euSharingCountry.getCountryNameMT() != null) {
                    euSharingCountryEntries.add(euSharingCountry.getCountryNameMT());
                } else {
                    euSharingCountryEntries.add(euSharingCountry.getCountryNameEN());
                }
                euSharingCountryValues.add(euSharingCountry.getCountryCode());
            }

            preferenceCountriesSelection.setEntries(euSharingCountryEntries.toArray(new CharSequence[0]));
            preferenceCountriesSelection.setEntryValues(euSharingCountryValues.toArray(new CharSequence[0]));

            preferenceCountriesSelection.setOnPreferenceChangeListener((preference, value) -> {
                updateInteropMode(preference, value);
                return true;
            });

            preferenceMalta.setOnPreferenceChangeListener((preference, value) -> {
                if ((Boolean) value) {
                    preferenceEU.setChecked(false);
                    preferenceCountries.setChecked(false);
                    preferenceCountriesSelection.setVisible(false);
                }
                secureStorage.setConfigInteroperabilityMode(InteroperabilityMode.DISABLED);
                updateInteropMode(preference, value);

                return true;
            });
        } else {
            //Show warning message
            Preference preferenceEUDisabled = findPreference("preferences_static_eu_disabled");
            preferenceEUDisabled.setVisible(true);

            preferenceEU.setEnabled(false);
            preferenceCountries.setEnabled(false);
            preferenceCountriesSelection.setEnabled(false);
            preferenceMalta.setEnabled(false);
        }
    }

    public void updateInteropMode(Preference preference, Object value){
        int mode;
        HashSet selectedCountries;
        switch(preference.getKey()){
            case "preferences_covid_interop_eu":{
                mode = InteroperabilityMode.toInt(InteroperabilityMode.EU);
                mode = (boolean)value == false ? InteroperabilityMode.toInt(InteroperabilityMode.DISABLED) : mode;
                break;
            }
            case "preferences_covid_interop_countries":{
                mode = InteroperabilityMode.toInt(InteroperabilityMode.COUNTRIES);
                mode = (boolean)value == false ? InteroperabilityMode.toInt(InteroperabilityMode.DISABLED) : mode;
                break;
            }
            case "preferences_covid_interop_countries_selection":{
                mode = InteroperabilityMode.toInt(InteroperabilityMode.COUNTRIES_UPDATE_PENDING);
                selectedCountries = (HashSet)value;
                updateSelectedCountries(selectedCountries);
                break;
            }
            default:{
                mode = InteroperabilityMode.toInt(InteroperabilityMode.DISABLED);
                mode = (boolean)value == false ? InteroperabilityMode.toInt(InteroperabilityMode.DISABLED) : mode;
                break;
            }
        }

        DP3T.updateInteropMode(getActivity(), mode, new ResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                Logger.i("DP3T Interface", "changed interop mode: " + response);
            }

            @Override
            public void onError(Throwable throwable) {
                Logger.e("DP3T Interface", "updateInteropMode", throwable);
                throwable.printStackTrace();
            }
        });
    }

    public void updateSelectedCountries(HashSet<String> selectedCountries){
        DP3T.updateSelectedCountries(getActivity(), selectedCountries, new ResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                Logger.i("DP3T Interface", "updated selected countries: " + response);
            }

            @Override
            public void onError(Throwable throwable) {
                Logger.e("DP3T Interface", "updateSelectedCountries", throwable);
                throwable.printStackTrace();
            }
        });
    }
}
