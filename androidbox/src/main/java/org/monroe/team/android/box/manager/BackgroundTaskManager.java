package org.monroe.team.android.box.manager;

import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public class BackgroundTaskManager {

    private final ExecutorService taskExecutionService = Executors.newCachedThreadPool();

    final public synchronized <ResultType> BackgroundTask<ResultType> execute(
            Callable<ResultType> callable) {
        return execute(callable, null);
    }

    final public synchronized <ResultType> BackgroundTask<ResultType> execute(
                                Callable<ResultType> callable,
                                TaskCompletionNotificationObserver<ResultType> observer) {
        Future<ResultType> future = taskExecutionService.submit(new CoverCallable<ResultType>(callable, observer));
        BackgroundTask<ResultType> answer = new BackgroundTask<ResultType>(future, observer);
        return answer;
    }

    public static class BackgroundTask <Result> {

        private final Future<Result> actualFuture;

        public BackgroundTask(Future<Result> actualFuture, TaskCompletionNotificationObserver<Result> observer) {
            this.actualFuture = actualFuture;
        }

        public void cancel() {
            actualFuture.cancel(true);
        }
    }

    public static interface TaskCompletionNotificationObserver<Result>{
        public void onSuccess(Result result);
        public void onFails(Exception e);
    }

    private static class CoverCallable<ResultType> implements Callable<ResultType>{

        private final Callable<ResultType> originalCallable;
        private final TaskCompletionNotificationObserver<ResultType> observer;

        private CoverCallable(Callable<ResultType> originalCallable, TaskCompletionNotificationObserver<ResultType> observer) {
            this.originalCallable = originalCallable;
            this.observer = observer;
        }

        @Override
        public ResultType call() throws Exception {
            ResultType result =  null;
            try {
                result = originalCallable.call();
            } catch (Exception e){
                if (observer != null){
                    observer.onFails(e);
                }
                throw e;
            }
            if (observer != null) {
                observer.onSuccess(result);
            }
            return result;
        }
    }
}
