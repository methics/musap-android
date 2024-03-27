package fi.methics.musap.sdk.internal.util;

import android.util.Log;

/**
 * Logging utility method to put all calls to Android logging util to
 * one place
 */
public class MLog {

    private static boolean debugEnabled = true;
    private static boolean testMode     = false;

    public static void setDebugEnabled(boolean isEnabled) {
        debugEnabled = isEnabled;
    }
    public static void setTestMode(boolean isEnabled) {
        testMode = isEnabled;
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
     * @param msg debug message
     * @param t   throwable
     */
    public static void d(String msg, Throwable t) {
        if (testMode) {
            System.out.println( msg);
            return;
        }
        Log.d(TAG, msg, t);
    }

    /**
     * Log an error
     * @param msg error message
     */
    public static void e(String msg) {
        if (testMode) {
            System.err.println( msg);
            return;
        }
        Log.e(TAG, msg);
    }

    /**
     * Log an error with stack trace
     * @param msg error message
     * @param t   throwable
     */
    public static void e(String msg, Throwable t) {
        if (testMode) {
            System.err.println( msg);
            return;
        }
        Log.e(TAG, msg, t);
    }

    /**
     * Log an error with custom tag
     * @param tag TAG
     * @param msg error message
     */
    public static void e(String tag, String msg) {
        if (testMode) {
            System.err.println(tag + ": " + msg);
            return;
        }
        Log.e(tag, msg);
    }

    /**
     * Split large content to multiple log lines to avoid losing details
     * @param tag     Tag to log with
     * @param content Content to log
     */
    private static void largeLogD(String tag, String content) {
        if (testMode) {
            System.out.println(tag + ": " + content);
            return;
        }
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
