package fi.methics.musap.sdk.internal.async;

import android.content.Context;

import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.SscdInfo;
import fi.methics.musap.sdk.internal.discovery.KeyBindReq;
import fi.methics.musap.sdk.internal.discovery.AndroidMetadataStorage;
import fi.methics.musap.sdk.internal.discovery.SharedPrefStorage;
import fi.methics.musap.sdk.internal.util.AsyncTaskResult;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapAsyncTask;
import fi.methics.musap.sdk.internal.util.MusapSscd;

public class BindKeyTask extends MusapAsyncTask<MusapKey> {

    private final MusapSscd sscd;
    private final KeyBindReq req;

    public BindKeyTask(MusapCallback<MusapKey> callback, Context context, MusapSscd sscd, KeyBindReq req) {
        super(callback, context);
        this.sscd = sscd;
        this.req  = req;
    }

    @Override
    protected AsyncTaskResult<MusapKey> runOperation() throws MusapException {
        try {
            MusapKey key = sscd.bindKey(req);
            MLog.d("BindKeyTask Got MUSAP key");
            AndroidMetadataStorage storage = new AndroidMetadataStorage(new SharedPrefStorage(context.get()));

            SscdInfo activeSscd = sscd.getSscdInfo();
            String       sscdId = sscd.getSscdId();
            key.setSscdId(sscdId);

            storage.addKey(key, activeSscd);
            return new AsyncTaskResult<>(key);
        } catch (MusapException e) {
            throw e;
        } catch (Exception e) {
            throw new MusapException(e);
        }
    }
}
