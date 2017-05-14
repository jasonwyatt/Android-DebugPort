package jwf.debugport.internal.sqlite;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jason on 5/9/16.
 */
public class SQLiteOpenHelper {
    private final Application mApp;
    private final String mName;
    private boolean mCreated;
    private SQLiteDatabase mDatabase;

    public SQLiteOpenHelper(SQLiteClientConnection connection, String name) {
        mApp = connection.getApp();
        mName = name;
    }

    public SQLiteDatabase open() {
        if (mDatabase == null) {
            mDatabase = mApp.openOrCreateDatabase(mName, Context.MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
        }
        return mDatabase;
    }

    public void close() {
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
        }
    }
}
