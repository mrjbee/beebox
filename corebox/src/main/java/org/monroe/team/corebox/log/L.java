package org.monroe.team.corebox.log;


final public class L {
    private L() {}

    private static LogImplementation implementation = new DisabledLog();

    public static Logger DEBUG = create("DEBUG");

    public static void setup(LogImplementation implementation){
        L.implementation = implementation;
    }

    public static void d(String TAG, String msg, Object ... args) {
        String message = formatMessage(msg, args);
        implementation.d(TAG, message);
    }

    public static void i(String TAG, String msg, Object ... args) {
        String message = formatMessage(msg, args);
        implementation.i(TAG, message);
    }

    public static void w(String TAG, String msg, Object ... args) {
        String message = formatMessage(msg, args);
        implementation.w(TAG, message);
    }

    public static void e(String TAG, String msg, Object ... args) {
        String message = formatMessage(msg, args);
        implementation.e(TAG, message);
    }

    public static void w(String TAG, String msg, Throwable e) {
        implementation.w(TAG, msg,e);
    }

    public static void e(String TAG, String msg, Throwable e) {
        implementation.e(TAG, msg,e);
    }

    public static Logger create(String TAG){
        return new Logger(TAG);
    }

    public interface LogImplementation {
        void d(String tag, String message);
        void e(String tag, String message);
        void w(String tag, String message);
        void i(String tag, String message);
        void w(String tag, String msg, Throwable e);
        void e(String tag, String msg, Throwable e);
    }

    public static class Logger {

        private final String TAG;

        public Logger(String tag) {
            TAG = tag;
        }

        @Override
        public String toString() {
            return "Logger{" +
                    "TAG='" + TAG + '\'' +
                    '}';
        }

        public void d(String msg, Object ... args){
            L.d(TAG, msg, args);
        }
        public void e(String msg, Object ... args){
            L.e(TAG, msg, args);
        }
        public void w(String msg, Object ... args){
            L.w(TAG, msg, args);
        }
        public void i(String msg, Object ... args){
            L.i(TAG, msg, args);
        }

        public void w(String msg, Throwable e){
            L.w(TAG, msg, e);
        }
        public void e(String msg, Throwable e){
            L.e(TAG, msg, e);
        }
    }


    private static String formatMessage(String msg, Object[] args) {
        if (args == null || args.length == 0) return msg;
        return String.format(msg, args);
    }

    public static class DisabledLog implements LogImplementation{
        @Override
        public void d(String tag, String message) {

        }

        @Override
        public void e(String tag, String message) {

        }

        @Override
        public void w(String tag, String message) {

        }

        @Override
        public void i(String tag, String message) {

        }

        @Override
        public void w(String tag, String msg, Throwable e) {

        }

        @Override
        public void e(String tag, String msg, Throwable e) {

        }
    }

}
