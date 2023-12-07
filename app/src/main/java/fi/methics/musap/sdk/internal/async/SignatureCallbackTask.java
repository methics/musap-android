package fi.methics.musap.sdk.internal.async;

import android.content.Context;

import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.util.AsyncTaskResult;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapAsyncTask;

/**
 * Sends MUSAP Signature Callback
 */
public class SignatureCallbackTask extends MusapAsyncTask<Void> {

    private final MusapLink link;
    private final MusapSignature signature;

    private final String txnId;

    public SignatureCallbackTask(MusapLink link, MusapSignature signature, String txnId, MusapCallback<Void> callback, Context context) {
        super(callback, context);
        this.link = link;
        this.signature = signature;
        this.txnId = txnId;
    }

    @Override
    protected AsyncTaskResult<Void> runOperation() throws MusapException {
        try {
            link.sendSignatureCallback(signature, txnId);
            return new AsyncTaskResult<>(null);
        } catch (Exception e) {
            MLog.e("Failed", e);
            throw new MusapException(e);
        }
    }
}
