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
public class TelnetServer implements Runnable {
    private static final String TAG = "TelnetServer";
    private final Object mLock = new Object();
    private final Context mApp;
    private final Params mParams;
    private ServerSocket mServerSocket;
    private Thread mThread;
    private volatile boolean mAlive = false;
    private HashSet<ClientConnection> mClients = new HashSet<>();

    public TelnetServer(Context app, Params params) throws UnknownHostException {
        mApp = app;
        mParams = params;
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
            mServerSocket = new ServerSocket(mParams.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mAlive = true;
        Log.i(TAG, "Server running at "+ Utils.getIpAddress(mApp)+":"+mParams.getPort());
        while (mAlive) {
            Socket client;
            ClientConnection clientConn = null;
            if (mServerSocket == null) {
                continue;
            }
            try {
                client = mServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "An error occurred accepting a client connection.", e);
                continue;
            }

            synchronized (mLock) {
                clientConn = new ClientConnection(mApp, client, this);
                mClients.add(clientConn);
            }
            new Thread(clientConn).start();
        }
        Log.i(TAG, "Shutdown.");
    }

    public void notifyClosing(ClientConnection clientConnection) {
        if (!mAlive) {
            // we are dead anyway, no need to worry..
            return;
        }

        synchronized (mLock) {
            mClients.remove(clientConnection);
        }
    }
}
