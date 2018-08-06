package com.jwx.patriarchsign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.jwx.patriarchsign.app.activities.DeviceBindActivity;
import com.jwx.patriarchsign.app.activities.IndexActivity;
import com.jwx.patriarchsign.app.activities.IpConfigActivity;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.socket.RecorderManager;
import com.jwx.patriarchsign.socket.ScreenShot;
import com.jwx.patriarchsign.utils.BitmapUtils;
import com.jwx.patriarchsign.utils.SpUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ScreenShot screenShot;
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
//        RecorderManager.getInstance(this).startRecorder(this,0.5f);
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if(null == screenShot) {
//                    screenShot = new ScreenShot(MainActivity.this);
//                }
//                if(screenShot.isConnected())
//                    return;
//                //重新连接
//                System.out.println("重新连接服务端.....");
//                screenShot.connect("192.168.199.110",10000);
//            }
//        },0,10000);

        finish();
    }
}
