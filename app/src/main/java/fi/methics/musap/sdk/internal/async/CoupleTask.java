package fi.methics.musap.sdk.internal.async;

import android.content.Context;

import java.io.IOException;

import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.datatype.RelyingParty;
import fi.methics.musap.sdk.internal.util.AsyncTaskResult;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapAsyncTask;
import fi.methics.musap.sdk.internal.util.MusapStorage;

/**
 * Runs Musap Link coupling operation async to prevent
 * network operations on the main thread.
 */
public class CoupleTask extends MusapAsyncTask<RelyingParty> {

    private final MusapLink link;
    private final String couplingCode;
    private final String appId;

    public CoupleTask(MusapLink link, String couplingCode, String appId, MusapCallback<RelyingParty> callback, Context context) {
        super(callback, context);
        this.link = link;
        this.couplingCode = couplingCode;
        this.appId = appId;
    }

    @Override
    protected AsyncTaskResult<RelyingParty> runOperation() throws MusapException {
        try {
            // TODO: Add proper error handling
            RelyingParty rp = link.couple(this.couplingCode, this.appId);
            if (rp == null) {
                throw new MusapException("Wrong coupling code");
            }

            new MusapStorage(this.context.get()).storeRelyingParty(rp);
            return new AsyncTaskResult<>(rp);
        } catch (Exception e) {
            MLog.e("Failed", e);
            throw new MusapException(e);
        }
    }
}
