package jwf.debugport;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * No-op implementation of DebugPortService.
 */
public class DebugPortService extends Service {
    public static final String METADATA_DEBUG_PORT = "jwf.debugport.METADATA_DEBUG_PORT";
    public static final String METADATA_SQLITE_PORT = "jwf.debugport.METADATA_SQLITE_PORT";
    public static final String METADATA_STARTUP_COMMANDS = "jwf.debugport.METADATA_STARTUP_COMMANDS";

    public static Params start(Context context) {
        Params params = new Params();
        return params;
    }

    public static void start(Context context, Params params) {
    }

    public static void stop(Context context) {
    }

    public static void kill(Context context) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
