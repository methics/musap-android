package fi.methics.musap.sdk.extension;

import java.util.Map;

public interface SscdSettings {

    public Map<String, String> getSettings();

    public default String getSetting(String key) {
        Map<String, String> settings = this.getSettings();
        if (settings == null) return null;
        return settings.get(key);
    }

    public default void setSetting(String key, String value) {
        if (this.getSettings() == null) return;
        this.getSettings().put(key, value);
    }

}
