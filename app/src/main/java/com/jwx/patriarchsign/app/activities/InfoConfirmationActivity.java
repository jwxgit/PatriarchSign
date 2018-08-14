package com.jwx.patriarchsign.app.activities;


import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.utils.ToastUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfoConfirmationActivity extends BaseActivity {
    private static final int CODE_IMG_FACE   = 0;
    private static final int CODE_IMG_SIGN   = 1;
    private static final int CODE_IMG_FINGER = 2;
    private TextView tName, tSex, tBirthDay;
    private TableLayout mTableLayout;
    private ImageView signature, photograph, fingerprint;
    private List list = new ArrayList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_confirmation);

        tName = (TextView) findViewById(R.id.name);
        tSex = (TextView) findViewById(R.id.sex);
        tBirthDay = (TextView) findViewById(R.id.birthDay);
        mTableLayout = (TableLayout) findViewById(R.id.TableLayout);

        signature = (ImageView) findViewById(R.id.signature);
        photograph = (ImageView) findViewById(R.id.photograph);
        fingerprint = (ImageView) findViewById(R.id.fingerprint);

        initView();
        //addTableView();
    }


    private void initView() {

        tName.setText(String.valueOf("张三s"));
        mTableLayout.setStretchAllColumns(true);

        photograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("2222222","2" + v);
                changePhoto(null);
            }
        });

        signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("445555","2" + v);
                changeSign(null);
            }
        });

        fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("8888","2" + v);
                changeFinger(null);
            }
        });

    }

    //拍照
    public void changePhoto(View view) {
        PermissionListener listener = new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, List<String> grantedPermissions) {
                // Successfully.
                if (requestCode == 200) {
                    Intent intent = new Intent(InfoConfirmationActivity.this, FaceCollectionActivity.class);
                    startActivityForResult(intent, CODE_IMG_FACE);
                }
            }

            @Override
            public void onFailed(int requestCode, List<String> deniedPermissions) {
                // Failure.
                if (requestCode == 200) {
                    ToastUtils.showMessageShort("请允许相机权限哦！");
                }
            }
        };
        AndPermission.with(this).requestCode(200).permission(Manifest.permission.CAMERA).callback(listener).start();
    }

    //签名
    public void changeSign(View view) {
        Intent intent = new Intent(this, SignCollectActivity.class);
        startActivityForResult(intent, CODE_IMG_SIGN);
    }

    //指纹
    public void changeFinger(View view) {
        Intent intent = new Intent(this, FingerCollectActivity.class);
        startActivityForResult(intent, CODE_IMG_FINGER);
    }

    @Override
    public void back(View view) {
        super.back(view);
    }


    public void  imageViewStep(View view){
        Log.e("","3333:" + view);
        Toast.makeText(this, "view", Toast.LENGTH_SHORT).show();
    }

    public void addTableView() {
        list = Arrays.asList(186,267,148,164,126);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, Color.rgb(221,221,221)); // 边框粗细及颜色
        //drawable.setColor("#ddd"); // 边框内部颜色

        for (int i = 0; i < 2; i++) {
            TableRow tableRow = new TableRow(getBaseContext());
            tableRow.setBackgroundColor(Color.rgb(255, 255, 255));
            for (int j = 0; j < 5; j++) {
                int w = (int) list.get(j);
                Button btn = new Button(this);
                Log.e("","w:" + w);
                btn.setPadding(0, 0, 0, 0);



                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btn.getLayoutParams();
                params.width =  w;
                params.height = 30;
                btn.setLayoutParams(params);


//                btn.setMinWidth(w);
//                btn.setMinHeight(30);
//                btn.setMinimumHeight(30);
//                btn.setMinimumWidth(w);
//                btn.setMaxHeight(30);
//                btn.setMaxWidth(w);
                btn.setId(j+i);
                btn.setTextSize(12);
                btn.setBackground(drawable);
                btn.setText("text:"+ j);

                tableRow.addView(btn);

            }
            Log.e("table--", "table:" + tableRow);
            mTableLayout.addView(tableRow);
        }
    }
}
