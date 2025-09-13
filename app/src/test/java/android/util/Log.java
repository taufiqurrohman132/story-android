package android.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings({"unused", "RedundantReturn"})
public class Log {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    // Selalu kembalikan true agar log statements dianggap dipakai
    public static boolean isLoggable(@NonNull String tag, int level) {
        return true;
    }

    // Dummy log methods
    @SuppressWarnings("unused")
    public static int v(@NonNull String tag, @NonNull String msg) { return 0; }

    @SuppressWarnings("unused")
    public static int v(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) { return 0; }

    @SuppressWarnings("unused")
    public static int d(@NonNull String tag, @NonNull String msg) { return 0; }

    @SuppressWarnings("unused")
    public static int d(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) { return 0; }

    @SuppressWarnings("unused")
    public static int i(@NonNull String tag, @NonNull String msg) { return 0; }

    @SuppressWarnings("unused")
    public static int i(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) { return 0; }

    @SuppressWarnings("unused")
    public static int w(@NonNull String tag, @NonNull String msg) { return 0; }

    @SuppressWarnings("unused")
    public static int w(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) { return 0; }

    @SuppressWarnings("unused")
    public static int e(@NonNull String tag, @NonNull String msg) { return 0; }

    @SuppressWarnings("unused")
    public static int e(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) { return 0; }
}
