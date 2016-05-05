package jwf.debugport;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import jwf.debugport.internal.TelnetServer;
import jwf.debugport.internal.Utils;

/**
 *
 */
public class DebugPortService extends Service {
    private static final String TAG = "DebugPortService";
    private static final String INTENT_EXTRA_PARAMS = "jwf.debugport.PARAMS";
    private static final String ACTION_STOP = "jwf.debugport.ACTION_STOP";
    private static final int STOP_REQUEST_CODE = 0;
    private static final int NOTIFICATION_ID = R.id.debugport_notification_id;
    private int mPort;
    private TelnetServer mServer;
    private PowerManager.WakeLock mWakeLock;

    /**
     * Utility method to start the DebugPortService
     */
    public static void start(Context context) {
        start(context, new Params());
    }

    /**
     * Utility method to start the DebugPortService.
     * @param params Parameters to configure the service.
     */
    public static void start(Context context, Params params) {
        Intent intent = new Intent(context, DebugPortService.class);
        intent.putExtra(INTENT_EXTRA_PARAMS, params);
        context.startService(intent);
    }

    /**
     * Kill the currently-running server.
     */
    public static void stop(Context context) {
        Intent intent = new Intent(context, DebugPortService.class);
        context.stopService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_STOP.equals(intent.getAction())) {
            stop(this);
            return START_NOT_STICKY;
        }
        Params params = intent.getParcelableExtra(INTENT_EXTRA_PARAMS);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DebugPortWakeLock");
        mWakeLock.acquire();

        new AsyncTask<Params, Void, Params>() {
            @SuppressWarnings("deprecation")
            @Override
            protected void onPostExecute(Params params) {
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Context context = DebugPortService.this;
                    Notification.Builder builder = new Notification.Builder(DebugPortService.this);
                    builder.setSmallIcon(R.drawable.ic_notification);
                    builder.setContentTitle(getString(R.string.notification_title));
                    builder.setContentText(getString(R.string.notification_subtitle, Utils.getIpAddress(context), params.getPort()));

                    Intent stopIntent = new Intent(context, DebugPortService.class);
                    stopIntent.setAction(ACTION_STOP);
                    PendingIntent pendingStopIntent = PendingIntent.getService(context, STOP_REQUEST_CODE, stopIntent, 0);
                    builder.setDeleteIntent(pendingStopIntent);
                    manager.notify(NOTIFICATION_ID, builder.getNotification());
                }
            }

            @Override
            protected Params doInBackground(Params... params) {
                try {
                    mServer = new TelnetServer(DebugPortService.this, params[0]);
                    mServer.startServer();
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
                return params[0];
            }
        }.execute(params);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
        if (mServer != null) {
            mServer.killServer();
        }
        if (mWakeLock != null) {
            mWakeLock.release();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
