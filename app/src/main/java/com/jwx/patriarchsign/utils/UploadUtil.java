package com.jwx.patriarchsign.utils;

import com.jwx.patriarchsign.app.activities.BaseActivity;
import com.jwx.patriarchsign.app.dialog.UploadingDialog;
import com.jwx.patriarchsign.data.cache.BitmapCache;
import com.jwx.patriarchsign.data.protocols.CommentProtocol;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class UploadUtil {

    private static UploadingDialog mDialog;

    public static void uploadParentInfo(BaseActivity activity, String childCode, String childName) {
        showUploadDialog(activity, childCode, childName);
        CommentProtocol.uploadParentInfo(BitmapCache.getParentFingerBmp(), BitmapCache.getParentSignBmp(), BitmapCache.getParentFaceBmp()
                , childCode, childName, new CommentProtocol.ResultCallback() {
                    @Override
                    public void onSuccess() {
                        BitmapCache.clearParentCache();
                        mDialog.onLoadSucceed();
                    }

                    @Override
                    public void onFailed() {
                        mDialog.onLoadFailed();
                    }
                });
    }

    private static void showUploadDialog(final BaseActivity activity, final String childCode, final String childName) {
        if (mDialog == null) {
            mDialog = new UploadingDialog();
            mDialog.setRetryClickListener(new UploadingDialog.RetryClickListener() {
                @Override
                public void onRetryClick() {
                    uploadParentInfo(activity, childCode, childName);
                }
            });
            mDialog.setCancelable(false);
        }
        if (mDialog.isAdded()) {
            mDialog.loadStart();
        } else {
            mDialog.show(activity.getSupportFragmentManager(), "upload");
        }
    }
}
