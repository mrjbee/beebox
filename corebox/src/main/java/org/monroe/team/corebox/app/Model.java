package org.monroe.team.corebox.app;

import org.monroe.team.corebox.services.BackgroundTaskManager;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCase;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public abstract class Model {

    protected final ServiceRegistry serviceRegistry;
    protected final BackgroundTaskManager backgroundTaskManager = new BackgroundTaskManager();
    protected final String applicationName;

    protected Model(String appName, ServiceRegistry serviceRegistry) {
        applicationName = appName;
        this.serviceRegistry = serviceRegistry;
        this.serviceRegistry.registrate(Model.class, this);
        this.serviceRegistry.registrate(BackgroundTaskManager.class,backgroundTaskManager);
    }

    public abstract UCResponseHandler getResponseHandler();

    public <RequestType,ResponseType> BackgroundTaskManager.BackgroundTask<ResponseType> execute(
            Class<? extends UserCase<RequestType,ResponseType>> ucId,
            final RequestType request, final BackgroundResultCallback<ResponseType> callback){

        final UserCase<RequestType,ResponseType> uc = getUserCase(ucId);

        return backgroundTaskManager.execute(new Callable<ResponseType>() {
            @Override
            public ResponseType call() throws Exception {
                return uc.execute(request);
            }
        }, new BackgroundTaskManager.TaskCompletionNotificationObserver<ResponseType>() {
            @Override
            public void onSuccess(final ResponseType o) {
                getResponseHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(o);
                        callback.onDone();
                    }
                });
            }

            @Override
            public void onFails(final Exception e) {
                if (e instanceof ExecutionException){
                    getResponseHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFails(e.getCause());
                            callback.onDone();
                        }
                    });
                } else if(e instanceof RuntimeException) {
                    getResponseHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFails(e);
                            callback.onDone();
                        }
                    });
                } else {
                    getResponseHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCancel();
                            callback.onDone();
                        }
                    });
                }
            }
        });
    }

    public <RequestType,ResponseType> ResponseType execute(
            Class<? extends UserCase<RequestType,ResponseType>> ucId,
            final RequestType request){
        final UserCase<RequestType,ResponseType> uc = getUserCase(ucId);
        return uc.execute(request);
    }

    private <RequestType,ResponseType> UserCase<RequestType, ResponseType> getUserCase(Class<? extends UserCase<RequestType, ResponseType>> ucId) {
        if (!serviceRegistry.contains(ucId)){
            UserCase<RequestType, ResponseType> ucInstance;
            try {
                if (UserCaseSupport.class.isAssignableFrom(ucId)) {
                    ucInstance = ucId.getConstructor(ServiceRegistry.class).newInstance(serviceRegistry);
                } else {
                    ucInstance = ucId.newInstance();
                }
                serviceRegistry.registrate((Class<UserCase<RequestType, ResponseType>>) ucId,ucInstance);
            } catch (Exception e) {
                throw new RuntimeException("Error during creating uc = "+ucId, e);
            }
        }
        return serviceRegistry.get(ucId);
    }

    public <Type> Type usingService(Class<Type> serviceClass) {
        return serviceRegistry.get(serviceClass);
    }

    public static abstract class BackgroundResultCallback<ResponseType> {
        abstract public void onResult(ResponseType response);
        public void onCancel(){}
        public void onFails(Throwable e){}
        public void onDone(){}
    }

   public static interface UCResponseHandler {
       public void post(Runnable r);
   }
}
