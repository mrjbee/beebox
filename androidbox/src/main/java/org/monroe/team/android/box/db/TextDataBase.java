package org.monroe.team.android.box.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.P;

import java.util.ArrayList;
import java.util.List;

public final class TextDataBase {

    private TransactionManager mTransactionManger;
    private Class[] mTableClassList;
    private Context mContext;
    private String mAppId;
    private int mVersion;

    public TextDataBase(Context context, String appId, int version, TextDataProvider... textDataProviders) {
        mContext = context;
        mAppId = appId;
        mVersion = version;
        mTableClassList = new Class[textDataProviders.length];
        for (int i=0; i< textDataProviders.length; i++){
            mTableClassList[i] = textDataProviders[i].mDataTable;
            textDataProviders[i].mTextDataBase = this;
        }
    }

    public synchronized TransactionManager getTransactionManger() {
        if (mTransactionManger == null){
            final TextDataBaseSchema textDataBaseSchema = new TextDataBaseSchema(mVersion, mAppId, mTableClassList);
            mTransactionManger = new TransactionManager(new DBHelper(mContext, textDataBaseSchema), new DAOFactory() {
                @Override
                public DAOSupport createInstanceFor(SQLiteDatabase database) {
                    return new TextDataBaseDao(database, textDataBaseSchema);
                }
            });
        }
        return mTransactionManger;
    }

    private static class TextDataBaseDao extends DAOSupport{
        public TextDataBaseDao(SQLiteDatabase db, Schema schema) {
            super(db, schema);
        }

        public List<String> getIdList(Class<? extends TextDataTable> tableClass) {
            TextDataTable dataTable = table(tableClass);
            Cursor cursor = db.query(dataTable.TABLE_NAME,
                    strs(dataTable._ID.name(),
                         dataTable._DATA.name()),
                    null,
                    null,
                    null,
                    null,
                    null);
            return collect(cursor, new Closure<Cursor, String>() {
                @Override
                public String execute(Cursor arg) {
                    return arg.getString(0);
                }
            });
        }

        private <ResultType> List<ResultType> collect(Cursor cursor, Closure<Cursor, ResultType> closure) {
            List<ResultType> answer = new ArrayList<ResultType>(cursor.getCount());
            ResultType itResult;
            while (cursor.moveToNext()) {
                itResult = closure.execute(cursor);
                if (itResult != null) answer.add(itResult);
            }
            cursor.close();
            return answer;
        }

        public <DataType> List<DataType> getValues(Class<? extends TextDataTable> tableClass, final TextDataAdapter<DataType> mTextDataAdapter) {
            TextDataTable dataTable = table(tableClass);
            Cursor cursor = db.query(dataTable.TABLE_NAME,
                    strs(dataTable._ID.name(),
                            dataTable._DATA.name()),
                    null,
                    null,
                    null,
                    null,
                    null);
            return collect(cursor, new Closure<Cursor, DataType>() {
                @Override
                public DataType execute(Cursor arg) {
                    String key = arg.getString(0);
                    String data = arg.isNull(1)? null:arg.getString(1);
                    return mTextDataAdapter.toData(key, data);
                }
            });
        }

        public <DataType> DataType get(String id, Class<? extends TextDataTable> tableClass, TextDataAdapter<DataType> dataAdapter) {
            TextDataTable dataTable = table(tableClass);
            String whereStatement = null;
            String[] whereArgs = null;
            whereStatement = dataTable._ID.name()+" is ?";
            whereArgs = strs(id);

            Cursor cursor = db.query(dataTable.TABLE_NAME,
                    strs(dataTable._ID.name(),
                            dataTable._DATA.name()),
                    whereStatement,
                    whereArgs,
                    null,
                    null,
                    null);
            List<Result> list = collect(cursor, new Closure<Cursor, Result>() {
                @Override
                public Result execute(Cursor arg) {
                    return result().with(
                            arg.getString(0),
                            arg.isNull(1) ? null:arg.getString(1));
                }
            });

            if (list.size() > 1) throw new IllegalStateException("Two much values for "+dataTable.getDataName());
            if (list.isEmpty()) return null;
            return dataAdapter.toData(
                    list.get(0).get(0, String.class),
                    list.get(0).get(1, String.class));
        }

        public int delete(String id, Class<? extends TextDataTable> tableClass) {
            TextDataTable dataTable = table(tableClass);
            String whereStatement = null;
            String[] whereArgs = null;
            whereStatement = dataTable._ID.name()+" is ?";
            whereArgs = strs(id);
            int res = db.delete(dataTable.TABLE_NAME,
                    whereStatement,
                    whereArgs);
            return res;
        }

