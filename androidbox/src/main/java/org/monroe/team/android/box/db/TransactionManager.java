package org.monroe.team.android.box.db;

import android.database.sqlite.SQLiteDatabase;

public class TransactionManager {

    private final DBHelper helper;

    private SQLiteDatabase dbInstance;
    private DAOSupport dao;

    private final DAOFactory daoFactory;

    public TransactionManager(DBHelper helper, DAOFactory daoFactory) {
        this.helper = helper;
        this.daoFactory = daoFactory;
    }

    public synchronized void prepareResources() {
        if (dbInstance == null){
            dbInstance = helper.getWritableDatabase();
            dao = daoFactory.createInstanceFor(dbInstance);
        }
    }

    public synchronized void releaseResources() {
        if (dbInstance != null){
            dbInstance.close();
            dbInstance = null;
            dao = null;
        }
    }


    public <ResultValue> ResultValue execute(TransactionAction<ResultValue> action) {
        prepareResources();
        dbInstance.beginTransaction();
        ResultValue resultValue;
        try {
            resultValue = action.execute(dao);
            dbInstance.setTransactionSuccessful();
            return resultValue;
        }
        catch(RuntimeException e){
            throw e;
        }finally {
            dbInstance.endTransaction();
        }
    }

    public static interface TransactionAction <ResultValue> {
        public ResultValue execute(DAOSupport dao);
    }
}
