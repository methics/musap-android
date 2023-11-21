package fi.methics.musap.sdk.internal.util;

import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;

public class SigningResult {

    public MusapSignature signature;
    public MusapException exception;

    public SigningResult(MusapSignature signature) {
        this.signature = signature;
    }

    public SigningResult(MusapException e) {
        this.exception = e;
    }

}