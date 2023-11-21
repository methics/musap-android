package fi.methics.musap.sdk.sscd.rest204;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import fi.methics.musap.sdk.extension.SscdSettings;
import fi.methics.musap.sdk.internal.util.MBase64;

public class Rest204Settings implements SscdSettings {

    public static final String SETTINGS_REST_URL   = "resturl";
    public static final String SETTINGS_RAW_FORMAT = "format_raw";
    public static final String SETTINGS_CMS_FORMAT = "format_cms";
    public static final String SETTINGS_TIMEOUT    = "timeout";
    public static final String SETTINGS_COUNTRY    = "country";
    public static final String SETTINGS_PROVIDER   = "provider";
    public static final String SETTINGS_SSCD_NAME  = "sscdname";
    public static final String SETTINGS_SIGNATUREPROFILE  = "signatureprofile";
    public static final String SETTINGS_BIND_SIGNATUREPROFILE  = "bindsignatureprofile";

    // AP settings
    public static final String SETTINGS_REST_APID    = "apid";
    public static final String SETTINGS_REST_USERID  = "userid";
    public static final String SETTINGS_REST_API_KEY = "apikey";

    private Map<String, String> settings = new HashMap<>();
    private Duration timeout;
    private boolean noSpamEnabled;
    private boolean eventIdEnabled;
    private boolean dtbdEnabled = true;

    /**
     * Construct REST SSCD settings
     * @param restUrl   REST end-point URL
     */
    public Rest204Settings(String restUrl) {
        settings.put(SETTINGS_REST_URL,   restUrl);
        settings.put(SETTINGS_RAW_FORMAT, "http://www.methics.fi/KiuruMSSP/v3.2.0#PKCS1");
        settings.put(SETTINGS_CMS_FORMAT, "http://uri.etsi.org/TS102204/v1.1.2#CMS-Signature");
        this.timeout = Duration.ofMinutes(2);
        settings.put(SETTINGS_TIMEOUT, String.valueOf(timeout.toMillis()));
    }

    public void setApId(String apid) {
        settings.put(SETTINGS_REST_APID, apid);
        settings.put(SETTINGS_REST_USERID, MBase64.toBase64(apid));
    }

    public void setSignatureProfile(String signatureProfile) {
        this.setSetting(SETTINGS_SIGNATUREPROFILE, signatureProfile);
    }

    public void setBindSignatureProfile(String signatureProfile) {
        this.setSetting(SETTINGS_BIND_SIGNATUREPROFILE, signatureProfile);
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

    public void setSscdName(String name) {
        this.setSetting(SETTINGS_SSCD_NAME, name);
    }

    public void setApiKey(String apikey) {
        settings.put(SETTINGS_REST_API_KEY, apikey);
    }

    public void enableNoSpam() {
        this.noSpamEnabled = true;
    }

    public void setEventIdEnabled() {
        this.eventIdEnabled = true;
    }

    public void setDtbdEnabled(boolean enabled) {
        this.dtbdEnabled = enabled;
    }

    @Override
    public Map<String, String> getSettings() {
        return settings;
    }

    public String getRestUrl() {
        return this.getSetting(SETTINGS_REST_URL);
    }

    public String getRestUserId() {
        return this.getSetting(SETTINGS_REST_USERID);
    }

    public String getRestApiKey() {
        return this.getSetting(SETTINGS_REST_API_KEY);
    }
    public String getRawFormatUri() {
        return this.getSetting(SETTINGS_RAW_FORMAT);
    }

    public String getCmsFormatUri() {
        return this.getSetting(SETTINGS_CMS_FORMAT);
    }

    /**
     * Get the SignatureProfile used during binding
     * @return SignatureProfile
     */
    public String getBindSignatureProfile() {
        String sigprof = this.getSetting(SETTINGS_BIND_SIGNATUREPROFILE);
        if (sigprof != null) {
            return sigprof;
        }
        return this.getSignatureProfile();
    }

    /**
     * Get the SignatureProfile used during signing
     * @return SignatureProfile
     */
    public String getSignatureProfile() {
        return this.getSetting(SETTINGS_SIGNATUREPROFILE);
    }

    public String getSscdName() {
        String name = this.getSetting(SETTINGS_SSCD_NAME);
        return name != null ? name : "REST 204";
    }

    public String getProvider() {
        String provider = this.getSetting(SETTINGS_PROVIDER);
        return provider != null ? provider : "Methics";
    }
    public String getCountry() {
        String country = this.getSetting(SETTINGS_COUNTRY);
        return country != null ? country : "FI";
    }

    public Duration getTimeout() {
        if (this.timeout == null) return Duration.ofMinutes(2);
        return this.timeout;
    }

    public boolean isNoSpamEnabled() {
        return this.noSpamEnabled;
    }

    public boolean isEventIdEnabled() {
        return this.eventIdEnabled;
    }

    public boolean isDtbdEnabled() {
        return this.dtbdEnabled;
    }

}
