package fi.methics.musap.sdk.internal.async;

import android.content.Context;

import java.io.IOException;

import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.util.AsyncTaskResult;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapAsyncTask;

/**
 * Runs Musap Link coupling operation async to prevent
 * network operations on the main thread.
 */
public class CoupleTask extends MusapAsyncTask<Boolean> {

    private final MusapLink link;
    private final String couplingCode;
    private final String appId;

    public CoupleTask(MusapLink link, String couplingCode, String appId, MusapCallback<Boolean> callback, Context context) {
        super(callback, context);
        this.link = link;
        this.couplingCode = couplingCode;
        this.appId = appId;
    }

    @Override
    protected AsyncTaskResult<Boolean> runOperation() throws MusapException {
        try {
            boolean success = link.couple(this.couplingCode, this.appId);
            return new AsyncTaskResult<>(success);
        } catch (Exception e) {
            MLog.e("Failed", e);
            throw new MusapException(e);
        }
    }
}
