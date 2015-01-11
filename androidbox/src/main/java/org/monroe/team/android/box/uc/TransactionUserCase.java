package org.monroe.team.android.box.uc;

import org.monroe.team.android.box.db.DAOSupport;
import org.monroe.team.android.box.db.TransactionManager;
import org.monroe.team.android.box.manager.ServiceRegistry;

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
