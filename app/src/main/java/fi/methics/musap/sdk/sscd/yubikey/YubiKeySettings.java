package fi.methics.musap.sdk.sscd.yubikey;

import java.util.HashMap;
import java.util.Map;

import fi.methics.musap.sdk.extension.SscdSettings;

public class YubiKeySettings implements SscdSettings {
    private Map<String, String> settings = new HashMap<>();
    @Override
    public Map<String, String> getSettings() {
        return settings;
    }

}
