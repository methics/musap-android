package fi.methics.musap.sdk.internal.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fi.methics.musap.sdk.internal.datatype.RelyingParty;

/**
 * Stores MUSAP internal data such as App ID.
 */
public class MusapStorage {

    private static final String PREF_NAME     = "musap_internal";
    private static final String MUSAP_ID_PREF = "musapid";
    private static final String RP_PREF       = "relying-parties";
    private static final Gson GSON = new Gson();

    private final Context context;

    public MusapStorage(Context context) {
        this.context = context;
    }

    /**
     * Store a Relying Party
     * @param rp Relying Party
     */
    public void storeRelyingParty(RelyingParty rp) {
        List<RelyingParty> rps = listRelyingParties();
        if (rps == null || rps.isEmpty()) {
            rps = new ArrayList<>();
        }
        rps.add(rp);
        this.storePrefValue(RP_PREF, GSON.toJson(rps));
    }

    /**
     * List Relying Parties
     * @return RP list
     */
    public List<RelyingParty> listRelyingParties() {
        Type listType = new TypeToken<ArrayList<RelyingParty>>(){}.getType();
        return GSON.fromJson(getPrefValue(RP_PREF), listType);
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
