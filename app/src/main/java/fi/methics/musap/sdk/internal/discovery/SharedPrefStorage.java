package fi.methics.musap.sdk.internal.discovery;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SharedPrefStorage implements Storage {

    private static final String DEFAULT_PREF_NAME = "musap";

    private final Context context;
    private final String prefName;

    public SharedPrefStorage(Context c) {
        this.context = c;
        // Use default
        this.prefName = DEFAULT_PREF_NAME;
    }

    public SharedPrefStorage(Context c, String prefName) {
        this.context = c;
        // Use default
        this.prefName = prefName;
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
        return context.getSharedPreferences(this.prefName, Context.MODE_PRIVATE);
    }
}
