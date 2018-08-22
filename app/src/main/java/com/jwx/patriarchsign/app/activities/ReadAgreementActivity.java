package com.jwx.patriarchsign.app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.application.BaseApplication;
import com.jwx.patriarchsign.constant.MessageType;
import com.jwx.patriarchsign.imageView.ImageLoader;
import com.jwx.patriarchsign.imageView.ImageViewPager;
import com.jwx.patriarchsign.imageView.Images;
import com.jwx.patriarchsign.imageView.ZoomImageView;
import com.jwx.patriarchsign.msg.Agreement;
import com.jwx.patriarchsign.msg.ChildInfo;
import com.jwx.patriarchsign.msg.Command;
import com.jwx.patriarchsign.msg.ImageInfo;
import com.jwx.patriarchsign.msg.Inoculation;
import com.jwx.patriarchsign.msg.MessageFactory;
import com.jwx.patriarchsign.msg.SocketMessage;
import com.jwx.patriarchsign.netservice.SocketService;
import com.jwx.patriarchsign.netty.MessageLisener;
import com.jwx.patriarchsign.netty.MessageLisenerRegister;
import com.jwx.patriarchsign.netty.NettyClient;
import com.jwx.patriarchsign.utils.BitmapUtils;
import com.jwx.patriarchsign.utils.FileUtils;
import com.jwx.patriarchsign.utils.ToastUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ReadAgreementActivity extends BaseActivity {
    private CheckBox mCheckBox;
    private Button mBtnagree, mBtndisagree;
    private ListView listView;
    private ViewPager vp;
    BaseAdapter adapter;
    private  ImageViewPager IP;
    private ImageLoader imageLoader = ImageLoader.getInstance();  //获取图片进行管理的工具类实例。
    private ArrayList<View> viewList;
    private List<String> nonexistImages = new ArrayList<>();

    static int Position;
    static List<String> list = new ArrayList<>();
    // 需要展示的知情同意书列表
    private List<String> informes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agreement);
        //发送下载
        for (int i = 0; i < 10; i++) {
            list.add("第" + i + "个协议图片");
        }
        listView = (ListView) findViewById(R.id.listView);
        vp = (ViewPager) findViewById(R.id.vp);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        mBtnagree = (Button) findViewById(R.id.button_agree);
        mBtndisagree = (Button) findViewById(R.id.button_disagree);
        initMessageLisener();
        // 验证健康询问和疫苗图片是否存在是否存在
        nonexistImages.clear();
        // todo 图片加载步骤
        // 1.访问本地缓存，有直接加载
        // 2.如果没有，向服务端发送消息请求图片（异步）
        // 3.监听器接收到服务端发送过来的图片进行缓存起来（一张一张异步发送）
        // 4.设置loading,等图片全部加载完成就进行展示，结束loading
        informes = loadingImages();
        //右边协议书列表
        adapter = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View layout = View.inflate(ReadAgreementActivity.this, R.layout.list_view_text, null);
                ImageView face = (ImageView) layout.findViewById(R.id.text);

                face.setImageResource(R.mipmap.ka_min);
                face.setId(position);

                return layout;
            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int position) {
                return list;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

        };
        //setVp();
        //listView.setAdapter(adapter);
        PermissionListener listener = new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, List<String> grantedPermissions) {
                // Successfully.
                if (requestCode == 200) {
                    setVp();
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

    private void initMessageLisener() {
        MessageLisenerRegister.registMessageLisener(MessageType.SERVER_PUSH_IMAGE, new MessageLisener() {
            @Override
            public void onMessage(SocketMessage message) {
                Object data = message.getData();
                if (data instanceof ImageInfo) {
                    //获取图片后，先写入本地，再缓存起来
                    ImageInfo imageInfo = (ImageInfo) data;
                    String path = BaseApplication.BASE_PATH + imageInfo.getImgMd5() + ".jpg";
                    if (FileUtils.generateFile(imageInfo.getImgContent(), path)) {
                        Log.i(ReadAgreementActivity.class.getName(), "缓存图片：MD5=" + imageInfo.getImgMd5());
                        BaseApplication.IMAGE_CACHE.put(imageInfo.getImgMd5(), path);
                        nonexistImages.remove(imageInfo.getImgMd5());
                    }
                }
                // 为空说明图片都已经加载完成
                if (null == nonexistImages || nonexistImages.size() == 0) {
                    Log.i(ReadAgreementActivity.class.getName(), "图片都已经加载完成......");
                    // todo 去掉loading ，注意loading要能设置超时时间，防止图片没收到一直处于loading
                }
            }
        });

        // 强制退出
        MessageLisenerRegister.registMessageLisener(MessageType.SERVER_SIGNATURE_CANCEL, new MessageLisener() {
            @Override
            public void onMessage(SocketMessage message) {
                Intent intent = new Intent(ReadAgreementActivity.this, IndexActivity.class);
                startActivity(intent);
            }
        });
    }

    // 获取需要展示的知情同意书图片MD5列表
    private List<String> loadingImages() {
        // todo 添加 loading
        // 儿童信息从内存缓存中取
        if (null == BaseApplication.childInfo) {
            Log.e(this.getClass().getName(), "儿童信息为空,返回等待页");
            Intent intent = new Intent(ReadAgreementActivity.this, IndexActivity.class);
            this.startActivity(intent);
        }
        List<String> result = new ArrayList<>();
        ChildInfo childInfo = BaseApplication.childInfo;
        String[] healthMd5 = childInfo.getHealthModels();
        if (null != healthMd5 && healthMd5.length > 0) {
            for (String picMd5 : healthMd5) {
                result.add(picMd5);
            }
        }
        if (null != childInfo.getInoculations() && childInfo.getInoculations().size() > 0) {
            for (Inoculation inoculation : childInfo.getInoculations()) {
                result.add(inoculation.getInocModel());
            }
        }
        // 获取不存在的图片
        for (String md5 : result) {
            if (!TextUtils.isEmpty(md5) && TextUtils.isEmpty(BaseApplication.IMAGE_CACHE.get(md5))) {
                nonexistImages.add(md5);
                NettyClient.sendMessage(MessageFactory.ClientMessages.getClientGetConsentFormMessage(md5));
            }
        }
        return result;
    }



    private void setVp() {
        viewList = new ArrayList<View>();   //保存view，用于PagerAdapter
        for (int i = 0; i < Images.imageUrls.length; i++) {
            new DownLoadPic().execute(i);//图片有几张就下载几张
        }

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemclick);

        //vp.setAdapter(new MyPagerAdapter(this, list));
        vp.setAdapter(pagerAdapter);
        //vp.setPageTransformer(true,new RotateDownPageTransformer());

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Log.e("vp","滑动中=====position:"+ position + "   positionOffset:"+ positionOffset + "   positionOffsetPixels:"+positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("vp", "postion:" + position);

                Position = position;
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.smoothScrollToPosition(Position);
                    }
                });
               listView.performItemClick(listView, position, position);

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
            //Log.e("vp", "adapterView:" + adapterView + "position：" + position);
            vp.setCurrentItem(position);
        }
    };




    //同意阅读协议 进入信息确认界面
    public void intoNextStep(View view) {

        if (!mCheckBox.isChecked()) {
            Toast.makeText(this, "请勾选我已阅读", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = NettyClient.sendMessage(MessageFactory.ClientMessages.getClientAgreementMessage(1));
        if (!success) {
            Toast.makeText(this, "发送同意消息失败", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, InfoConfirmationActivity.class);
        startActivity(intent);

    }
    //取消 跳转到待机画面
    public void disagreeStep(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("是否取消接种?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                // 清除儿童数据
                BaseApplication.childInfo = null;
                if (null != list) list.clear();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void back(View view) {
        super.back(view);
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
            ImageView imageView = new ImageView(ReadAgreementActivity.this);
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
            View view = LayoutInflater.from(ReadAgreementActivity.this).inflate(R.layout.zoom_image_layout, null);
            ZoomImageView zoomImageView = (ZoomImageView) view.findViewById(R.id.zoom_image_view);
        /*    Log.e("test====================", ""+ bitmap);*/
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
