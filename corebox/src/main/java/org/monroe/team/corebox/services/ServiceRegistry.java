package org.monroe.team.corebox.services;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {

    private final Map<Class, Object> model = new HashMap<Class, Object>();

    final public <ServiceType> boolean contains(Class<ServiceType> serviceId) {
        return model.containsKey(serviceId);
    }

    final public <ServiceType> ServiceType get(Class<ServiceType> serviceId) {
        return getImpl(serviceId);
    }

    protected  <ServiceType> ServiceType getImpl(Class<ServiceType> serviceId) {
        return (ServiceType) model.get(serviceId);
    }

    final public <ServiceType> void registrate(Class<ServiceType> serviceId, ServiceType service){
        model.put(serviceId, service);
    }

}
