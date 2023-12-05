package fi.methics.musap.sdk.internal.async;

import android.content.Context;

import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.datatype.RelyingParty;
import fi.methics.musap.sdk.internal.util.AsyncTaskResult;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapAsyncTask;
import fi.methics.musap.sdk.internal.util.MusapStorage;

/**
 * Sends MUSAP Signature Callback
 */
public class SignatureCallbackTask extends MusapAsyncTask<Void> {

    private final MusapLink link;
    private final MusapSignature signature;

    public SignatureCallbackTask(MusapLink link, MusapSignature signature, MusapCallback<Void> callback, Context context) {
        super(callback, context);
        this.link = link;
        this.signature = signature;
    }

    @Override
    protected AsyncTaskResult<Void> runOperation() throws MusapException {
        try {
            link.sendSignatureCallback(signature);
            return new AsyncTaskResult<>(null);
        } catch (Exception e) {
            MLog.e("Failed", e);
            throw new MusapException(e);
        }
    }
}
