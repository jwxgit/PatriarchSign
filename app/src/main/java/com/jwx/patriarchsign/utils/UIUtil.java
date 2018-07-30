package com.jwx.patriarchsign.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.application.BaseApplication;

import java.util.List;

/**
 * 工具类
 *
 * @author 菜花
 */
public class UIUtil {


    private static float mDeceleration;


    /**
     * 获取上下文
     *
     * @return
     */
    public static Context getContext() {

        return new ContextThemeWrapper(BaseApplication.getContext(), R.style.AppTheme);
    }

    /**
     * 主线程中执行 任务
     *
     * @param task
     */
    public static void runOnUiThread(Runnable task) {

        long currentThreadId = android.os.Process.myTid();
        long mainThreadId = getMainThreadId();
        if (currentThreadId == mainThreadId) {
            // 如果在主线程中执行
            task.run();
        } else {
            // 需要转的主线程执行
            getMainThreadHandler().post(task);
        }
    }

    public static Resources getResources() {

        return getContext().getResources();
    }

    public static long getMainThreadId() {

        return BaseApplication.getMainThreadId();
    }

    public static Handler getMainThreadHandler() {

        return BaseApplication.getMainThreadHandler();
    }

    public static String getPackageName() {

        return getContext().getPackageName();
    }

    public static void sw() {
        Configuration config = getResources().getConfiguration();
        int smallestScreenWidth = config.smallestScreenWidthDp;
        LogUtils.i("smallest width : " + smallestScreenWidth);
    }

    /**
     * @param dip
     * @return
     */
    public static int dip2px(float dip) {

        // 公式 1: px = dp * (dpi / 160)
        // 公式 2: dp = px / denistity;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;
        // metrics.densityDpi
        return (int) (dip * density + 0.5f);
    }

    public static int px2dip(float px) {

        // 公式 1: px = dp * (dpi / 160)
        // 公式 2: dp = px / denistity;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;
        // metrics.densityDpi
        return (int) (px / density + 0.5f);
    }

    public static void startActivity(Intent intent) {

        getContext().startActivity(intent);
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {

        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取导航栏高度
     *
     * @return
     */
    public static int getNavigationBarHeight() {

        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 判断app是否在前台运行
     *
     * @param context
     * @return
     */
    public static boolean isAppOnForeGround(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(
                    packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        // 屏幕宽度
        return display.getWidth();
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {

        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        // 屏幕宽度
        return display.getHeight();
    }

    /**
     * 获取PPI
     *
     * @return
     */
    public static float getPpi() {
        return getContext().getResources().getDisplayMetrics().density * 160.0f;
    }

    /**
     * 根据摩擦力 获取listView的减速度
     */
    public static float computeDeceleration() {

        if (mDeceleration == 0) {
            mDeceleration = SensorManager.GRAVITY_EARTH   // g (m/s^2)
                    * 39.37f               // inch/meter
                    * getPpi()                 // pixels per inch
                    * ViewConfiguration.getScrollFriction();
        }
        return mDeceleration;
    }

    /**
     * 获取ListView的当前滑动速度
     *
     * @param startVelocity 开始速度
     * @param timePass      经过的时间
     * @return
     */
    public static float getCurrentVelocity(float startVelocity, long timePass) {
        if (startVelocity > 0) {
            return startVelocity - computeDeceleration() * timePass / 3000.0f;
        }
        return startVelocity + computeDeceleration() * timePass / 3000.0f;
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity
     * @param color
     */
    public static void setWindowStatusBarColor(Activity activity, int color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void setWindowStatusBarColor(Dialog dialog, int colorResId) {
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                Window window = dialog.getWindow();
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                window.setStatusBarColor(dialog.getContext().getResources().getColor(colorResId));
//
//                //底部导航栏
//                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 强制收起软键盘
     *
     * @param view
     */
    public static void forceHideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 强制打开软键盘
     *
     * @param view
     */
    public static void forceShowSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }

    public static void copyToClipboard(String text) {
        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(text);
        Toast.makeText(getContext(), "已复制到剪贴板！", Toast.LENGTH_SHORT).show();
    }

    /**
     * android:windowSoftInputMode="adjustResize|stateHidden"
     *
     * @param root         最外层布局，需要调整的布局
     * @param scrollToView 被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
     */
    private static int maxScrollHeight;

    public static void controlKeyboardLayout(final View root, final View scrollToView, final int offset, final RelayoutListener listener) {
        if (root == null)
            return;

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom - getNavigationBarHeight();
                //若不可视区域高度大于50dp，则键盘显示
                if (rootInvisibleHeight > UIUtil.dip2px(50)) {
                    int[] location = new int[2];
                    //获取scrollToView在窗体的坐标
                    scrollToView.getLocationInWindow(location);
                    //计算root滚动高度，使scrollToView在可见区域
                    int srollHeight = (location[1] + scrollToView.getHeight() + offset) - rect.bottom;
                    if (maxScrollHeight == 0) {
                        maxScrollHeight = srollHeight;
                    }
                    smoothScrollToY(root, srollHeight + root.getScrollY(), maxScrollHeight, listener);
//                    root.scrollTo(0, srollHeight);
                } else {
                    //键盘隐藏
                    smoothScrollToY(root, 0, maxScrollHeight, listener);
//                    root.scrollTo(0, 0);
                }
            }
        });
    }

    private static ValueAnimator scrollAnim;

    private static void smoothScrollToY(final View rootView, final int endY, final int maxScrollY, final RelayoutListener listener) {
        if (scrollAnim != null && scrollAnim.isRunning())
            scrollAnim.cancel();
        scrollAnim = ValueAnimator.ofInt(rootView.getScrollY(), endY);
        scrollAnim.setInterpolator(new DecelerateInterpolator());
        scrollAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rootView.scrollBy(0, (int) animation.getAnimatedValue() - rootView.getScrollY());
                if (listener != null && maxScrollY > 0)
                    listener.onScroll(rootView.getScrollY(), maxScrollY);
            }
        });
        scrollAnim.setDuration(200);
        scrollAnim.start();
    }

    public interface RelayoutListener {
        void onScroll(int scrollY, int maxScrollY);
    }

}
