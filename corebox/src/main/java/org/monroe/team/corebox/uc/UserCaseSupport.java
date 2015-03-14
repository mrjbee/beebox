package org.monroe.team.corebox.uc;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.utils.ETD;

public abstract class UserCaseSupport<RequestType,ResponseType> implements UserCase <RequestType,ResponseType> {

    private final ServiceRegistry serviceRegistry;
    private long executionCount = 0;

    public UserCaseSupport(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    protected final <ServiceType> ServiceType using(Class<ServiceType> serviceId){
        if (!serviceRegistry.contains(serviceId)) throw new IllegalStateException("Unexpected service = "+serviceId);
        return serviceRegistry.get(serviceId);
    }

    @Override
    final public ResponseType execute(RequestType request) {
        ETD.NonBlockingStopWatch stopWatch = null;
        try {
            stopWatch = ETD.nonBlockingMeasure(this.getClass().getSimpleName() + " " + (++executionCount), 3000);
            return executeImpl(request);
        } finally {
            stopWatch.stop();
        }
    }

    protected abstract ResponseType executeImpl(RequestType request);
}
