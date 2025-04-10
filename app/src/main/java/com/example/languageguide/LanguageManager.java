package com.example.languageguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.LocaleList;

import java.util.Locale;

public class LanguageManager {
    private static final String PREFS_NAME = "LANGUAGE_SETTINGS";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_ITEM = "item";
    private final Context context;
    public LanguageManager(Context context) {
        this.context = context;
    }

    public void setLanguage(String language, int item) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        configuration.setLocales(localeList);
        configuration.setLayoutDirection(locale);

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_LANGUAGE, language);
        editor.putInt(KEY_ITEM, item);
        editor.apply();
    }

    public void loadLanguage() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = preferences.getString(KEY_LANGUAGE, "en");
        int item = preferences.getInt(KEY_ITEM, 0);
        setLanguage(language, item);
    }

    public int getSelectedLanguageIndex() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_ITEM, 0);
    }
}
