package fi.methics.musap.sdk.sscd.external;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import fi.methics.musap.sdk.api.MusapClient;
import fi.methics.musap.sdk.attestation.KeyAttestation;
import fi.methics.musap.sdk.attestation.UiccKeyAttestation;
import fi.methics.musap.sdk.extension.SscdSettings;
import fi.methics.musap.sdk.internal.datatype.MusapLink;

public class ExternalSscdSettings implements SscdSettings {

    public static final String SETTINGS_TIMEOUT    = "timeout";
    public static final String SETTINGS_CLIENT_ID  = "clientid";
    public static final String SETTINGS_SSCD_NAME  = "sscdname";
    public static final String SETTINGS_PROVIDER   = "provider";

    private Map<String, String> settings = new HashMap<>();
    private Duration timeout;

    /**
     * Construct External SSCD settings
     * @param clientid Client ID (matching a client in MUSAP Link)
     */
    public ExternalSscdSettings(String clientid) {
        this.timeout = Duration.ofMinutes(2);
        settings.put(SETTINGS_TIMEOUT, String.valueOf(timeout.toMillis()));
        settings.put(SETTINGS_CLIENT_ID, clientid);
    }

    @Override
    public Map<String, String> getSettings() {
        return settings;
    }

    public ExternalSscdSettings setSscdName(String name) {
        this.setSetting(SETTINGS_SSCD_NAME, name);
        return this;
    }

    public ExternalSscdSettings setProvider(String provider) {
        this.setSetting(SETTINGS_PROVIDER, provider);
        return this;
    }

    public String getClientId() {
        return this.getSetting(SETTINGS_CLIENT_ID);
    }

    public Duration getTimeout() {
        if (this.timeout == null) return Duration.ofMinutes(2);
        return this.timeout;
    }

    public String getProvider() {
        return this.getSetting(SETTINGS_PROVIDER);
    }

    public String getSscdName() {
        String name = this.getSetting(SETTINGS_SSCD_NAME);
        return name != null ? name : "External Signature";
    }

    public MusapLink getMusapLink() {
        return MusapClient.getMusapLink();
    }

}
