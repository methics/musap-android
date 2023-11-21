package fi.methics.musap.sdk.internal.discovery;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.methics.musap.sdk.api.MusapClient;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.internal.datatype.KeyAttribute;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapSscd;
import fi.methics.musap.sdk.internal.keygeneration.UpdateKeyReq;
import fi.methics.musap.sdk.internal.util.MLog;

/**
 * MUSAP Metadata Storage class
 * This is used to store Key and SSCD metadata. Both are saved in JSON format. String sets
 * "keyids" and "sscdids" contain all added keys and SSCDs. The JSON of a key or an SSCD can be
 * fetched with a key name or SSCD ID.
 *
 * Key JSON contains a reference to the SSCD it belongs to, but the reverse is not true.
 * This simplifies key generation, delete, and update operations.
 *
 */
public class MetadataStorage {

    private static final String PREF_NAME = "musap";
    private static final String SSCD_SET  = "sscd";

    /**
     * Set that contains all known key names
     */
    private static final String KEY_ID_SET = "keyids";
    private static final String SSCD_ID_SET  = "sscdids";

    /**
     * Prefix that storage uses to store key-speficic metadata.
     */
    private static final String KEY_JSON_PREFIX  = "keyjson_";
    private static final String SSCD_JSON_PREFIX = "sscdjson_";

    private Context context;

    public MetadataStorage(Context context) {
        this.context = context;
    }

    /**
     * Store a MUSAP key metadata.
     * The storage has two parts:
     *  1. A set of all known key IDs.
     *  2. For each key name, there is an entry "keyjson_<keyid>" that contains the key
     *     metadata.
     * @param key  MUSAP key
     * @param sscd MUSAP SSCD that holds the key
     */
    public void addKey(MusapKey key, MusapSscd sscd) {
        if (key == null) {
            MLog.e("Cannot store null MUSAP key");
            throw new IllegalArgumentException("Cannot store null MUSAP key");
        }
        if (key.getKeyId() == null) {
            MLog.e("Cannot store unnamed MUSAP key");
            throw new IllegalArgumentException("Cannot store unnamed MUSAP key");
        }

        MLog.d("Storing key");

        // Update Key Name list with new Key Name
        Set<String> newKeyIds = new HashSet<>(this.getAllKeyIds());
        newKeyIds.add(key.getKeyId());

        // Conver
        String keyJson = this.toJson(key);
        MLog.d("KeyJson=" + keyJson);

        this.getSharedPref()
                .edit()
                .putStringSet(KEY_ID_SET, newKeyIds)
                .putString(this.makeStoreName(key), keyJson)
                .apply();

        if (sscd != null) {
            this.addSscd(sscd);
        }
    }

    /**
     * List available MUSAP keys
     * @return MUSAP keys
     */
    public List<MusapKey> listKeys() {
        Set<String> keyIds = this.getAllKeyIds();
        List<MusapKey> keyList = new ArrayList<>();
        for (String keyId: keyIds) {
            String keyJson = this.getKeyJson(keyId);
            if (keyJson == null) {
                MLog.e("Missing key metadata JSON for key name " + keyId);
            } else {
                MLog.d("Found key " + keyJson);
                MusapKey key = new Gson().fromJson(keyJson, MusapKey.class);
                keyList.add(key);
            }
        }

        return keyList;
    }

    /**
     * List available MUSAP keys that match the search request.
     * @param req Key search request
     * @return List of matching keys
     */
    public List<MusapKey> listKeys(KeySearchReq req) {
        Set<String> keyIds = this.getAllKeyIds();
        List<MusapKey> keyList = new ArrayList<>();
        for (String keyId: keyIds) {
            String keyJson = this.getKeyJson(keyId);
            if (keyJson == null) {
                MLog.e("Missing key metadata JSON for key name " + keyId);
            } else {
                MusapKey key = new Gson().fromJson(keyJson, MusapKey.class);
                if (req.matches(key)) {
                    MLog.d("Request matches key " + keyId);
                    keyList.add(key);
                } else {
                    MLog.d("Request does not match key " + keyId);
                }
            }
        }

        return keyList;
    }

    /**
     * Remove key metadata from storage
     * @param key Key to remove
     * @return true if key was found and removed
     */
    public boolean removeKey(MusapKey key) {
        // Update Key Name list without given Key Name
        Set<String> newKeyIds = new HashSet<>(this.getAllKeyIds());
        if (!this.getAllKeyIds().contains(key.getKeyId())) {
            MLog.d("No key found with name " + key.getKeyId());
            return false;
        }
        newKeyIds.remove(key.getKeyId());

        String keyJson = new Gson().toJson(key);
        MLog.d("KeyJson=" + keyJson);

        this.getSharedPref()
                .edit()
                .putStringSet(KEY_ID_SET, newKeyIds)
                .putString(this.makeStoreName(key), keyJson)
                .remove(this.makeStoreName(key.getKeyId()))
                .apply();
        return true;
    }

