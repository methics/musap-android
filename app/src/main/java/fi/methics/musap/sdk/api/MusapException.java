package fi.methics.musap.sdk.api;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * MUSAP Exception contains MUSAP specific extra details about errors including:
 * <ul>
 *     <li>Error Code: Error specific code (based on MUSAP specification)</li>
 *     <li>Error Name: Human/Machine readable name of the error</li>
 * </ul>
 */
public class MusapException extends Exception {

    public static final int ERROR_WRONG_PARAM   = 101;
    public static final int ERROR_MISSING_PARAM = 102;
    public static final int ERROR_INVALID_ALGORITHM = 103;
    public static final int ERROR_UNKNOWN_KEY   = 105;
    public static final int ERROR_UNSUPPORTED_DATA = 107;
    public static final int ERROR_KEYGEN_UNSUPPORTED = 108;
    public static final int ERROR_BIND_UNSUPPORTED = 109;
    public static final int ERROR_TIMED_OUT     = 208;
    public static final int ERROR_USER_CANCEL   = 401;
    public static final int ERROR_KEY_BLOCKED   = 402;
    public static final int ERROR_SSCD_BLOCKED  = 403;
    public static final int ERROR_INTERNAL      = 900;

    private String errorName;
    private int    errorCode;

    public MusapException(Exception cause) {
        super(cause);
        if (cause instanceof MusapException) {
            this.errorCode = ((MusapException)cause).getErrorCode();
        } else if (cause instanceof InvalidAlgorithmParameterException || cause instanceof NoSuchAlgorithmException) {
            this.errorCode = ERROR_INVALID_ALGORITHM;
        } else if (cause instanceof InvalidKeyException) {
            this.errorCode = ERROR_KEY_BLOCKED;
        } else {
            this.errorCode = ERROR_INTERNAL;
        }
        this.errorName = getErrorName(errorCode);
    }

    public MusapException(String msg) {
        super(msg);
        this.errorCode = ERROR_INTERNAL;
        this.errorName = getErrorName(errorCode);
    }

    public MusapException(int errorCode, Exception e) {
        super(e);
        this.errorCode = errorCode;
        this.errorName = getErrorName(errorCode);
    }

    public MusapException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
        this.errorName = getErrorName(errorCode);
    }

    public MusapException(int errorCode, String msg, Exception e) {
        super(msg, e);
        this.errorCode = errorCode;
        this.errorName = getErrorName(errorCode);
    }
    /**
     * Get a human/machine readable name for this error
     * @return error name (e.g. "internal_error")
     */
    public String getErrorName() {
        return this.errorName;
    }

    /**
     * Get ErrorCode
     * @return error code as integer
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    private String getErrorName(int errorCode) {
        switch (errorCode) {
            case 101: return "wrong_param";
            case 102: return "missing_param";
            case 105: return "unknown_key";
            case 107: return "unsupported_data";
            case 208: return "timed_out";
            case 401: return "user_cancel";
            case 402: return "key_blocked";
            case 403: return "sscd_blocked";
            case 900: return "internal_error";
            default:
                return "internal_error";
        }
    }

}
