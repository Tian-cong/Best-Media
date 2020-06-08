package com.tiancong.bestwish.utils;


import android.util.Log;

import com.tiancong.bestwish.BuildConfig;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Formatted Logging helper class.
 */
public class LogHelper {
    private static String TAG = "Best_Media666666666";
    private static final boolean DEBUG;//debug包不需要做限制

    static {
        Log.isLoggable(TAG, Log.VERBOSE);
        DEBUG = true;
    }

    public static void v(Object arg) {
        if (DEBUG) // 对VERBOSE级别日志做限制
        {
            String message = arg == null ? "null" : arg.toString();
            Log.v(TAG, buildMessage("%s", message));
        }

    }

    public static void v(String format, Object... args) {
        if (DEBUG)
            Log.v(TAG, buildMessage(format, args));
    }

    public static void d(Object arg) {
        if (DEBUG) // 对DEBUG级别日志做限制
        {
            String message = arg == null ? "null" : arg.toString();
            Log.d(TAG, buildMessage("%s", message));
        }
    }

    public static void d(String format, Object... args) {
        if (DEBUG)
            Log.d(TAG, buildMessage(format, args));
    }

    public static void e(Object arg) {
        String message = arg == null ? "null" : arg.toString();
        Log.e(TAG, buildMessage("%s", message));
    }

    public static void e(String format, Object... args) {
        Log.e(TAG, buildMessage(format, args));
    }

    public static void i(String format, Object... args) {
        Log.i(TAG, buildMessage(format, args));
    }

    public static void e(Throwable tr, String format, Object... args) {
        Log.e(TAG, buildMessage(format, args), tr);
    }

    public static void wtf(String format, Object... args) {
        Log.wtf(TAG, buildMessage(format, args));
    }

    public static void wtf(Throwable tr, String format, Object... args) {
        Log.wtf(TAG, buildMessage(format, args), tr);
    }

    /**
     * Formats the caller's provided message and prepends useful info like
     * calling thread ID and method name.
     */
    private static String buildMessage(String format, Object... args) {
        String msg = (args == null) ? format : String.format(Locale.US, format, args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogHelper.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                String endWords = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                if (!isNumeric(endWords)) {
                    callingClass = endWords;
                }
                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s",
                Thread.currentThread().getId(), caller, msg);
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]+");
        return pattern.matcher(str).matches();
    }
}

