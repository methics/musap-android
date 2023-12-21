package fi.methics.musap.sdk.internal.sign;

import android.app.Activity;

import fi.methics.musap.sdk.api.MusapClient;
import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.discovery.KeySearchReq;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.api.MusapCallback;

/**
 * MUSAP class that handles signing.
 */
public class MusapSigner {

    private final MusapKey key;
    private final Activity activity;

    /**
     * Create a new MUSAP Signer with a key and an activity.
     * @param key      MUSAP Key selected through key discovery. See {@link MusapClient#listKeys(KeySearchReq)}.
     * @param activity Activity that may be required by SSCDs that need user interaction.
     */
    public MusapSigner(MusapKey key, Activity activity) {
        this.key      = key;
        this.activity = activity;
    }

    /**
     * Sign data.
     * @param req      Signature request that contains the data to sign and related metadata.
     * @param callback Callback that delivers the signing response and errors.
     * @throws MusapException if pre-requisites for signing are not met. For example null key might throw this.
     */
    public void sign(SignatureReq req, MusapCallback<MusapSignature> callback) throws MusapException {
        if (this.key == null || req == null) {
            MLog.e("Missing key or data");
            throw new MusapException("Missing key or data");
        }

        if (this.key.getSscdType() == null) {
            MLog.e("Cannot sign. Missing SSCD type");
            throw new MusapException("Missing key type");
        }
        try {
            MusapSscdInterface sscd = this.key.getSscdImpl();
            if (sscd == null) {
                throw new MusapException("No SSCD found for key " + this.key.getKeyId() + "(" + this.key.getSscdId() + ")");
            }
            req.setActivity(this.activity);
            MusapClient.sign(req, callback);
        } catch (MusapException e) {
            throw e;
        } catch (Exception e) {
            throw new MusapException(e);
        }
    }
}