        public void insert(String id, String value, Class<? extends TextDataTable> tableClass) {
            TextDataTable dataTable = table(tableClass);
            long id_row = db.insertOrThrow(
                    dataTable.TABLE_NAME,
                    null,
                    content()
                            .value(dataTable._ID, id)
                            .value(dataTable._DATA, value)
                            .get());
        }
    }

    private static class TextDataBaseSchema extends Schema{
        public TextDataBaseSchema(int version, String appId, Class<? extends Table>... tables) {
            super(version, appId+"_app", tables);
        }
    }

    public static abstract class TextDataTable extends Schema.VersionTable {

        public final String TABLE_NAME;
        public final ColumnID<String> _ID = Schema.column("_id", String.class);
        public final ColumnID<String> _DATA = Schema.column("_data", String.class);

        public TextDataTable() {
            TABLE_NAME = getDataName()+"_text_data";
            define(1, TABLE_NAME)
                    .column(_ID, "TEXT NOT NULL PRIMARY KEY")
                    .column(_DATA, "TEXT");
        }

        public abstract String getDataName();
    }

    public interface TextDataAdapter<DataType> {
        public DataType toData(String id, String textData);
        public P<String, String> toIdText(DataType data);
    }

    private static abstract class TextDataBaseTransactionAction<ResultValue> implements TransactionManager.TransactionAction<ResultValue>{

        @Override
        final public ResultValue execute(DAOSupport dao) {
            return execute((TextDataBaseDao) dao);
        }

        public abstract ResultValue execute(TextDataBaseDao dao);
    }

    public static class TextDataProvider<DataType>{

        private final Class<? extends TextDataTable> mDataTable;
        private final TextDataAdapter<DataType> mTextDataAdapter;
        private TextDataBase mTextDataBase;

        public TextDataProvider(Class<? extends TextDataTable> mDataTable, TextDataAdapter<DataType> mTextDataAdapter) {
            this.mDataTable = mDataTable;
            this.mTextDataAdapter = mTextDataAdapter;
        }

        public synchronized int getSize(){
            return getIdList().size();
        }

        public synchronized List<String> getIdList(){
           return getTransactionManager().execute(new TextDataBaseTransactionAction<List<String>>() {
               @Override
               public List<String> execute(TextDataBaseDao dao) {
                   return dao.getIdList(mDataTable);
               }
           });
        }

        private TransactionManager getTransactionManager() {
            if (mTextDataBase ==null) throw new IllegalStateException("Provider not registered");
            return mTextDataBase.getTransactionManger();
        }

        public synchronized List<DataType> getValueList(){
            return getTransactionManager().execute(new TextDataBaseTransactionAction<List<DataType>>() {
                @Override
                public List<DataType> execute(TextDataBaseDao dao) {
                    return dao.getValues(mDataTable, mTextDataAdapter);
                }
            });
        }

        public synchronized DataType putData(final DataType data){
            return getTransactionManager().execute(new TextDataBaseTransactionAction<DataType>() {
                @Override
                public DataType execute(TextDataBaseDao dao) {
                    P<String, String> idTextData = mTextDataAdapter.toIdText(data);
                    DataType wasValue = internal_get(dao, idTextData.first);
                    internal_delete(dao, idTextData.first);
                    internal_insert(dao, idTextData.first, idTextData.second);
                    return wasValue;
                }
            });
        }

        public synchronized DataType deleteData(final String id){
            return getTransactionManager().execute(new TextDataBaseTransactionAction<DataType>() {
                @Override
                public DataType execute(TextDataBaseDao dao) {
                    DataType wasValue = internal_get(dao, id);
                    internal_delete(dao, id);
                    return wasValue;
                }
            });
        }

        public synchronized DataType getData(final String id){
            return getTransactionManager().execute(new TextDataBaseTransactionAction<DataType>() {
                @Override
                public DataType execute(TextDataBaseDao dao) {
                    DataType wasValue = internal_get(dao, id);
                    return wasValue;
                }
            });
        }

        private void internal_insert(TextDataBaseDao dao, String id, String value) {
            dao.insert(id, value, mDataTable);
        }

        private void internal_delete(TextDataBaseDao dao, String id) {
            dao.delete(id, mDataTable);
        }

        private DataType internal_get(TextDataBaseDao dao, String id) {
            return dao.get(id, mDataTable, mTextDataAdapter);
        }
    }

}
