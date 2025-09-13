// Taruh di: src/test/java/android/util/Log.java
// Package tetap android.util supaya unit test yang pakai Log asli bisa jalan
package android.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Dummy Log class untuk unit test JVM.
 * Tujuannya hanya agar android.util.Log tidak crash saat test.
 * Tidak digunakan di kode produksi.
 */
@SuppressWarnings({"unused", "WeakerAccess", "RedundantReturn", "ConstantReturn"})
public class Log {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    /** Selalu kembalikan true agar log statements dianggap dipakai */
    public static boolean isLoggable(@NonNull String tag, int level) {
        return true;
    }

    // Dummy log methods
    public static int v(@NonNull String tag, @NonNull String msg) { return 0; }
    public static int v(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) { return 0; }
    public static int d(@NonNull String tag, @NonNull String msg) { return 0; }
    public static int d(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) { return 0; }
    public static int i(@NonNull String tag, @NonNull String msg) { return 0; }
    public static int i(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) { return 0; }
    public static int w(@NonNull String tag, @NonNull String msg) { return 0; }
    public static int w(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) { return 0; }
    public static int e(@NonNull String tag, @NonNull String msg) { return 0; }
    public static int e(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) { return 0; }

}
