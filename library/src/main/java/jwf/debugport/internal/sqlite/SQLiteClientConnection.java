package jwf.debugport.internal.sqlite;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.util.Scanner;

import jwf.debugport.internal.ClientConnection;
import jwf.debugport.internal.TelnetServer;
import jwf.debugport.internal.Utils;
import jwf.debugport.internal.sqlite.commands.Commands;
import jwf.debugport.internal.sqlite.commands.SQLiteCommand;

/**
 * ClientConnection implementation for an SQLite REPL with MySQL-style params.
 */
public class SQLiteClientConnection extends ClientConnection {
    private SQLiteOpenHelper mCurrentDb;

    public SQLiteClientConnection(Context context, Socket client, TelnetServer parent, String[] startupCommands) {
        super(context, client, parent, startupCommands);
    }

    @Override
    public void closeConnection() {
        if (mCurrentDb != null) {
            mCurrentDb.close();
        }
    }

    @Override
    public void run() {
        try {
            Socket s = getSocket();
            Scanner in = new Scanner(s.getInputStream());
            in.useDelimiter(";");
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            out.println("SQLite Database REPL");
            boolean wasExitCommand = false;
            do {
                out.println();
                out.print("sqlite> ");
                out.flush();

                String commandCandidate = in.next().trim();
                SQLiteCommand command = Commands.getInstance().getCommand(commandCandidate);
                if (command != null) {
                    command.execute(this, out, commandCandidate);
                    wasExitCommand = command.isExitCommand();
                } else {
                    if (mCurrentDb != null) {
                        SQLiteDatabase db = mCurrentDb.open();
                        long start = System.nanoTime();
                        try {
                            Cursor c = db.rawQuery(commandCandidate, null);
                            int rows = c.getCount();
                            long elapsed = System.nanoTime() - start;
                            Utils.printTable(out, c);

                            if (rows > 0) {
                                out.println(rows + " rows in set (" + Utils.nanosToMillis(elapsed) + ")");
                            } else {
                                out.println("Query executed in " + Utils.nanosToMillis(elapsed) + ".");
                            }
                        } catch (SQLiteException sqle) {
                            out.println(sqle.getMessage());
                        }
                    } else {
                        printNoDBSelected(out);
                    }
                }
            } while (!wasExitCommand);
        } catch (Exception e) {
            logError("Error:", e);
        } finally {
            close();
        }
    }

    public void printNoDBSelected(PrintWriter out) {
        out.println("No database currently selected. Call `use [table name];` to select one, or `show databases;` to list available databases.");
    }

    public void setCurrentDatabase(String dbName) {
        if (mCurrentDb != null) {
            mCurrentDb.close();
        }
        mCurrentDb = new SQLiteOpenHelper(this, dbName);
        mCurrentDb.open();
    }

    public SQLiteDatabase getCurrentDatabase() {
        if (mCurrentDb == null) {
            return null;
        }
        return mCurrentDb.open();
    }

    public boolean databaseExists(String dbName) {
        String[] databases = getApp().databaseList();
        boolean exists = false;
        for (String existingDb : databases) {
            if (existingDb.equals(dbName)) {
                return true;
            }
        }
        return false;
    }
}
