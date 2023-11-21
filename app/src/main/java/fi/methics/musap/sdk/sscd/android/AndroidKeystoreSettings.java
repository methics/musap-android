package fi.methics.musap.sdk.sscd.android;

import java.util.HashMap;
import java.util.Map;

import fi.methics.musap.sdk.extension.SscdSettings;

public class AndroidKeystoreSettings implements SscdSettings {
    private Map<String, String> settings = new HashMap<>();

    @Override
    public Map<String, String> getSettings() {
        return settings;
    }
}
