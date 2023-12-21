package fi.methics.musap.sdk.internal.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fi.methics.musap.sdk.internal.datatype.MusapLink;
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
        if (rp == null) {
            MLog.d("Not storing null RP");
            return;
        }

        List<RelyingParty> rps = listRelyingParties();
        if (rps == null || rps.isEmpty()) {
            rps = new ArrayList<>();
        }
        rps.add(rp);
        this.storePrefValue(RP_PREF, GSON.toJson(rps));
    }

    public boolean removeRelyingParty(RelyingParty rp) {
        if (rp == null) {
            MLog.d("Cannot remove null Relying Party");
            return false;
        }

        boolean removed = false;
        List<RelyingParty> newRps = new ArrayList<>();
        List<RelyingParty> oldRps = listRelyingParties();

        // Copy all old RPs except the one to be removed to a new list.
        for (RelyingParty oldRp: oldRps) {
            if (oldRp.getLinkID().equalsIgnoreCase(rp.getLinkID())) {
               MLog.d("Removing RP " + oldRp.getLinkID());
               removed = true;
            } else {
                newRps.add(oldRp);
            }
        }

        this.storePrefValue(RP_PREF, GSON.toJson(newRps));

        return removed;
    }

    /**
     * Remove MUSAP Link
     */
    public void removeLink() {
        this.storePrefValue(MUSAP_ID_PREF, null);
    }

    /**
     * List Relying Parties
     * @return RP list
     */
    public List<RelyingParty> listRelyingParties() {
        Type listType = new TypeToken<ArrayList<RelyingParty>>(){}.getType();
        return GSON.fromJson(getPrefValue(RP_PREF), listType);
    }

    /**
     * Store the MUSAP Link
     * @param link MUSAP Link
     */
    public void storeLink(MusapLink link) {
        this.storePrefValue(MUSAP_ID_PREF, GSON.toJson(link));
        MLog.d("Stored MUSAP Link with MUSAP ID " + link.getMusapId());
    }

    /**
     * Get the stored MUSAP Link
     * @return MUSAP Link or null if not stored
     */
    public MusapLink getMusapLink() {
        return GSON.fromJson(this.getPrefValue(MUSAP_ID_PREF), MusapLink.class);
    }

    /**
     * Get the MUSAP ID if stored
     * @return MUSAP ID
     */
    public String getMusapId() {
        MusapLink link = getMusapLink();
        if (link == null) return null;
        return link.getMusapId();
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
