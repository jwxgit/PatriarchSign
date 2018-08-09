package com.jwx.patriarchsign.app.activities;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.jwx.patriarchsign.R;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfoConfirmation extends BaseActivity {
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

        signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("","222:" + v);
            }
        });
    }


    @Override
    public void back(View view) {
        super.back(view);
    }


    public void  imageViewStep(View v){
        Log.e("","3333:" + v);
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
