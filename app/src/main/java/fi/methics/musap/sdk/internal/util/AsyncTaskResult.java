/*
 * (c) Copyright 2003-2020 Methics Oy. All rights reserved.
 */

package fi.methics.musap.sdk.internal.util;

public class AsyncTaskResult<T> {

    private T result;
    private Exception error;

    public T getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }

    public AsyncTaskResult(T result) {
        super();
        this.result = result;
    }

    public AsyncTaskResult(Exception error) {
        super();
        this.error = error;
    }
}
