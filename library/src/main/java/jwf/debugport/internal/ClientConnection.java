package jwf.debugport.internal;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;

import bsh.Interpreter;
import jwf.debugport.commands.Commands;

/**
 *
 */
class ClientConnection implements Runnable, Commands.ExitListener {
    private static final String TAG = "ClientConnection";
    private final Socket mClient;
    private final WeakReference<TelnetServer> mParent;
    private final Application mApp;
    private final String[] mStartupCommands;
    private TelnetConsoleInterface mConsole;

    public ClientConnection(Context context, Socket client, TelnetServer parent, String[] startupCommands) {
        mApp = (Application) context.getApplicationContext();
        mClient = client;
        mParent = new WeakReference<>(parent);
        mStartupCommands = startupCommands;
    }

    public void close() {
        Log.i(TAG, "Client closing:" + mClient.toString());
        TelnetServer parent = mParent.get();
        if (parent != null) {
            parent.notifyClosing(this);
        }
        try {
            mClient.close();
        } catch (IOException e) {
            // m'eh..
            Log.e(TAG, "Error upon closing.", e);
        }
    }

    @Override
    public void run() {
        try {
            mConsole = new TelnetConsoleInterface(mClient.getInputStream(), mClient.getOutputStream());
            Interpreter interpreter = new Interpreter(mConsole);
            interpreter.setShowResults(true);
            interpreter.set("cmd", new Commands(this));
            interpreter.set("app", mApp);
            interpreter.eval("importCommands(\"jwf.debugport.commands\")");

            for (String startupCommand : mStartupCommands) {
                interpreter.eval(startupCommand);
            }

            interpreter.setExitOnEOF(false);
            interpreter.run();
        } catch (Exception e) {
            Log.e(TAG, "Error: ", e);
        } finally {
            close();
        }
    }

    @Override
    public void onExit() {
        try {
            mConsole.close();
        } catch (IOException e) {
            // m'eh
        }
        close();
    }
}
