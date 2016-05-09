package jwf.debugport.internal.sqlite.commands;

import android.app.Application;

import java.io.PrintWriter;
import java.util.regex.Pattern;

import jwf.debugport.annotations.*;
import jwf.debugport.internal.sqlite.SQLiteClientConnection;
import jwf.debugport.internal.sqlite.SQLiteTelnetServer;

/**
 * Created by jason on 5/9/16.
 */
public class ExitCommand extends SQLiteCommand {
    private static final Pattern command = Pattern.compile("^\\s*exit|quit\\s*$", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isCommandString(String candidate) {
        return command.matcher(candidate).matches();
    }

    @Override
    public void execute(SQLiteClientConnection connection, PrintWriter out, String commandString) {
        out.println("Thanks for using Android DebugPort!");
        out.flush();
    }

    @Override
    public boolean isExitCommand() {
        return true;
    }
}
