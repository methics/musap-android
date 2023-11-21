package fi.methics.musap.sdk.internal.async;

import android.content.Context;

import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapSscd;
import fi.methics.musap.sdk.internal.discovery.KeyBindReq;
import fi.methics.musap.sdk.internal.discovery.MetadataStorage;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.util.AsyncTaskResult;
import fi.methics.musap.sdk.internal.util.IdGenerator;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapAsyncTask;

public class BindKeyTask extends MusapAsyncTask<MusapKey> {

    private final MusapSscdInterface sscd;
    private final KeyBindReq req;

    public BindKeyTask(MusapCallback<MusapKey> callback, Context context, MusapSscdInterface sscd, KeyBindReq req) {
        super(callback, context);
        this.sscd = sscd;
        this.req  = req;
    }

    @Override
    protected AsyncTaskResult<MusapKey> runOperation() throws MusapException {
        try {
            MusapKey key = sscd.bindKey(req);
            MLog.d("BindKeyTask Got MUSAP key");
            MetadataStorage storage = new MetadataStorage(context.get());

            MusapSscd activeSscd = sscd.getSscdInfo();
            String        sscdId = sscd.generateSscdId(key);
            activeSscd.setSscdId(sscdId);
            key.setSscdId(sscdId);

            storage.addKey(key, activeSscd);
            return new AsyncTaskResult<>(key);
        } catch (Exception e) {
            throw new MusapException(e);
        }
    }
}
