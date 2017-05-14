package jwf.debugport.internal.sqlite.commands;

import android.app.Application;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwf.debugport.annotations.Command;
import jwf.debugport.internal.Utils;
import jwf.debugport.internal.sqlite.SQLiteClientConnection;

/**
 * Created by jason on 5/9/16.
 */
@Command.Help(
        format = "DROP DATABASE [database name];",
        value = "Drop the database named [database name] from the app's collection of databases.",
        group = Command.GROUP_SQL_DATABASES
)
public class DropDatabaseCommand extends SQLiteCommand {
    private static final Pattern command = Pattern.compile("\\s*drop\\s+database\\s+(\\w+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

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

            String[] databases = app.databaseList();
            boolean exists = false;
            if (!connection.databaseExists(dbName)) {
                out.println("Database with name `"+dbName+"` does not exist");
                return;
            }

            long start = System.nanoTime();
            app.deleteDatabase(dbName);
            long elapsed = System.nanoTime() - start;
            out.println("Database `" + dbName + "` dropped in " + Utils.nanosToMillis(elapsed));
        }
    }
}
