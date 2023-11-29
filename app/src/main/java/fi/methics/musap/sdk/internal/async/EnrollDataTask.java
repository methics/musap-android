package fi.methics.musap.sdk.internal.async;

import android.content.Context;

import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.util.AsyncTaskResult;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapAsyncTask;
import fi.methics.musap.sdk.internal.util.MusapStorage;

public class EnrollDataTask extends MusapAsyncTask<Void>  {

    private final MusapLink link;
    private final String fcmToken;

    public EnrollDataTask(MusapLink link, String fcmToken, MusapCallback<Void> callback, Context context) {
        super(callback, context);
        this.link = link;
        this.fcmToken = fcmToken;
    }

    @Override

    protected AsyncTaskResult<Void> runOperation() throws MusapException {
        try {
           String musapId = this.link.enroll(this.fcmToken);
           new MusapStorage(this.context.get()).storeMusapId(musapId);
           return new AsyncTaskResult<>(null);
        } catch (Exception e) {
            MLog.e("Failed", e);
            throw new MusapException(e);
        }
    }
}
