package jwf.debugport.internal;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;

/**
 * Base ClientConnection type.
 */
public abstract class ClientConnection implements Runnable {
    private static final String TAG = "ClientConnection";
    private final Application mApp;
    private final Socket mSocket;
    private final WeakReference<TelnetServer> mParent;
    private final String[] mStartupCommands;
    private String mLogTag;

    public ClientConnection(Context context, Socket client, TelnetServer parent, String[] startupCommands) {
        mApp = (Application) context.getApplicationContext();
        mSocket = client;
        mParent = new WeakReference<TelnetServer>(parent);
        mStartupCommands = startupCommands;
    }

    public Application getApp() {
        return mApp;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public TelnetServer getParent() {
        return mParent.get();
    }

    public String[] getStartupCommands() {
        return mStartupCommands;
    }

    public void close() {
        logInfo("Client closing:" + getSocket().toString());
        TelnetServer parent = getParent();
        if (parent != null) {
            parent.notifyClosing(this);
        }
        try {
            closeConnection();
            getSocket().close();
        } catch (IOException e) {
            // m'eh..
            logError("Error upon closing.", e);
        }
    }

    protected void logInfo(String msg) {
        Log.i(getTag(), msg);
    }

    protected void logDebug(String msg) {
        Log.d(getTag(), msg);
    }

    protected void logError(String msg) {
        Log.e(getTag(), msg);
    }

    protected void logError(String msg, Throwable t) {
        Log.e(getTag(), msg, t);
    }

    private String getTag() {
        if (mLogTag == null) {
            mLogTag = getClass().getSimpleName();
        }
        return mLogTag;
    }

    public abstract void closeConnection();
}
