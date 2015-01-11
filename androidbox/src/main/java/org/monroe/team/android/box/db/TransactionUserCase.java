package org.monroe.team.android.box.db;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

public abstract class TransactionUserCase<RequestType,ResponseType, Dao extends DAOSupport> extends UserCaseSupport<RequestType,ResponseType> {

    public TransactionUserCase(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    final public ResponseType execute(final RequestType request) {
        return using(TransactionManager.class).execute(new TransactionManager.TransactionAction<ResponseType>() {
            @Override
            public ResponseType execute(DAOSupport dao) {
                return transactionalExecute(request, (Dao) dao);
            }
        });
    }

    protected abstract ResponseType transactionalExecute(RequestType request, Dao dao);
}
