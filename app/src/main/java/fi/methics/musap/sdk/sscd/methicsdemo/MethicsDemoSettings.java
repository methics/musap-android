package fi.methics.musap.sdk.sscd.methicsdemo;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import fi.methics.musap.sdk.extension.SscdSettings;

public class MethicsDemoSettings implements SscdSettings {

    public static final String SETTINGS_DEMO_URL   = "demourl";
    public static final String SETTINGS_RAW_FORMAT = "format_raw";
    public static final String SETTINGS_CMS_FORMAT = "format_cms";
    public static final String SETTINGS_TIMEOUT    = "timeout";
    public static final String SETTINGS_SSCD_NAME  = "sscdname";
    public static final String SETTINGS_SIGNATUREPROFILE  = "signatureprofile";

    private Map<String, String> settings = new HashMap<>();
    private Duration timeout;

    /**
     * Construct Methics Demo SSCD settings
     * @param demoUrl   Demo end-point URL
     */
    public MethicsDemoSettings(String demoUrl) {
        settings.put(SETTINGS_DEMO_URL,   demoUrl);
        settings.put(SETTINGS_RAW_FORMAT, "http://www.methics.fi/KiuruMSSP/v3.2.0#PKCS1");
        settings.put(SETTINGS_CMS_FORMAT, "http://uri.etsi.org/TS102204/v1.1.2#CMS-Signature");
        this.timeout = Duration.ofMinutes(2);
        settings.put(SETTINGS_TIMEOUT, String.valueOf(timeout.toMillis()));
    }

    /**
     * Set the RAW format URI. Default is "http://www.methics.fi/KiuruMSSP/v3.2.0#PKCS1"
     * @param formatUri New RAW format URI
     */
    public void setRawFormat(String formatUri) {
        settings.put(SETTINGS_RAW_FORMAT, formatUri);
    }

    /**
     * Set the CMS format URI. Default is "http://uri.etsi.org/TS102204/v1.1.2#CMS-Signature"
     * @param formatUri New CMS format URI
     */
    public void setCmsFormat(String formatUri) {
        settings.put(SETTINGS_CMS_FORMAT, formatUri);
    }

    @Override
    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSignatureProfile(String signatureProfile) {
        this.setSetting(SETTINGS_SIGNATUREPROFILE, signatureProfile);
    }

    public void setSscdName(String name) {
        this.setSetting(SETTINGS_SSCD_NAME, name);
    }

    public String getDemoUrl() {
        return this.getSetting(SETTINGS_DEMO_URL);
    }

    public String getRawFormatUri() {
        return this.getSetting(SETTINGS_RAW_FORMAT);
    }

    public String getCmsFormatUri() {
        return this.getSetting(SETTINGS_CMS_FORMAT);
    }

    public String getSignatureProfile() {
        return this.getSetting(SETTINGS_SIGNATUREPROFILE);
    }

    public String getSscdName() {
        String name = this.getSetting(SETTINGS_SSCD_NAME);
        return name != null ? name : "Methics Demo";
    }

    public Duration getTimeout() {
        if (this.timeout == null) return Duration.ofMinutes(2);
        return this.timeout;
    }

}
