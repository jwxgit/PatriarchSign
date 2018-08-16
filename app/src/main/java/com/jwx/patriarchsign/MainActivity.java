package com.jwx.patriarchsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.jwx.patriarchsign.app.activities.IndexActivity;
import com.jwx.patriarchsign.app.activities.IpConfigActivity;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.netservice.SocketService;
import com.jwx.patriarchsign.socket.AbstractSocketClient;
import com.jwx.patriarchsign.utils.SpUtils;


public class MainActivity extends AppCompatActivity {
    private AbstractSocketClient client;
    private String ip;
    private String port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ip = SpUtils.getString(Constances.SP_KEY_ID_ADDRESS, "");
        port = SpUtils.getString(Constances.SP_KEY_PORT, "");
        //ip配置
        Intent intent;
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
              intent = new Intent(this, IpConfigActivity.class);
        } else {
            // 待机画面
              intent = new Intent(this, IndexActivity.class);
        }
        startActivity(intent);

        //开启后台监听数据服务
        Intent sericeIntent = new Intent(this, SocketService.class);
        this.startService(sericeIntent);

        finish();
    }
}
