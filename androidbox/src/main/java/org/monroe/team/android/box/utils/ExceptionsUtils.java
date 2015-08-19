package org.monroe.team.android.box.utils;

import org.monroe.team.android.box.data.Data;

public class ExceptionsUtils {

    public static Throwable resolveDataFetchException(Data.FetchError error){
        return error.originalException();
    }

    public static Throwable resolveDataFetchException(Data.FetchException error){
        return resolveDataFetchException(error.error);
    }

    public static RuntimeException asRuntime(Throwable throwable) {
        if (throwable instanceof RuntimeException) return (RuntimeException) throwable;
        return new RuntimeException(throwable);
    }
}
