package fi.methics.musap.sdk.internal.util;

import android.util.Log;

/**
 * Logging utility method to put all calls to Android logging util to
 * one place
 */
public class MLog {

    private static boolean debugEnabled = true;

    public static void setDebugEnabled(boolean isEnabled) {
        debugEnabled = isEnabled;
    }

    private static final String TAG = "MUSAPLog";

    /**
     * Log a debug message
     * @param msg debug message
     */
    public static void d(String msg) {
        largeLogD(msg);
    }

    /**
     * Log a debug message with tag
     * @param tag TAG
     * @param msg debug message
     */
    public static void d(String tag, String msg) {
        largeLogD(tag, msg);
    }

    /**
     * Log an error on debug level
     * @param msg error log
     * @param t   throwable
     */
    public static void d(String msg, Throwable t) {
        Log.d(TAG, msg, t);
    }

    /**
     * Log an error
     * @param msg error message
     */
    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    /**
     * Log an error with stack trace
     * @param msg error message
     * @param t   throwable
     */
    public static void e(String msg, Throwable t) {
        Log.e(TAG, msg, t);
    }

    /**
     * Log an error with custom tag
     * @param tag TAG
     * @param msg error message
     */
    public static void e(String tag, String msg) {
        Log.d(tag, msg);
    }

    /**
     * Split large content to multiple log lines to avoid losing details
     * @param tag     Tag to log with
     * @param content Content to log
     */
    private static void largeLogD(String tag, String content) {
        if (!debugEnabled) {
            return;
        }

        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLogD(content.substring(4000));
        } else {
            Log.d(tag, content);
        }

    }

    /**
     * Split large content to multiple log lines to avoid losing details
     * @param content Content to log
     */
    private static void largeLogD(String content) {
        largeLogD(TAG, content);
    }

}
