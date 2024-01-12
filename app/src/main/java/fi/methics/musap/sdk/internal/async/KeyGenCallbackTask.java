package fi.methics.musap.sdk.internal.async;

import android.content.Context;

import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.util.AsyncTaskResult;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapAsyncTask;

/**
 * Sends MUSAP Key Generation Callback
 */
public class KeyGenCallbackTask extends MusapAsyncTask<Void> {

    private final MusapLink link;
    private final MusapKey key;

    private final String txnId;

    public KeyGenCallbackTask(MusapLink link, MusapKey key, String txnId, MusapCallback<Void> callback, Context context) {
        super(callback, context);
        this.link = link;
        this.key = key;
        this.txnId = txnId;
    }

    @Override
    protected AsyncTaskResult<Void> runOperation() throws MusapException {
        try {
            link.sendKeygenCallback(key, txnId);
            return new AsyncTaskResult<>(null);
        } catch (Exception e) {
            MLog.e("Failed", e);
            throw new MusapException(e);
        }
    }
}
