package fi.methics.musap.sdk.extension;

import fi.methics.musap.sdk.internal.discovery.KeyBindReq;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapSscd;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.sign.SignatureReq;

/**
 * MUSAP SSCD extension interface. Every SSCD should implement this.
 */
public interface MusapSscdInterface<T extends SscdSettings> {

    /**
     * Bind an existing key to this MUSAP library
     * @param req Key bind request
     * @throws Exception if binding failed
     * @return
     */
    public MusapKey bindKey(KeyBindReq req) throws Exception;

    /**
     * Generate a new key with this SSCD. Note that this SSCD must support
     * @param req Key generation request
     * @throws Exception if key generation failed
     * @return Recently generated MUSAPKey
     */
    public MusapKey generateKey(KeyGenReq req) throws Exception;

    /**
     * Sign with the SSCD
     * @param req Signature request
     * @return Signature response
     * @throws Exception if signature failed
     */
    public MusapSignature sign(SignatureReq req) throws Exception;

    /**
     * Get SSCD info. Must not return null.
     * @return SSCD info
     */
    public MusapSscd getSscdInfo();

    /**
     * Generate a stable SSCD ID for this
     * @param key MUSAP key
     * @return SSCD ID
     */
    public String generateSscdId(MusapKey key);

    /**
     * Does this SSCD support key generation?
     * @return true if key generation is supported
     */
    public default boolean isKeygenSupported() {
        return this.getSscdInfo().isKeygenSupported();
    }

    public T getSettings();

}
