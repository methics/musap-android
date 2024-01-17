package fi.methics.musap.sdk.internal.async;

import android.content.Context;

import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.SscdInfo;
import fi.methics.musap.sdk.internal.discovery.MetadataStorage;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.util.AsyncTaskResult;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapAsyncTask;
import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.internal.util.MusapSscd;

public class GenerateKeyTask extends MusapAsyncTask<MusapKey> {

    private final MusapSscd sscd;
    private final KeyGenReq req;

    public GenerateKeyTask(MusapCallback<MusapKey> callback, Context context, MusapSscd sscd, KeyGenReq req) {
        super(callback, context);
        this.sscd = sscd;
        this.req  = req;
    }

    @Override
    protected AsyncTaskResult<MusapKey> runOperation() throws MusapException {
        try {
            MusapKey key = sscd.generateKey(req);
            MLog.d("GenerateKeyTask Got MUSAP key");
            MetadataStorage storage = new MetadataStorage(context.get());

            SscdInfo activeSscd = sscd.getSscdInfo();
            String       sscdId = sscd.getSscdId();
            key.setSscdId(sscdId);

            storage.addKey(key, activeSscd);
            return new AsyncTaskResult<>(key);
        } catch (MusapException e) {
            throw e;
        } catch (Exception e) {
            MLog.e("Failed to generate key", e);
            throw new MusapException(e);
        }
    }
}
