package com.jwx.patriarchsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.jwx.patriarchsign.app.activities.DeviceBindActivity;
import com.jwx.patriarchsign.app.activities.IndexActivity;
import com.jwx.patriarchsign.app.activities.IpConfigActivity;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.socket.AbstractSocketClient;
import com.jwx.patriarchsign.socket.DefaultSocketClient;
import com.jwx.patriarchsign.socket.DefaultUIHandler;
import com.jwx.patriarchsign.socket.UIHandler;
import com.jwx.patriarchsign.utils.SpUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private AbstractSocketClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        String ip = SpUtils.getString(Constances.SP_KEY_ID_ADDRESS, "");
        String port = SpUtils.getString(Constances.SP_KEY_PORT, "");
        int workbenchId = SpUtils.getInt(Constances.SP_KEY_WORKBENCH_ID, -1);
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            Intent intent = new Intent(this, IpConfigActivity.class);
            startActivity(intent);
        } else {
            if (workbenchId == -1) {
                Intent intent = new Intent(this, DeviceBindActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, IndexActivity.class);
                startActivity(intent);
            }
        }

        // 设置

//        RecorderManager.getInstance(this).startRecorder(this,0.5f);
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if(null == client) {
//                    UIHandler uiHandler = new DefaultUIHandler();
//                    AbstractSocketClient socketClient = new DefaultSocketClient(uiHandler);
//                    uiHandler.setSocketClient(socketClient);
//                    socketClient.connect("198.168.199.110",10000);
//                }
//
//            }
//        },0,10000);

        finish();
    }
}