    /**
     * Store metadata of an active MUSAP SSCD
     * @param sscd SSCD (that has keys bound or generated)
     */
    public void addSscd(MusapSscd sscd) {
        if (sscd == null) {
            MLog.e("Cannot store null MUSAP SSCD");
            throw new IllegalArgumentException("Cannot store null MUSAP SSCD");
        }
        if (sscd.getSscdId() == null) {
            MLog.e("Cannot store MUSAP SSCD without an ID");
            throw new IllegalArgumentException("Cannot store MUSAP SSCD without an ID");
        }

        // Update SSCD id list with new SSCD ID
        Set<String> sscdIds = new HashSet<>(this.getAllSscdIds());
        if (sscdIds.contains(sscd.getSscdId())) {
            MLog.d("SSCD " + sscd.getSscdId() + " already stored");
            return;
        }
        sscdIds.add(sscd.getSscdId());

        MLog.d("Storing SSCD " + sscd.getSscdId());

        String json = new Gson().toJson(sscd);
        MLog.d("SSCD JSON=" + json);

        this.getSharedPref()
                .edit()
                .putStringSet(SSCD_ID_SET, sscdIds)
                .putString(this.makeStoreName(sscd), json)
                .apply();
    }


    /**
     * List available active MUSAP SSCDs
     * @return active MUSAP SSCDs (that have keys bound or generated)
     */
    public List<MusapSscd> listActiveSscds() {
        Set<String> sscdIds = this.getAllSscdIds();
        List<MusapSscd> sscdList = new ArrayList<>();
        for (String sscdid : sscdIds) {
            String sscdJson = this.getSscdJson(sscdid);
            if (sscdJson == null) {
                MLog.e("Missing SSCD metadata JSON for SSCD ID " + sscdid);
            } else {
                MusapSscd sscd = new Gson().fromJson(sscdJson, MusapSscd.class);
                sscdList.add(sscd);
            }
        }
        return sscdList;
    }

    /**
     * Store MUSAP import data
     * @param data import data
     */
    public void addImportData(MusapImportData data) {
        if (data == null) return;
        List<MusapSscd> activeSscds = this.listActiveSscds();
        List<MusapSscdInterface> enabledSscds = MusapClient.listEnabledSscds();
        List<MusapKey>  activeKeys  = this.listKeys();
        for (MusapSscd sscd : data.sscds) {
            // Avoid duplicate SSCDs and SSCDs that are not enabled in this MUSAP
            boolean alreadyExists   = activeSscds.stream().anyMatch(s -> s.getSscdId().equals(sscd.getSscdId()));
            boolean sscdTypeEnabled = !enabledSscds.stream().anyMatch(s -> s.getSscdInfo().getSscdType().equals(sscd.getSscdType()));
            if (alreadyExists || !sscdTypeEnabled) continue;
            this.addSscd(sscd);
        }
        for (MusapKey key : data.keys) {
            // Avoid duplicate keys
            if (activeKeys.stream().anyMatch(k -> k.getKeyUri().equals(k.getKeyUri()))) continue;

            if (key.getSscdImpl() != null) {
                this.addKey(key, key.getSscdImpl().getSscdInfo());
            }
        }
    }

    /**
     * Get MUSAP import data for export
     * @return import data
     */
    public MusapImportData getImportData() {
        MusapImportData data = new MusapImportData();
        data.sscds = this.listActiveSscds();
        data.keys  = this.listKeys();
        return data;
    }

    /**
     * Update target key metadata with new values.
     * @param req
     * @return True if the update is succesful.
     */
    public boolean updateKeyMetaData(UpdateKeyReq req) {
        MusapKey targetKey = req.getKey();

        if (targetKey == null) {
            MLog.d("Update request is missing target key");
            throw new IllegalArgumentException("Missing key");
        }

        String keyJson = this.getKeyJson(req.getKey().getKeyId());
        MusapKey oldKey = this.parseKeyJson(keyJson);

        if (req.getAlias() != null) {
            oldKey.setAlias(req.getAlias());
        }

        if (req.getDid() != null) {
            oldKey.setDid(req.getDid());
        }

        if (req.getState() != null) {
            oldKey.setState(req.getState());
        }
        if (req.getAttributes() != null) {
            for (KeyAttribute attr: req.getAttributes()) {

                // Remove attribute if the new value is set to null.
                if (attr.value == null) {
                    oldKey.removeAttribute(attr.name);
                } else {
                    oldKey.addAttribute(attr);
                }
            }
        }

        // Store the updated key.
        this.addKeyToMetadataStorage(oldKey);

        return true;
    }

    private String makeStoreName(MusapKey key) {
        return KEY_JSON_PREFIX + key.getKeyId();
    }
    private String makeStoreName(MusapSscd sscd) {
        return SSCD_JSON_PREFIX + sscd.getSscdId();
    }
    private String makeStoreName(String keyId) {
        return KEY_JSON_PREFIX + keyId;
    }

    private Set<String> getAllKeyIds() {
        return this.getSharedPref().getStringSet(KEY_ID_SET, new HashSet<>());
    }
    private Set<String> getAllSscdIds() {
        return this.getSharedPref().getStringSet(SSCD_ID_SET, new HashSet<>());
    }

    private String getKeyJson(String keyId) {
        return this.getSharedPref().getString(this.makeStoreName(keyId), null);
    }
    private String getSscdJson(String sscdid) {
        return this.getSharedPref().getString(SSCD_JSON_PREFIX + sscdid, null);
    }
    private SharedPreferences getSharedPref() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Add key metadata to the metadata storage.
     * @param key
     */
    private void addKeyToMetadataStorage(MusapKey key) {
        String keyJson = this.toJson(key);
        this.getSharedPref()
                .edit()
                .putString(this.makeStoreName(key), keyJson)
                .apply();
    }

    private String toJson(MusapKey key) {
        return new Gson().toJson(key);
    }

    private MusapKey parseKeyJson(String keyJson) {
        return new Gson().fromJson(keyJson, MusapKey.class);
    }

}
