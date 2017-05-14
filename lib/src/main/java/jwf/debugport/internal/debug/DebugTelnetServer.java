package jwf.debugport.internal.debug;

import android.content.Context;

import java.net.Socket;
import java.net.UnknownHostException;

import jwf.debugport.Params;
import jwf.debugport.internal.TelnetServer;

/**
 * Implementation of {@link TelnetServer} for a REPL in the Application context.
 */
public class DebugTelnetServer extends TelnetServer<DebugClientConnection> {
    public DebugTelnetServer(Context app, Params params) throws UnknownHostException {
        super(app, params, params.getDebugPort());
    }

    @Override
    public DebugClientConnection getClientConnection(Context c, Socket socket, TelnetServer server, String[] startupCommands) {
        return new DebugClientConnection(c, socket, server, startupCommands);
    }
}
