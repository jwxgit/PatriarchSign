package com.jwx.patriarchsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.jwx.patriarchsign.app.activities.DeviceBindActivity;
import com.jwx.patriarchsign.app.activities.IndexActivity;
import com.jwx.patriarchsign.app.activities.InfoConfirmation;
import com.jwx.patriarchsign.app.activities.IpConfigActivity;
import com.jwx.patriarchsign.app.activities.ReadAgreement;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.imageView.ImageViewPager;
import com.jwx.patriarchsign.socket.AbstractSocketClient;
import com.jwx.patriarchsign.socket.DefaultSocketClient;
import com.jwx.patriarchsign.socket.DefaultUIHandler;
import com.jwx.patriarchsign.socket.UIHandler;
import com.jwx.patriarchsign.utils.SpUtils;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private AbstractSocketClient client;
    private String ip;
    private String port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SpUtils.clear();
        //test 116.7.236.130 20021
        ip = SpUtils.getString(Constances.SP_KEY_ID_ADDRESS, "");
        port = SpUtils.getString(Constances.SP_KEY_PORT, "");
        //int workbenchId = SpUtils.getInt(Constances.SP_KEY_WORKBENCH_ID, -1);

        //ip配置
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            Intent intent = new Intent(this, IpConfigActivity.class);
            startActivity(intent);
            //connectServers("116.7.236.130",20021);
        } else {
            // 待机画面
            Intent intent = new Intent(this, IndexActivity.class);
            startActivity(intent);
            connectServers(ip,Integer.valueOf(port).intValue());

        }
        finish();
    }

    // 打开签核APP 自动连接服务器
    public void connectServers(final String ip, final int port) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (null == client || !client.isConnected()) try {
                    //
                    System.out.println("断线重连.....");
                    UIHandler uiHandler = new DefaultUIHandler();
                    client = new DefaultSocketClient(uiHandler);
                    uiHandler.setSocketClient(client);
                    client.connect(ip,port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 10000);
    }
}
