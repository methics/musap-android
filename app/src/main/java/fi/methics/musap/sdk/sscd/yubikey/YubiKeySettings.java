package fi.methics.musap.sdk.sscd.yubikey;

import java.util.HashMap;
import java.util.Map;

import fi.methics.musap.sdk.extension.SscdSettings;

public class YubiKeySettings implements SscdSettings {

    private static final String MANAGEMENT_KEY = "managementkey";
    private static final String MANAGEMENT_KEY_TYPE = "managementkeytype";

    private static final String ADMIN_PIN = "adminpin";

    private final Map<String, String> settings = new HashMap<>();
    @Override
    public Map<String, String> getSettings() {
        return settings;
    }

    /**
     * Set the management key of a Yubi key.
     * MUSAP requires the management key to generate a key. If no key is provided,
     * MUSAP uses default mangement key.
     * @param key The management key the user has set in hexadecimal format
     */
    public void setManagementKey(String key) {
        this.settings.put(MANAGEMENT_KEY, key);
    }

    public String getManagementKey() {
        return this.settings.get(MANAGEMENT_KEY);
    }

    /**
     * Set the management key type of a Yubi key.
     * MUSAP requires the management key to generate a key. If no key is provided,
     * MUSAP uses default mangement key type.
     * @param type The management key type the user has set.
     */
    public void setManagementKeyType(String type) {
        this.settings.put(MANAGEMENT_KEY_TYPE, type);
    }

    public String getManagementKeyType() {
        return this.settings.get(MANAGEMENT_KEY_TYPE);
    }

    /**
     * Set the Admin PIN of a Yubi key.
     * MUSAP requires the Admin PIN key to generate a key on OpenPGPC. If no PIN is provided,
     * MUSAP uses default PIN
     * @param pin The Admin PIN the user has set.
     */
    public void setAdminPin(String pin) {
        this.settings.put(ADMIN_PIN, pin);
    }

    public String getAdminPin() {
        return this.settings.get(ADMIN_PIN);
    }


}
