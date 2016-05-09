package jwf.debugport.internal.sqlite;

import android.content.Context;

import java.net.Socket;
import java.net.UnknownHostException;

import jwf.debugport.Params;
import jwf.debugport.internal.TelnetServer;

/**
 * Implementation of {@link TelnetServer} to support a REPL to access SQLite Databases.
 */
public class SQLiteTelnetServer extends TelnetServer<SQLiteClientConnection> {
    public SQLiteTelnetServer(Context app, Params params) throws UnknownHostException {
        super(app, params, params.getSQLitePort());
    }

    @Override
    public SQLiteClientConnection getClientConnection(Context c, Socket socket, TelnetServer server, String[] startupCommands) {
        return new SQLiteClientConnection(c, socket, server, startupCommands);
    }
}
