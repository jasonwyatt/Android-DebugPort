package jwf.debugport.app;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Locale;

import jwf.debugport.DebugPortService;
import jwf.debugport.Params;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.server_toggle);
        if (toggle != null) {
            toggle.setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        TextView debugStatus = (TextView) findViewById(R.id.debug_server_status);
        TextView sqliteStatus = (TextView) findViewById(R.id.sqlite_server_status);
        if (isChecked) {
            Params params = new Params()
                    .setStartupCommands(new String[]{
                            "import android.os.*;",
                            "import java.util.*;",
                            "x = 1+1;",
                    });
            DebugPortService.start(this, params);
            if (debugStatus != null) {
                debugStatus.setText(getString(R.string.debug_server_status, getIpAddress(), params.getDebugPort()));
                debugStatus.setVisibility(View.VISIBLE);
            }
            if (sqliteStatus != null) {
                sqliteStatus.setText(getString(R.string.sqlite_server_status, getIpAddress(), params.getSQLitePort()));
                sqliteStatus.setVisibility(View.VISIBLE);
            }
        } else {
            DebugPortService.stop(this);
            if (debugStatus != null) {
                debugStatus.setVisibility(View.GONE);
            }
            if (sqliteStatus != null) {
                sqliteStatus.setVisibility(View.GONE);
            }
        }
    }

    public String getIpAddress() {
        WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        return String.format(Locale.getDefault(), "%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
    }
}
