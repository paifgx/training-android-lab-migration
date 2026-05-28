package de.garten.training.depotflow.core;

import android.util.Log;

public final class LegacyLogger {

    private static final String PREFIX = "DepotFlow";

    private LegacyLogger() {
    }

    public static void d(String area, String message) {
        Log.d(PREFIX + ":" + area, message);
    }

    public static void e(String area, String message, Throwable error) {
        Log.e(PREFIX + ":" + area, message, error);
    }
}
