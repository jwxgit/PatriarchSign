package com.jwx.patriarchsign.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.viewpage.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class ReadAgreement extends BaseActivity {
    private CheckBox mCheckBox;
    private Button mBtnagree, mBtndisagree;
    private ListView listView;
    private ViewPager vp;

    static int Position;
    static List<String> list = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agreement);

        for (int i = 0; i < 10; i++) {
            list.add("第" + i + "个协议图片");
        }

        listView = (ListView) findViewById(R.id.listView);
        vp = (ViewPager) findViewById(R.id.vp);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        mBtnagree = (Button) findViewById(R.id.button_agree);
        mBtndisagree = (Button) findViewById(R.id.button_disagree);

        setVp();


    }


    private void setVp() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_view_text, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemclick);

        vp.setAdapter(new MyPagerAdapter(this, list));
        //vp.setPageTransformer(true,new RotateDownPageTransformer());

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Log.e("vp","滑动中=====position:"+ position + "   positionOffset:"+ positionOffset + "   positionOffsetPixels:"+positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("vp", "显示页改变=====postion:" + position);

                Position = position;
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.smoothScrollToPosition(Position);
                    }
                });


            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        // Log.e("vp","状态改变=====SCROLL_STATE_IDLE====静止状态");
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        //Log.e("vp","状态改变=====SCROLL_STATE_DRAGGING==滑动状态");
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        //Log.e("vp","状态改变=====SCROLL_STATE_SETTLING==滑翔状态");
                        break;
                }
            }
        });

    }

    AdapterView.OnItemClickListener itemclick = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Log.e("vp", "adapterView:" + adapterView + "position：" + position);
            vp.setCurrentItem(position);
        }
    };

    public void intoNextStep(View view) {

        Log.e("Checked", "是否选中isChecked:" + mCheckBox.isChecked());
        if(mCheckBox.isChecked()){
            Intent intent = new Intent(this, InfoConfirmation.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "请勾选我已阅读", Toast.LENGTH_SHORT).show();
        }
    }

    public void disagreeStep(View view){
        Intent intent = new Intent(this, DeviceBindActivity.class);
        startActivity(intent);
    }

    @Override
    public void back(View view) {
        super.back(view);
    }
}
