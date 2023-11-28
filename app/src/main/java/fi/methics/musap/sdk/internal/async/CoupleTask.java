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

    public CoupleTask(MusapLink link, MusapCallback<Boolean> callback, Context context) {
        super(callback, context);
        this.link = link;
    }

    @Override
    protected AsyncTaskResult<Boolean> runOperation() throws MusapException {
        try {
            boolean success = link.couple();
            return new AsyncTaskResult<>(success);
        } catch (Exception e) {
            MLog.e("Failed", e);
            return new AsyncTaskResult<>(false);
        }
    }
}
