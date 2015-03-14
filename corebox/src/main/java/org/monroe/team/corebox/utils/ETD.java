package org.monroe.team.corebox.utils;

import org.monroe.team.corebox.log.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ETD {

    private final static LogCondition NO_OP = new LogCondition() {
        @Override
        public boolean log(long ms, String[] results) {
            return true;
        }
    };

    private static final Map<String, StopWatch> stopWatchMap = new HashMap<>();

    public static StopWatch measure(String name){
        StopWatch stopWatch = new StopWatch(name, System.currentTimeMillis());
        if(stopWatchMap.put(name,stopWatch)!=null){
            L.w("EDT", "Previous measure still run. Id = " + name);
        }
        return stopWatch;
    }

    public static NonBlockingStopWatch nonBlockingMeasure(String name){
        return nonBlockingMeasure(name, -1);
    }

    public static NonBlockingStopWatch nonBlockingMeasure(String name, long warnAfterMs){
        return new NonBlockingStopWatch(name, System.currentTimeMillis(), warnAfterMs);
    }

    public synchronized static long stopMeasure(String name,  String... results) {
        return stopMeasure(name, NO_OP, results);
    }

    public synchronized static long stopMeasure(String name, LogCondition condition, String... results) {
        StopWatch watch = stopWatchMap.remove(name);
        if (watch == null){
            L.w("EDT","There is no running with id = "+name);
            return -1;
        }

        long stopMs = System.currentTimeMillis();
        long delta = stopMs - watch.startMs;

        String resultString = buildResultString(results);
        if (condition.log(delta,results)) {
            String time = timeToString(delta);
            L.i("ETD", "Name = " + name + " time=" + time+ " result=" + resultString);
            long startMs = watch.startMs;
            for (Stage stage: watch.stageList){
               L.i("ETD", "  -- stage = "+stage.name+" time="+ timeToString(stage.time - startMs)+ " result=" + buildResultString(stage.results));
               startMs = stage.time;
            }
        }
        return delta;
    }

    private static String timeToString(long delta) {
        return delta/1000+"sec "+delta%1000+"ms";
    }


    private static String buildResultString(String[] results) {
        String resultString = "[";
        for (String res:results){
            resultString+=res+";";
        }
        resultString+="]";
        return resultString;
    }

    public static class StopWatch{

        private final String Id;
        private final long startMs;
        private final List<Stage> stageList = new ArrayList<>();

        public StopWatch(String id, long startMs) {
            Id = id;
            this.startMs = startMs;
        }

        public long stop(String ... resultMsg){
            return this.stop(NO_OP, resultMsg);
        }

        public long stop(LogCondition logCondition, String ... resultMsg){
            return ETD.stopMeasure(this.Id, logCondition, resultMsg);
        }

        public void stage(String stageName, String ...resultMs){
            stageList.add(new Stage(stageName,System.currentTimeMillis(),resultMs));
        }
    }

    private static class Stage{

        private final String name;
        private final long time;
        private final String[] results;

        private Stage(String name, long time, String[] results) {
            this.name = name;
            this.time = time;
            this.results = results;
        }
    }

    public static interface LogCondition{
        public boolean log(long ms, String[] results);
    }

    public static LogCondition moreThenMs(final long conditionMs){
        return new LogCondition() {
            @Override
            public boolean log(long ms, String[] results) {
                return ms >= conditionMs;
            }
        };
    }

    public static LogCondition moreThenSeconds(final long seconds){
        return new LogCondition() {
            @Override
            public boolean log(long ms, String[] results) {
                return DateUtils.asSeconds(ms) >= seconds;
            }
        };
    }

    public static class NonBlockingStopWatch {

        private final String name;
        private final long startMs;
        private final long warnMs;
        private final Thread thread;

        private long lastWarnMs;
        private boolean alive = true;

        public NonBlockingStopWatch(String name, long startMs, long warnMs) {
            this.name = name;
            this.startMs = startMs;
            this.warnMs = warnMs;
            lastWarnMs = startMs;
            thread = new Thread(){
                @Override
                public void run() {
                    while (alive || isInterrupted()){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            break;
                        }
                        checkWarnMeasure();
                    }
                    doStop();
                }
            };
            thread.start();
        }

        private synchronized void checkWarnMeasure() {
            if (warnMs < 1){
                return;
            }

            long curTime = System.currentTimeMillis();
            long delta = curTime - lastWarnMs;
            if (delta > warnMs){
                //do warn here
                lastWarnMs = curTime;
                L.w("ETD", "Name = " + name + " actual = "+timeToString(delta)+" expected = "+timeToString(warnMs));
            }
        }

        private synchronized void doStop() {
            checkWarnMeasure();
            long delta = System.currentTimeMillis() - startMs;
            L.i("ETD", "Name = " + name + " execution time = "+timeToString(delta));
        }

        public synchronized void stop(){
            alive = false;
            thread.interrupt();
        }
    }
}
