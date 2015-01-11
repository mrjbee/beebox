package org.monroe.team.android.box.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;

import org.monroe.team.corebox.services.ServiceRegistry;

public class AndroidServiceRegistry extends ServiceRegistry{

    private final Context context;

    public AndroidServiceRegistry(Context context) {
        this.context = context;
    }

    @Override
    protected <ServiceType> ServiceType getImpl(Class<ServiceType> serviceId) {
        ServiceType answer = checkAndGetAndroidService(serviceId);
        if (answer != null) return answer;
        return super.getImpl(serviceId);
    }

    @SuppressLint("ServiceCast")
    private <ServiceType> ServiceType checkAndGetAndroidService(Class<ServiceType> serviceId) {
        if (serviceId.equals(NotificationManager.class)){
            return (ServiceType) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }else if (serviceId.equals(AlarmManager.class))
            return (ServiceType) context.getSystemService(Context.ALARM_SERVICE);
        return null;
    }


}
