package jwf.debugport.internal.sqlite.commands;

import android.app.Application;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwf.debugport.annotations.Command;
import jwf.debugport.internal.Utils;
import jwf.debugport.internal.sqlite.SQLiteClientConnection;
import jwf.debugport.internal.sqlite.SQLiteOpenHelper;

/**
 * Created by jason on 5/9/16.
 */
@Command.Help(
        format = "CREATE DATABASE [database name];",
        value = "Create a new database called [database name].",
        group = Command.GROUP_SQL_DATABASES
)
public class CreateDatabaseCommand extends SQLiteCommand {
    private static final Pattern command = Pattern.compile("\\s*create\\s+database\\s+(\\w+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    @Override
    public boolean isCommandString(String candidate) {
        return command.matcher(candidate).matches();
    }

    @Override
    public void execute(SQLiteClientConnection connection, PrintWriter out, String commandString) {
        Matcher commandMatcher = command.matcher(commandString);
        if (!commandMatcher.matches()) {
            return;
        }
        String dbName = commandMatcher.group(1);

        if (!isValidDatabaseName(dbName)) {
            out.println("Invalid database name: "+dbName);
            out.flush();
        } else {
            Application app = connection.getApp();

            if (connection.databaseExists(dbName)) {
                out.println("Database `" + dbName + "` already exists");
                return;
            }

            long start = System.nanoTime();
            SQLiteOpenHelper helper = new SQLiteOpenHelper(connection, dbName);
            helper.open();
            long elapsed = System.nanoTime() - start;
            helper.close();
            out.println("Database `" + dbName + "` created in " + Utils.nanosToMillis(elapsed));
        }
    }
}
