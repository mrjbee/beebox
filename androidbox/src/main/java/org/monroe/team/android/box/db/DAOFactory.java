package org.monroe.team.android.box.db;

import android.database.sqlite.SQLiteDatabase;

public interface DAOFactory  {
    public DAOSupport createInstanceFor(SQLiteDatabase database);
}
