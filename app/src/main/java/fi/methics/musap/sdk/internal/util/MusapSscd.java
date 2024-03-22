package fi.methics.musap.sdk.internal.util;

import java.util.ArrayList;
import java.util.List;

import fi.methics.musap.sdk.api.MusapClient;
import fi.methics.musap.sdk.attestation.KeyAttestation;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.extension.SscdSettings;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.datatype.SscdInfo;
import fi.methics.musap.sdk.internal.discovery.KeyBindReq;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.sign.SignatureReq;

/**
 * MUSAP SSCD wrapper. This class can be used to manage an SSCD and it's keys.
 * @see MusapClient#listEnabledSscds() to get a complete list of supported SSCDs
 */
public class MusapSscd {

    public static final String SETTING_SSCDID = "id";

    private MusapSscdInterface<SscdSettings> impl;

    public MusapSscd(MusapSscdInterface<SscdSettings> impl) {
        this.impl = impl;
    }

    /**
     * Get SSCD info. This contains static details that explain SSCD capabilities and
     * help with SSCD discovery.
     * @return SSCD info
     */
    public SscdInfo getSscdInfo() {
        SscdInfo info = this.impl.getSscdInfo();
        info.setSscdId(this.getSscdId());
        return info;
    }

    /**
     * Sign with the SSCD
     * @param req Signature request
     * @return Signature response
     * @throws Exception if signature failed
     */
    public MusapSignature sign(SignatureReq req) throws Exception {
        return this.impl.sign(req);
    }

    /**
     * Bind an existing key to this MUSAP library
     * @param req Key bind request
     * @throws Exception if binding failed
     * @return existing MUSAP Key
     * @throws Exception
     */
    public MusapKey bindKey(KeyBindReq req) throws Exception {
        return this.impl.bindKey(req);
    }

    /**
     * Generate a new key with this SSCD. Note that this SSCD must support
     * @param req Key generation request
     * @throws Exception if key generation failed
     * @return Recently generated MUSAPKey
     * @throws Exception
     */
    public MusapKey generateKey(KeyGenReq req) throws Exception {
        return this.impl.generateKey(req);
    }

    /**
     * Get SSCD settings
     * @return settings
     */
    public SscdSettings getSettings() {
        return this.impl.getSettings();
    }

    /**
     * Get the associated key attestation mechanism
     * @return key attestation mechanism
     */
    public KeyAttestation getKeyAttestation() {
        return this.impl.getKeyAttestation();
    }

    /**
     * List all keys associated with this SSCD
     * @return List of keys
     */
    public List<MusapKey> listKeys() {
        List<MusapKey> keys = new ArrayList<>();
        for (MusapKey key : MusapClient.listKeys()) {
            if (key == null) continue;
            if (key.getSscdId() == null) continue;
            if (key.getSscdId().equals(getSscdId())) {
                keys.add(key);
            }
        }
        return keys;
    }

    /**
     * Get a key associated with this SSCD by Key ID
     * @param keyid Key ID
     * @return key or null if not found
     */
    public MusapKey getKey(String keyid) {
        if (keyid == null) return null;
        for (MusapKey key : this.listKeys()) {
            if (keyid.equals(key.getKeyId())) {
                return key;
            }
        }
        return null;
    }

    /**
     * Remove given key related to this SSCD
     * @param key Key to remove
     * @return true if key was removed
     */
    public boolean removeKey(MusapKey key) {
        if (key == null) return false;
        for (MusapKey k : this.listKeys()) {
            if (key.equals(k)) {
                MusapClient.removeKey(key);
                return true;
            }
        }
        return false;
    }

    /**
     * Remove all keys related to this SSCD
     * @return true if any key was removed
     */
    public boolean removeKeys() {
        List<MusapKey> keys = new ArrayList<>();
        boolean removed = false;
        for (MusapKey key : this.listKeys()) {
            MusapClient.removeKey(key);
            removed = true;
        }
        return removed;
    }

    /**
     * Get the SSCD ID
     * @return SSCD ID
     */
    public String getSscdId() {
        return this.getSettings().getSetting(SETTING_SSCDID);
    }

    /**
     * Get value of a setting of this SSCD
     * @param name Name of the setting
     * @return value of the setting
     */
    public String getSetting(String name) {
        return this.getSettings().getSetting(name);
    }

}
