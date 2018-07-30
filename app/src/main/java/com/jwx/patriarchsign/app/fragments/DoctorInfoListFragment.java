package com.jwx.patriarchsign.app.fragments;

import android.view.View;
import android.widget.LinearLayout;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.activities.DoctorInfoActivity;
import com.jwx.patriarchsign.app.holders.DoctorInfoItemHolder;
import com.jwx.patriarchsign.data.domain.DoctorItemInfo;
import com.jwx.patriarchsign.data.protocols.BaseProtocol;
import com.jwx.patriarchsign.data.protocols.DoctorItemsProtocol;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class DoctorInfoListFragment extends BaseFragment<List<DoctorItemInfo>> implements DoctorInfoItemHolder.ClearCallback {
    @ViewInject(R.id.info_container)
    private LinearLayout mInfoContainer;

    private List<DoctorInfoItemHolder> itemHolders = new ArrayList<>();

    public List<DoctorInfoItemHolder> getItemHolders() {
        return itemHolders;
    }

    @Override
    public View onSuccessView(List<DoctorItemInfo> data) {
        View view = View.inflate(getContext(), R.layout.fragment_doctor_info_list, null);
        x.view().inject(this, view);
        itemHolders.clear();
        mInfoContainer.removeAllViews();
        for (DoctorItemInfo doctorItemInfo : data) {
            DoctorInfoItemHolder holder = new DoctorInfoItemHolder(getActivity());
            holder.setData(doctorItemInfo);
            holder.setClearCallback(this);
            itemHolders.add(holder);
            mInfoContainer.addView(holder.getRootView());
        }
        refershNextButtonState();
        return view;
    }

    @Override
    protected void onRefresh(boolean isSucceed, List<DoctorItemInfo> data) {
        if (data == null) return;
        for (DoctorItemInfo doctorItemInfo : data) {
            boolean holderExist = false;
            for (DoctorInfoItemHolder itemHolder : itemHolders) {
                if (itemHolder.getData().getUserId().equals(doctorItemInfo.getUserId())) {
                    itemHolder.setData(doctorItemInfo);
                    holderExist = true;
                    break;
                }
            }
            if (!holderExist) {
                DoctorInfoItemHolder holder = new DoctorInfoItemHolder(getActivity());
                holder.setData(doctorItemInfo);
                holder.setClearCallback(this);
                itemHolders.add(holder);
                mInfoContainer.addView(holder.getRootView());
            }
        }
        refershNextButtonState();
    }

    @Override
    public void onLoadData(int pagerIndex, final LoadingPager.LoadResultCallback<List<DoctorItemInfo>> resultCallback) {
        DoctorItemsProtocol protocol = new DoctorItemsProtocol();
        protocol.getDataFromNet(new BaseProtocol.LoadCallback<List<DoctorItemInfo>>() {
            @Override
            public void onSuccess(List<DoctorItemInfo> data) {
                resultCallback.onCompleted(data);
            }

            @Override
            public void onFailed(Throwable e, String errorCode) {
                resultCallback.onError(e);
            }
        });
    }

    /**
     * 刷新跳过按钮的可点击状态
     */
    private void refershNextButtonState() {
        boolean isNext = false;
        for (DoctorInfoItemHolder itemHolder : itemHolders) {
            if (itemHolder.getData().getState() == 0) {
                isNext = true;
                break;
            }
        }
        if (isNext) {
            ((DoctorInfoActivity) getActivity()).setCommitBtEnable(true);
        } else {
            ((DoctorInfoActivity) getActivity()).setCommitBtEnable(false);
        }
    }

    @Override
    public void onClearSuccess() {
        refershNextButtonState();
    }
}
