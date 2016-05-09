package jwf.debugport.internal;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;

import jwf.debugport.Params;


/**
 *
 */
public abstract class TelnetServer<T extends ClientConnection> implements Runnable {
    private static final String TAG = "TelnetServer";
    private final Object mLock = new Object();
    private final Context mApp;
    private final Params mParams;
    private final int mPort;
    private ServerSocket mServerSocket;
    private Thread mThread;
    private volatile boolean mAlive = false;
    private HashSet<T> mClients = new HashSet<>();

    public TelnetServer(Context app, Params params, int port) throws UnknownHostException {
        mApp = app;
        mParams = params;
        mPort = port;
    }

    public void startServer() throws IOException {
        synchronized (mLock) {
            if (mServerSocket != null && mServerSocket.isBound()) {
                throw new IOException("server already started");
            }
        }
        mThread = new Thread(this);
        mThread.start();
    }

    public void killServer() {
        if (mThread != null) {
            mThread.interrupt();
        }
        mAlive = false;
        synchronized (mLock) {
            for (ClientConnection client : mClients) {
                client.close();
            }
            try {
                mServerSocket.close();
            } catch (IOException e) {
                // m'eh
            }
            mServerSocket = null;
        }
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket(mPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mAlive = true;
        Log.i(TAG, "Server running at "+ Utils.getIpAddress(mApp)+":"+mParams.getPort());
        while (mAlive) {
            Socket client;
            T clientConn;
            if (mServerSocket == null) {
                break;
            }
            try {
                client = mServerSocket.accept();
            } catch (IOException e) {
                if (e.getMessage().equals("Socket closed")) {
                    // no big deal, we are done here..
                    break;
                }
                Log.w(TAG, "An error occurred accepting a client connection.", e);
                continue;
            }

            synchronized (mLock) {
                clientConn = getClientConnection(mApp, client, this, mParams.getStartupCommands());
                mClients.add(clientConn);
            }
            new Thread(clientConn).start();
        }
        Log.i(TAG, "Shutdown.");
    }

    public abstract T getClientConnection(Context c, Socket socket, TelnetServer server, String[] startupCommands);

    public void notifyClosing(T clientConnection) {
        if (!mAlive) {
            // we are dead anyway, no need to worry..
            return;
        }

        synchronized (mLock) {
            mClients.remove(clientConnection);
        }
    }
}
