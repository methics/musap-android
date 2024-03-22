package fi.methics.musap.sdk.attestation;

/**
 * Contains attestation type names that MUSAP supports at the moment.
 * This is not an enum to allow easier extendability for new types.
 */
public class KeyAttestationType {

    public static final String YUBIKEY = "Yubikey";
    public static final String UICC = "UICC";
    public static final String CERTIFICATE = "Certificate";
    public static final String NONE = "No Attestation";
}
