package org.monroe.team.android.box.db;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import org.monroe.team.corebox.utils.Closure;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DAOSupport {

    protected final SQLiteDatabase db;
    private final Schema schema;

    public DAOSupport(SQLiteDatabase db, Schema schema) {
        this.db = db;
        this.schema = schema;
    }

    final protected <Table extends Schema.Table> Table table(Class<Table> tableClass){
        return schema.table(tableClass);
    }

    protected static String[] strs(Object... vals) {
        String[] strings = new String[vals.length];
        for (int i = 0; i < vals.length; i++) {
            strings[i]=String.valueOf(vals[i]);
        }
        return strings;
    }

    protected static List<Result> bakeMany(Cursor cursor, Closure<Cursor,Result> receipt) {
        try {
            List<Result> answer = new ArrayList<Result>(cursor.getCount());
            Result itResult;
            while (cursor.moveToNext()) {
                itResult = receipt.execute(cursor);
                if (itResult != null) answer.add(itResult);
            }
            return answer;
        } finally {
            cursor.close();
        }
    }


    protected static Result bake(Cursor cursor, Closure<Cursor,Result> receipt) {
        try {
            if (!cursor.moveToFirst()) return null;
            return receipt.execute(cursor);
        }finally {
            cursor.close();
        }
    }


    protected static Result result() {
        return new Result();
    }

    protected static ContentBuilder content(){
        return new ContentBuilder();
    }

    protected static class ContentBuilder{

        private final ContentValues contentValues = new ContentValues();

        public <DataType> ContentBuilder value(Schema.VersionTable.ColumnID<DataType> columnID, DataType data){
            if (columnID.dataClass() == Integer.class){
                contentValues.put(columnID.name(), (Integer) data);
            }else if (columnID.dataClass() == String.class){
                contentValues.put(columnID.name(), (String) data);
            }else if (columnID.dataClass() == Long.class){
                contentValues.put(columnID.name(), (Long) data);
            }else if (columnID.dataClass() == Boolean.class){
                contentValues.put(columnID.name(), (Boolean) data);
            }else if (columnID.dataClass() == Float.class){
                contentValues.put(columnID.name(), (Float) data);
            }
            return this;
        }

        public ContentValues get(){
            return contentValues;
        }
    }

    public static class Result {

        private List<Object> fetchedFiledList = new ArrayList<Object>(4);

        static Result answer(){
            return new Result();
        }



        public Result with(Object ... withValues) {
            for (Object withValue : withValues) {
                fetchedFiledList.add(withValue);
            }

            return this;
        }

        @Override
        public String toString() {
            return "Result {" + fetchedFiledList + '}';
        }


        public <Type> Type get(int index, Class<Type> asClass) {
            if (asClass.equals(Date.class)){
                return (Type) new Date(get(index,Long.class));
            }
            return (Type) fetchedFiledList.get(index);
        }
    }



}
