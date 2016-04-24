package jwf.debugport;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;

import jwf.debugport.internal.TelnetServer;

/**
 *
 */
public class DebugPortService extends Service {
    private static final String TAG = "DebugPortService";
    private static final String INTENT_EXTRA_PARAMS = "jwf.debugport.PARAMS";
    private int mPort;
    private TelnetServer mServer;
    private PowerManager.WakeLock mWakeLock;
    private Params mParams;

    /**
     * Utility method to start the DebugPortService.
     * @param params Parameters to configure the service.
     */
    public static void start(Context context, Params params) {
        Intent intent = new Intent(context, DebugPortService.class);
        intent.putExtra(INTENT_EXTRA_PARAMS, params);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Params params = (Params) intent.getParcelableExtra(INTENT_EXTRA_PARAMS);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DebugPortWakeLock");
        mWakeLock.acquire();

        new AsyncTask<Params, Void, Void>() {
            @Override
            protected Void doInBackground(Params... params) {
                try {
                    mServer = new TelnetServer(DebugPortService.this, params[0]);
                    mServer.startServer();
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        }.doInBackground(params);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mServer.killServer();
        mWakeLock.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
