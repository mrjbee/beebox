package org.monroe.team.android.box.utils;

import android.util.Log;

import org.monroe.team.corebox.log.L;

public class AndroidLogImplementation implements L.LogImplementation {

    @Override
    public void d(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void e(String tag, String message) {
        Log.e(tag, message);
    }

    @Override
    public void w(String tag, String message) {
        Log.w(tag, message);
    }

    @Override
    public void i(String tag, String message) {
        Log.i(tag, message);
    }

    @Override
    public void w(String tag, String msg, Throwable e) {
        Log.w(tag, msg, e);
    }

    @Override
    public void e(String tag, String msg, Throwable e) {
        Log.e(tag, msg, e);
    }
}
