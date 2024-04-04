package fi.methics.musap.sdk.internal.discovery;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SharedPrefStorage implements Storage {

    private static final String PREF_NAME = "musap";

    private final Context context;

    public SharedPrefStorage(Context c) {
        this.context = c;
    }

    @Override
    public String getString(String key, String defaultValue) {
        return this.getSharedPref().getString(key, defaultValue);
    }

    @Override
    public Storage putString(String key, String value) {
        this.getSharedPref()
                .edit()
                .putString(key, value)
                .apply();
        return this;
    }

    @Override
    public Storage putStringSet(String key, Set<String> value) {
        this.getSharedPref()
                .edit()
                .putStringSet(key, value)
                .apply();
        return this;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return this.getSharedPref().getStringSet(key, defaultValue);
    }


    @Override
    public Storage removeString(String key) {
        this.getSharedPref().edit().remove(key).apply();
        return this;
    }

    private SharedPreferences getSharedPref() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
