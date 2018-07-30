package com.jwx.patriarchsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.jwx.patriarchsign.app.activities.DeviceBindActivity;
import com.jwx.patriarchsign.app.activities.IndexActivity;
import com.jwx.patriarchsign.app.activities.IpConfigActivity;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.utils.SpUtils;

public class MainActivity extends AppCompatActivity {

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
        finish();
    }

//    public void testFinger(View view) {
//        startSign("123", "老王", 1, 1, 1);
//    }
//
//    public void ipConfig(View view) {
//        Intent intent = new Intent(this, IpConfigActivity.class);
//        startActivity(intent);
//    }
//
//    public void startSign(String childCode, String childName, int fingerPrints, int signature, int pic) {
//        ParentInfo parentInfo = new ParentInfo();
//        parentInfo.setChildCode(childCode);
//        parentInfo.setChildName(childName);
//        Intent intent = null;
//        if (pic > 0) {
//            intent = new Intent(this, FaceCollectionActivity.class);
//            intent.putExtra("needFinger", fingerPrints > 0);
//            intent.putExtra("needSign", signature > 0);
//        } else if (signature > 0) {
//            intent = new Intent(this, SignCollectActivity.class);
//            intent.putExtra("needFinger", fingerPrints > 0);
//        } else if (fingerPrints > 0) {
//            intent = new Intent(this, FingerCollectActivity.class);
//        }
//        if (intent != null) {
//            intent.putExtra("parentInfo", parentInfo);
//            startActivity(intent);
//        }
//    }
}
