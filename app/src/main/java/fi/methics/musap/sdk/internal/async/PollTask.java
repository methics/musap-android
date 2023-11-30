package fi.methics.musap.sdk.internal.async;

import android.content.Context;

import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.datatype.SignaturePayload;
import fi.methics.musap.sdk.internal.util.AsyncTaskResult;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapAsyncTask;

public class PollTask extends MusapAsyncTask<SignaturePayload> {

    private final MusapLink link;

    public PollTask( MusapLink link, MusapCallback<SignaturePayload> callback, Context context) {
        super(callback, context);
        this.link = link;
    }

    @Override
    protected AsyncTaskResult<SignaturePayload> runOperation() throws MusapException {
        try {
            MLog.d("Polling for a signature");
            SignaturePayload payload = this.link.poll();
            MLog.d("Polled " + payload);
            return new AsyncTaskResult<>(payload);
        } catch (Exception e) {
            MLog.e("Failed to poll", e);
            throw new MusapException(e);
        }
    }
}
