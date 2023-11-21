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

    public static void d(String msg) {
        largeLogD(msg);
    }


    public static void d(String tag, String msg) {
        largeLogD(tag, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void e(String msg, Throwable t) {
        Log.e(TAG, msg, t);
    }

    public static void e(String tag, String msg) {
        Log.d(tag, msg);
    }


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
    private static void largeLogD(String content) {
        largeLogD(TAG, content);
    }

}
