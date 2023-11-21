/*
 * (c) Copyright 2003-2020 Methics Oy. All rights reserved.
 */

package fi.methics.musap.sdk.api;

import fi.methics.musap.sdk.api.MusapException;

/**
 * Handles callbacks from MusapClient.
 * @param <T> Type of the success result
 */
public interface MusapCallback<T> {

    /**
     * Called when the operation succeeds.
     * @param result
     */
    void onSuccess(T result);

    /**
     * Called when the operation fails. The exception contains an error code with more information.
     * @param e The exception that caused the operation to fail.
     */
    void onException(MusapException e);

}
