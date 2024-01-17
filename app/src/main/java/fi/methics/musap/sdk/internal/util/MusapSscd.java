package fi.methics.musap.sdk.internal.util;

import java.util.ArrayList;
import java.util.List;

import fi.methics.musap.sdk.api.MusapClient;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.extension.SscdSettings;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.datatype.SscdInfo;
import fi.methics.musap.sdk.internal.discovery.KeyBindReq;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.sign.SignatureReq;

public class MusapSscd {

    public static final String SETTING_SSCDID = "id";

    private MusapSscdInterface<SscdSettings> impl;

    public MusapSscd(MusapSscdInterface<SscdSettings> impl) {
        this.impl = impl;
    }

    public SscdInfo getSscdInfo() {
        SscdInfo info = this.impl.getSscdInfo();
        info.setSscdId(this.getSscdId());
        return info;
    }

    public MusapSignature sign(SignatureReq req) throws Exception {
        return this.impl.sign(req);
    }

    public MusapKey bindKey(KeyBindReq req) throws Exception {
        return this.impl.bindKey(req);
    }

    public MusapKey generateKey(KeyGenReq req) throws Exception {
        return this.impl.generateKey(req);
    }

    public SscdSettings getSettings() {
        return this.impl.getSettings();
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

    public String getSscdId() {
        return this.getSettings().getSetting(SETTING_SSCDID);
    }

    public String getSetting(String name) {
        return this.getSettings().getSetting(name);
    }

}
