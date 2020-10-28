package ch.admin.bag.dp3t.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtil {
    public static final String SHARED_PREFS_LANGUAGE = "DP3T_LANGUAGE";
    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_MT = "mt";

    //Get current custom locale. Set to default if missing.
    public static String getAppLocale(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        //Save and return default locale
        if (!sharedPrefs.contains(LanguageUtil.SHARED_PREFS_LANGUAGE)) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString(SHARED_PREFS_LANGUAGE, LANGUAGE_EN);
            editor.apply();

            return LANGUAGE_EN;
        }

        //Return custom locale
        return sharedPrefs.getString(SHARED_PREFS_LANGUAGE, LANGUAGE_EN);
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
