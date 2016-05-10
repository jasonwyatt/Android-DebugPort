package jwf.debugport.internal.sqlite.commands;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.PrintWriter;
import java.util.regex.Pattern;

import jwf.debugport.internal.Utils;
import jwf.debugport.internal.sqlite.SQLiteClientConnection;

/**
 * Created by jason on 5/9/16.
 */
public class ShowTablesCommand extends SQLiteCommand {
    public static final Pattern command = Pattern.compile("show\\s+tables", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    @Override
    public boolean isCommandString(String candidate) {
        return command.matcher(candidate).matches();
    }

    @Override
    public void execute(SQLiteClientConnection connection, PrintWriter out, String commandString) {
        SQLiteDatabase db = connection.getCurrentDatabase();
        if (db == null) {
            connection.printNoDBSelected(out);
            return;
        }

        Cursor c = db.rawQuery("SELECT name AS 'Table' FROM sqlite_master WHERE type = 'table' ORDER BY name", null);
        Utils.printTable(out, c);
    }
}
