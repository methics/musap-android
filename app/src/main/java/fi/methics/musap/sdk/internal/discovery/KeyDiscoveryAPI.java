package fi.methics.musap.sdk.internal.discovery;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.SscdInfo;

public class KeyDiscoveryAPI {

    private static List<MusapSscdInterface> enabledSscds = new ArrayList<>();
    private AndroidMetadataStorage storage;

    public KeyDiscoveryAPI(AndroidMetadataStorage storage) {
        this.storage = storage;
    }

    /**
     * List all SSCDs integrated to this MUSAP Library
     * @return SSCD list
     */
    public List<MusapSscdInterface> listEnabledSscds() {
        return enabledSscds;
    }

    /**
     * Find MUSAP SSCDs that match given search criteria
     * @param req SSCD search request>
     * @return MUSAP SSCDs
     */
    public List<MusapSscdInterface> listMatchingSscds(SscdSearchReq req) {
        return enabledSscds;
    }

    /**
     * List active SSCDs. This returns all SSCDs that have either a generated or a bound key.
     * @return Active SSCDs
     */
    public List<SscdInfo> listActiveSSCDs() {
        return storage.listActiveSscds();
    }

    /**
     * Enable given SSCD. The SSCD will be available for the user to select after this call.
     * Get the list of available SSCDs with {@link #listEnabledSscds()}.
     * @param sscd SSCD
     */
    public void enableSscd(MusapSscdInterface sscd) {
        enabledSscds.add(sscd);
    }

    /**
     * Find MUSAP keys that match given search criteria
     * @param req Key search request
     * @return Matching MUSAP keys
     */
    public List<MusapKey> findKey(KeySearchReq req) {

        List<MusapKey> keys = this.listKeys();
        List<MusapKey> result = new ArrayList<>();
        for (MusapKey key : keys) {
            if (req.matches(key)) {
                result.add(key);
            }
        }
        return result;
    }

    /**
     * List available MUSAP keys
     * @return List of available keys
     */
    public List<MusapKey> listKeys() {
        return this.storage.listKeys();
    }

    /**
     * Remove a key from MUSAP
     * @param key
     * @return
     */
    public boolean removeKey(MusapKey key) {
        return this.storage.removeKey(key);
    }

}
