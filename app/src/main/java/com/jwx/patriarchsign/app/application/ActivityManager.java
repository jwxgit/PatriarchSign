package com.jwx.patriarchsign.app.application;

import android.app.Activity;
import android.content.Intent;

import com.jwx.patriarchsign.app.activities.BaseActivity;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.utils.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/8 0008.
 */

public class ActivityManager {

    private static List<BaseActivity> activityStack = new ArrayList<>();

    public static void addActivity(BaseActivity activity) {
        activityStack.add(0, activity);
    }

    public static void removeActivity(BaseActivity activity) {
        activityStack.remove(activity);
    }

    public static void finishAll() {
        for (BaseActivity activity : activityStack) {
            activity.finish();
        }
    }

    public static void finishToTarget(Activity activityFrom, Class<? extends BaseActivity> activityClass) {
        finishToTarget(activityFrom, activityClass, false);
    }

    /**
     * @param activityFrom  发起关闭的Activity
     * @param activityClass 目标Activity 关闭到这个Activity为止  如果Activity栈没有 则打开新的Activity
     * @param closeable     新打开的Activity能否被关闭
     */
    public static void finishToTarget(Activity activityFrom, Class<? extends BaseActivity> activityClass, boolean closeable) {
        boolean found = false;
        for (BaseActivity activity : activityStack) {
            if (activity.getClass() != activityClass)
                activity.finish();
            else {
                found = true;
                break;
            }
        }
        if (!found) {
            Intent intent = new Intent(UIUtil.getContext(), activityClass);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constances.INTENT_KEY_CLOSEABLE, closeable);
            activityFrom.startActivity(intent);
            activityFrom.finish();
        }
    }

    /**
     * 删除指定类名的Activity之前的activity
     */
    public static boolean finishBeforeActivity(Class<?> cls) {
        for (int i = 0; i <activityStack.size(); i++) {
            if (!activityStack.get(i).getClass().equals(cls)) {
                activityStack.get(i).finish();
            } else {
                break;
            }
        }
        return true;
    }
}
