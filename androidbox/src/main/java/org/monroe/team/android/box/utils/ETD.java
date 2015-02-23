package org.monroe.team.android.box.utils;

import android.util.Log;

import org.monroe.team.corebox.utils.DateUtils;

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
            Log.w("EDT","Previous measure still run. Id = "+name);
        }
        return stopWatch;
    }

    public synchronized static long stopMeasure(String name,  String... results) {
        return stopMeasure(name, NO_OP, results);
    }

    public synchronized static long stopMeasure(String name, LogCondition condition, String... results) {
        StopWatch watch = stopWatchMap.remove(name);
        if (watch == null){
            Log.w("EDT","There is no running with id = "+name);
            return -1;
        }

        long stopMs = System.currentTimeMillis();
        long delta = stopMs - watch.startMs;

        String resultString = buildResultString(results);
        if (condition.log(delta,results)) {
            String time = timeToString(delta);
            Log.i("ETD", "Name = " + name + " time=" + time+ " result=" + resultString);
            long startMs = watch.startMs;
            for (Stage stage: watch.stageList){
               Log.i("ETD", "  -- stage = "+stage.name+" time="+ timeToString(stage.time - startMs)+ " result=" + buildResultString(stage.results));
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
}
