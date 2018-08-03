package com.jwx.patriarchsign.socket;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * @author wurenqing
 * @since 2018-08-03
 */

public class RecorderManager {


    private static final String TAG = RecorderManager.class.getName();
    public static final byte VERSION = 1;
    private static RecorderManager sManager;
    private Context mContext;
    private Handler mCompressHandler;
    private List<ClientHandler> mClientHandlers = new ArrayList<>();
    private Bitmap mDrawingBoard;
    private Canvas mCanvas = new Canvas();
    private View rootView;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    private Runnable mDrawTask = new DrawTask();
    private Runnable mCompressTask = new CompressTask();

    private final int MAX_CLIENT_COUNT = 10;
    //    private final float fps = 60f;
//    private final int delay = (int) (1000 / fps);
    private Socket socket;

    public static synchronized RecorderManager getInstance(Context context) {
        if (sManager == null) {
            sManager = new RecorderManager(context);
        }
        return sManager;
    }

    private RecorderManager(Context context) {
        this.mContext = context.getApplicationContext();
        new HandlerThread("Compress-Thread") {
            @Override
            protected void onLooperPrepared() {
                super.onLooperPrepared();
                mCompressHandler = new Handler();
            }
        }.start();
        startListen();

    }

    private void startListen() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                for (int i = 8080; i < 65535; i++) {
//                    try {
//                        serverSocket = new ServerSocket(i);
//                        final int port = i;
//                        mUIHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(mContext, "端口: " + port, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        break;
//                    } catch (IOException e) {
//                    }
//                }
                for (int i = 0; i < MAX_CLIENT_COUNT; ) {
                    try {
                        socket = serverSocket.accept();
                        new HandlerThread("Client-Thread") {
                            @Override
                            protected void onLooperPrepared() {
                                super.onLooperPrepared();
                                mClientHandlers.add(new ClientHandler(socket));
                            }
                        }.start();
                        i++;
                    } catch (IOException e) {
                        return;
                    }
                }

            }
        }.start();

    }

    public void stopRecorder() {

        rootView = null;
        mUIHandler.removeCallbacks(mDrawTask);
        if (mCompressHandler != null) {
            mCompressHandler.getLooper().quit();
        }
        for (ClientHandler clientHandler : mClientHandlers) {
            clientHandler.getLooper().quit();
        }
        try {
            socket.close();
        } catch (Exception e) {

        }
        sManager = null;
    }

    /**
     * API14(ICE_CREAM_SANDWICH)及以上版本全局初始化一次即可,context任意,可以是activity也可以是其他。
     * 以下版本需在每个activity的onResume中初始化,context需要传当前activity。
     *
     * @param context API14(ICE_CREAM_SANDWICH)以下传当前activty,其他版本建议传当前activty也可以是任意context
     * @param scale   实际传输图像尺寸与手机屏幕比例
     */
    public void startRecorder(final Context context, float scale) {
        Point point = getScreenSize(context);
        int exceptW = (int) (point.x * scale);
        int exceptH = (int) (point.y * scale);
        if (mDrawingBoard == null) {
            mDrawingBoard = Bitmap.createBitmap(exceptW, exceptH, Bitmap.Config.RGB_565);
        }
        if (mDrawingBoard.getWidth() != exceptW || mDrawingBoard.getHeight() != exceptH) {
            mDrawingBoard.recycle();
            mDrawingBoard = Bitmap.createBitmap(exceptW, exceptH, Bitmap.Config.RGB_565);
        }
        mCanvas.setBitmap(mDrawingBoard);
        mCanvas.scale(scale, scale);
        if (context instanceof Activity) {
            startRecorderActivity(((Activity) context));
        } else {
            Toast.makeText(context, "请下拉一下通知栏试试", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksAdapter() {
                @Override
                public void onActivityResumed(Activity activity) {
                    startRecorderActivity(activity);
                }
            });
        }
    }


    private static Point getScreenSize(Context context) {
        int w = context.getResources().getDisplayMetrics().widthPixels;
        int h = context.getResources().getDisplayMetrics().heightPixels;

        return new Point(w, h);
    }

    private void startRecorderActivity(Activity activity) {
        rootView = activity.getWindow().getDecorView();
        mUIHandler.removeCallbacks(mDrawTask);
        mUIHandler.post(mDrawTask);
    }


    private class DrawTask implements Runnable {
        @Override
        public void run() {
            if (rootView == null) {
                return;
            }
            mUIHandler.removeCallbacks(mDrawTask);
            rootView.draw(mCanvas);
            mCompressHandler.removeCallbacks(mCompressTask);
            mCompressHandler.post(mCompressTask);
        }
    }

    private class CompressTask implements Runnable {
        ByteArrayPool mByteArrayPool = new ByteArrayPool(1024 * 30);
        PoolingByteArrayOutputStream mByteArrayOutputStream = new PoolingByteArrayOutputStream(mByteArrayPool);

        @Override
        public void run() {
            try {//动态改变缩放比例时,由于不在该线程,可能导致bitmap被回收
                mByteArrayOutputStream.reset();
                long s = System.currentTimeMillis();
                mDrawingBoard.compress(Bitmap.CompressFormat.JPEG, 60, mByteArrayOutputStream);
                byte[] jpgBytes = mByteArrayOutputStream.toByteArray();
                Log.d(TAG, "compress " + (System.currentTimeMillis() - s));
                for (ClientHandler clientHandler : mClientHandlers) {
                    clientHandler.sendData(jpgBytes);
                }
                mUIHandler.post(mDrawTask);
            } catch (Exception e) {
            }
//            mUIHandler.postDelayed(mDrawTask, delay);
        }
    }

}
