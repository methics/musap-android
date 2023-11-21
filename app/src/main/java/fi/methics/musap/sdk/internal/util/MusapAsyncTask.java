/*
 * (c) Copyright 2003-2020 Methics Oy. All rights reserved.
 */

package fi.methics.musap.sdk.internal.util;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import fi.methics.musap.sdk.api.MusapCallback;
import fi.methics.musap.sdk.api.MusapException;

public abstract class MusapAsyncTask<T> extends AsyncTask<Void, Void, AsyncTaskResult<T>> {

    private final MusapCallback<T> callback;
    protected final WeakReference<Context> context;

    public MusapAsyncTask(MusapCallback<T> callback, Context context) {
        this.callback = callback;
        this.context = new WeakReference<>(context);
    }

    @Override
    protected final AsyncTaskResult<T> doInBackground(Void... v) {
        AsyncTaskResult<T> asyncTaskResult;
        try {
            asyncTaskResult = this.runOperation();
        } catch (MusapException e) {
            asyncTaskResult = new AsyncTaskResult<>(e);
        } catch (Exception e) {
            asyncTaskResult = new AsyncTaskResult<>(new MusapException(e));
        }

        return asyncTaskResult;
    }

    @Override
    protected final void onPostExecute(AsyncTaskResult<T> result) {
        // Sometimes the caller does not need a callback
        if (this.callback == null) {
            return;
        }

        if (result.getError() != null) {
            if (result.getError() instanceof MusapException) {
                this.callback.onException((MusapException) result.getError());
            } else {
                this.callback.onException(new MusapException(result.getError()));
            }
        } else {
            this.callback.onSuccess(result.getResult());
        }
    }


    protected abstract AsyncTaskResult<T> runOperation() throws MusapException;

}
