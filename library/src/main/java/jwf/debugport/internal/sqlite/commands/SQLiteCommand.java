package jwf.debugport.internal.sqlite.commands;

import android.app.Application;

import java.io.PrintWriter;
import java.util.regex.Pattern;

import jwf.debugport.internal.sqlite.SQLiteClientConnection;
import jwf.debugport.internal.sqlite.SQLiteTelnetServer;

/**
 * Abstract command..
 */
public abstract class SQLiteCommand {
    protected static final Pattern DATABASE_NAME = Pattern.compile("[a-zA-Z][_a-zA-Z0-9]*");
    protected static final Pattern TABLE_NAME = Pattern.compile("[a-zA-Z][_a-zA-Z0-9]*");

    /**
     * Whether or not the given string is a match for this command.
     */
    public abstract boolean isCommandString(String candidate);

    /**
     * Execute the command.
     */
    public abstract void execute(SQLiteClientConnection connection, PrintWriter out, String commandString);

    public boolean isExitCommand() {
        return false;
    }

    protected final boolean isValidDatabaseName(String dbName) {
        return DATABASE_NAME.matcher(dbName).matches();
    }

    protected final boolean isValidTableName(String tableName) {
        return TABLE_NAME.matcher(tableName).matches();
    }
}
