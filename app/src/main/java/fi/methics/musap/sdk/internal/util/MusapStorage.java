package fi.methics.musap.sdk.internal.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores MUSAP internal data such as App ID.
 */
public class MusapStorage {

    private static final String PREF_NAME = "musap_internal";
    private static final String MUSAP_ID_PREF = "musapid";

    private final Context context;

    public MusapStorage(Context context) {
        this.context = context;
    }

    public void storeMusapId(String musapId) {
        MLog.d("Stored MUSAP ID " + musapId);
        this.storePrefValue(MUSAP_ID_PREF, musapId);
    }

    public String getMusapId() {
        return this.getPrefValue(MUSAP_ID_PREF);
    }

    private void storePrefValue(String prefName, String prefValue) {
        this.getSharedPref().edit().putString(prefName, prefValue).apply();
    }

    private String getPrefValue(String prefName) {
        return this.getSharedPref().getString(prefName, null);
    }

    private SharedPreferences getSharedPref() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
