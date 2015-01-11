package org.monroe.team.android.box.uc;
import org.monroe.team.android.box.manager.ServiceRegistry;

public abstract class UserCaseSupport<RequestType,ResponseType> implements UserCase <RequestType,ResponseType> {

    private final ServiceRegistry serviceRegistry;

    public UserCaseSupport(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    protected final <ServiceType> ServiceType using(Class<ServiceType> serviceId){
        if (!serviceRegistry.contains(serviceId)) throw new IllegalStateException("Unexpected service = "+serviceId);
        return serviceRegistry.get(serviceId);
    }

}
