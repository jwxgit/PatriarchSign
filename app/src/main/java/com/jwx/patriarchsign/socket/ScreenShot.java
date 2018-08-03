package com.jwx.patriarchsign.socket;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jwx.patriarchsign.MainActivity;
import com.jwx.patriarchsign.utils.BitmapUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TCP Socket客户端
 *
 * @author wurenqing
 * @since 2018-08-03
 */
public class ScreenShot extends AbstractSocketClient {
    private Timer timer;
    private Context context;
    private Canvas mCanvas = new Canvas();
    private View view;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    private DrawTask mDrawTask = new DrawTask();

    public ScreenShot(Context context) {
        this.context = context;
    }

    @Override
    public void onConnect(SocketTransceiver transceiver) {
        System.out.println("已连接.....");
        doSend(transceiver);
    }


    public synchronized void doSend(SocketTransceiver transceiver) {
        final SocketTransceiver receiver = transceiver;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("开始发送数据.....");
                try {
                    Bitmap screen = startRecorder(context, 0.5f);
                    if (null == screen) return;
                    byte[] datas = BitmapUtils.Bitmap2Bytes(screen);
                    Message message = new Message();
                    message.setType(1);
                    message.setData(Base64.encodeToString(datas, Base64.DEFAULT));
                    System.out.println(JSON.toJSONString(message));
                    receiver.send(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2000);
    }


    @Override
    public void onConnectFailed() {
        System.out.print("连接失败......");
    }

    @Override
    public void onReceive(SocketTransceiver transceiver, Message message) {
        System.out.print("接收到服务端发送来的数据......");
    }

    @Override
    public void onDisconnect(SocketTransceiver transceiver) {
        System.out.print("断开连接......");
        if (null == timer) return;
        timer.cancel();
        timer = null;
    }


    private static Point getScreenSize(Context context) {
        int w = context.getResources().getDisplayMetrics().widthPixels;
        int h = context.getResources().getDisplayMetrics().heightPixels;
        return new Point(w, h);
    }


    public Bitmap startRecorder(final Context context, float scale) {
        // 绘图
        Point point = getScreenSize(context);
        int exceptW = (int) (point.x * scale);
        int exceptH = (int) (point.y * scale);
        Bitmap mDrawingBoard = null;
        if (mDrawingBoard == null) {
            mDrawingBoard = Bitmap.createBitmap(exceptW, exceptH, Bitmap.Config.RGB_565);
        }
        if (mDrawingBoard.getWidth() != exceptW || mDrawingBoard.getHeight() != exceptH) {
            mDrawingBoard.recycle();
            mDrawingBoard = Bitmap.createBitmap(exceptW, exceptH, Bitmap.Config.RGB_565);
        }
        mCanvas.setBitmap(mDrawingBoard);
        mCanvas.scale(scale, scale);
        // 显示当前页
        if (context instanceof Activity) {
            view = ((Activity) context).getWindow().getDecorView();
        } else {
            Toast.makeText(context, "请下拉一下通知栏试试", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksAdapter() {
                @Override
                public void onActivityResumed(Activity activity) {
                    view = activity.getWindow().getDecorView();
                    mUIHandler.post(mDrawTask);
                }
            });
        }
        return mDrawingBoard;
    }


    private class DrawTask implements Runnable {
        @Override
        public void run() {
            mUIHandler.removeCallbacks(mDrawTask);
            view.draw(mCanvas);

        }
    }

}
