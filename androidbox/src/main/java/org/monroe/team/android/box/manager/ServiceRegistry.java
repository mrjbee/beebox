package org.monroe.team.android.box.manager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {

    private final Map<Class, Object> model = new HashMap<Class, Object>();
    public final Context context;

    public ServiceRegistry(Context context) {
        this.context = context;
    }

    final public <ServiceType> boolean contains(Class<ServiceType> serviceId) {
        return model.containsKey(serviceId);
    }

    final public <ServiceType> ServiceType get(Class<ServiceType> serviceId) {
        ServiceType answer = checkAndGetAndroidService(serviceId);
        if (answer != null) return answer;
        return (ServiceType) model.get(serviceId);
    }

    @SuppressLint("ServiceCast")
    private <ServiceType> ServiceType checkAndGetAndroidService(Class<ServiceType> serviceId) {
        if (serviceId.equals(NotificationManager.class)){
            return (ServiceType) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }else if (serviceId.equals(AlarmManager.class))
            return (ServiceType) context.getSystemService(Context.ALARM_SERVICE);
        return null;
    }

    final public <ServiceType> void registrate(Class<ServiceType> serviceId, ServiceType service){
        model.put(serviceId, service);
    }

}
