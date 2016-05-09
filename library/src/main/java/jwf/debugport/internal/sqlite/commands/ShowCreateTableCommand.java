package jwf.debugport.internal.sqlite.commands;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwf.debugport.internal.sqlite.SQLiteClientConnection;

/**
 * Print the CREATE TABLE syntax for a table.
 */
public class ShowCreateTableCommand extends SQLiteCommand {
    private static final Pattern command = Pattern.compile("(show\\s+create\\s+table|schema)\\s+(\\w+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    @Override
    public boolean isCommandString(String candidate) {
        return command.matcher(candidate).matches();
    }

    @Override
    public void execute(SQLiteClientConnection connection, PrintWriter out, String commandString) {
        Matcher m = command.matcher(commandString);
        if (!m.matches()) {
            return;
        }

        String tableName = m.group(2);
        if (!isValidTableName(tableName)) {
            out.println("Invalid table name `"+tableName+"`");
        } else {
            SQLiteDatabase db = connection.getCurrentDatabase();
            Cursor c = db.rawQuery("SELECT sql FROM sqlite_master WHERE name = ?", new String[]{tableName});
            if (c.getCount() == 0) {
                out.println("Table `"+tableName+"` does not exist.");
            } else {
                c.moveToNext();
                out.print(c.getString(0));
                out.println(":");
            }
        }
    }
}
