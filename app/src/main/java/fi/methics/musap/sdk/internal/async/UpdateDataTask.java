package fi.methics.musap.sdk.internal.async;

import android.content.Context;

import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.util.AsyncTaskResult;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapAsyncTask;
import fi.methics.musap.sdk.internal.util.MusapStorage;

public class UpdateDataTask extends MusapAsyncTask<Boolean>  {

    private final MusapLink link;
    private final String fcmToken;

    public UpdateDataTask(MusapLink link, String fcmToken, MusapCallback<Boolean> callback, Context context) {
        super(callback, context);
        this.link = link;
        this.fcmToken = fcmToken;
    }

    @Override

    protected AsyncTaskResult<Boolean> runOperation() throws MusapException {
        try {
           boolean success = this.link.updateFcmToken(this.fcmToken);
           return new AsyncTaskResult<>(success);
        } catch (Exception e) {
            MLog.e("Failed", e);
            throw new MusapException(e);
        }
    }
}
