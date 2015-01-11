package org.monroe.team.android.box.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.monroe.team.android.box.Closure;

public class DBHelper extends SQLiteOpenHelper {

    private final Schema schema;

    public DBHelper(Context context, Schema schema) {
        super(context, schema.name, null, schema.version);
        this.schema = schema;
    }

    public void onCreate(final SQLiteDatabase db) {
        schema.withEachTable(new Closure<Schema.Table, Void>() {
            @Override
            public Void execute(Schema.Table table) {
                db.execSQL(table.createScript());
                return null;
            }
        });
    }

    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int version = oldVersion+1; version <= newVersion; version++ ) {
            final int finalVersion = version;
            schema.withEachTable(new Closure<Schema.Table, Void>() {
                @Override
                public Void execute(Schema.Table table) {
                    table.alterColumns(finalVersion, new Closure<String,Void>() {
                        @Override
                        public Void execute(String sql) {
                            db.execSQL(sql);
                            return null;
                        }
                    });
                    return null;
                }
            });
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new IllegalStateException("Not supported");
    }
}
