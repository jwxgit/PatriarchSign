package com.jwx.patriarchsign.imageView;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.activities.BaseActivity;
import com.jwx.patriarchsign.utils.ToastUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ImageViewPager extends BaseActivity {

    public ImageLoader imageLoader = ImageLoader.getInstance();  //获取图片进行管理的工具类实例。

    public ViewPager viewpager;

    public ArrayList<View> viewList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //checkPermission();

    }

    //打开设备取权限
    public void checkPermission() {
        PermissionListener listener = new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, List<String> grantedPermissions) {
                // Successfully.
                if (requestCode == 200) {
                    //setContentView(R.layout.image_activity_main);
                    setContentView(R.layout.activity_agreement);
                    initView();
                }
            }

            @Override
            public void onFailed(int requestCode, List<String> deniedPermissions) {
                // Failure.
                if (requestCode == 200) {
                    ToastUtils.showMessageShort("请开通相关权限，否则无法正常使用本应用！");
                }
            }
        };
        AndPermission.with(this).requestCode(200).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE).callback(listener).start();

    }

    public void initView() {
        //setContentView(R.layout.activity_agreement);
        viewpager = (ViewPager) findViewById(R.id.vp); //获取viewpager
        viewList = new ArrayList<View>();   //保存view，用于PagerAdapter
        for (int i = 0; i < Images.imageUrls.length; i++) {
            new DownLoadPic().execute(i);//图片有几张就下载几张
        }


        viewpager.setAdapter(pagerAdapter);  //加入适配器
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("vp", "postion:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    /**
     * 图片异步下载内部类
     */

    class DownLoadPic extends AsyncTask<Integer, Void, Bitmap> {

        /**
         * 记录每个图片对应的位置
         */
        private int mposition;

        @Override
        protected Bitmap doInBackground(Integer... params) {

            mposition = params[0];//获取传过来的图片position （下标）
            String strurl = Images.imageUrls[mposition];  //通过下标获得图片URL
            File imageFile = new File(getImagePath(strurl)); //获取图片在本地手机中的位置路径
            if (!imageFile.exists()) {  //判断是否存在手机里
                doPost(strurl);//如果没有就下载图片
            }
            if (strurl != null) {
                Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(),
                        290);  //压缩图片  我这里写的是290
                if (bitmap != null) {
                    imageLoader.addBitmapToMemoryCache(strurl, bitmap); //将图片加入缓冲  LruCache中
                    return bitmap;
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Bitmap o) {
            ImageView imageView = new ImageView(ImageViewPager.this);
            imageView.setImageBitmap(o);
            //imageView.setOnClickListener(new View.OnClickListener() {
            // @Override
            // public void onClick(View v) {

            //Intent intent = new Intent(ImageViewPager.this, ImageDetailsActivity.class);//打开图片详情类
            //intent.putExtra("image_position", mposition);
            //startActivity(intent);
            //}
            // });



            viewList.add(imageView);
            pagerAdapter.notifyDataSetChanged();  //这句话一定不能少  ，不然会有异常
            Log.e("TAG2", "" + viewList.size());

        }


    }

    /**
     * ViewPager的适配器  重写下面几个方法就可以了
     */

    PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {

            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(viewList.get(position));

        }

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            container.addView(viewList.get(position));
//            return viewList.get(position);
            String imagePath = getImagePath(Images.imageUrls[position]);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.empty_photo);
            }
            View view = LayoutInflater.from(ImageViewPager.this).inflate(R.layout.zoom_image_layout, null);
            ZoomImageView zoomImageView = (ZoomImageView) view.findViewById(R.id.zoom_image_view);
            Log.e("test====================", ""+ bitmap);
            zoomImageView.setImageBitmap(bitmap);
            container.addView(view);
            return view;
        }

    };


    /**
     * 下载图片方法  并将图片缓冲至手机指定位置中
     *
     * @param urlstr 图片URL
     */
    public void doPost(String urlstr) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e("TAG", "图片未缓存");
        } else {
            Log.e("TAG", "图片已缓存");
        }
        HttpURLConnection con = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        File imageFile = null;
        try {
            URL url = new URL(urlstr);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5 * 1000);
            con.setReadTimeout(15 * 1000);
            con.setDoInput(true);
            con.setDoOutput(true);
            bis = new BufferedInputStream(con.getInputStream());
            imageFile = new File(getImagePath(urlstr));
            fos = new FileOutputStream(imageFile);
            bos = new BufferedOutputStream(fos);
            byte[] b = new byte[1024];
            int length;
            while ((length = bis.read(b)) != -1) {// 写入手机中
                bos.write(b, 0, length);
                bos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (con != null) {
                    con.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (imageFile != null) {
            Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(),
                    290);
            if (bitmap != null) {
                imageLoader.addBitmapToMemoryCache(urlstr, bitmap);
            }
        }
    }

    /**
     * 获取图片的本地存储路径。
     *
     * @param imageUrl 图片的URL地址。
     * @return 图片的本地存储路径。
     */
    private String getImagePath(String imageUrl) {
        int lastSlashIndex = imageUrl.lastIndexOf("/");
        String imageName = imageUrl.substring(lastSlashIndex + 1);
        String imageDir = Environment.getExternalStorageDirectory().getPath() + "/pwxceshibao/";
        File file = new File(imageDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String imagePath = imageDir + imageName;
        return imagePath;
    }



}