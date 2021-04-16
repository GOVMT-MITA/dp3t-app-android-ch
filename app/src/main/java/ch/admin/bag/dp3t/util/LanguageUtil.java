package ch.admin.bag.dp3t.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtil {
    public static final String SHARED_PREFS_LANGUAGE_LEGACY = "DP3T_LANGUAGE";
    public static final String SHARED_PREFS_LANGUAGE = "preferences_language";
    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_MT = "mt";

    //Get current custom locale. Set to default if missing.
    public static String getAppLocale(Context context) {
        String defaultLocale = LANGUAGE_EN;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        //If no setting is present initialise a default
        if (!sharedPrefs.contains(LanguageUtil.SHARED_PREFS_LANGUAGE)) {

            //Migrate legacy variable if it is present
            if (sharedPrefs.contains(LanguageUtil.SHARED_PREFS_LANGUAGE_LEGACY)) {
                defaultLocale = sharedPrefs.getString(LanguageUtil.SHARED_PREFS_LANGUAGE_LEGACY, defaultLocale);
                editor.remove(LanguageUtil.SHARED_PREFS_LANGUAGE_LEGACY);
            }

            editor.putString(SHARED_PREFS_LANGUAGE, defaultLocale);
            editor.apply();

            return defaultLocale;
        }

        //Return custom locale
        return sharedPrefs.getString(SHARED_PREFS_LANGUAGE, defaultLocale);
    }

    //Set new custom locale.
    public static void setAppLocale(Context context, String localeCode) {
        //Save locale
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(SHARED_PREFS_LANGUAGE, localeCode);
        editor.apply();

        //Update app locale
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(configuration, displayMetrics);
        configuration.locale = new Locale(localeCode.toLowerCase());
        resources.updateConfiguration(configuration, displayMetrics);
    }
}
