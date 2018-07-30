package com.jwx.patriarchsign.app.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.data.domain.InoculationTableInfo;
import com.jwx.patriarchsign.data.protocols.BaseProtocol;
import com.jwx.patriarchsign.data.protocols.InoculationTablesProtocol;
import com.jwx.patriarchsign.utils.UIUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/11/8 0008.
 */

public class PickTableDialog extends DialogFragment {
    private TablePickedCallback        callback;
    private GridView                   mTableGrid;
    private ProgressBar                mProgressBar;
    private View                       mRetryBt;
    private List<InoculationTableInfo> mTableList;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
//        lp.windowAnimations = R.style.BottomToTopAnim;
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout((int) (UIUtil.getScreenWidth() * 0.6f),
                getDialog().getWindow().getAttributes().height);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout mRootView = (RelativeLayout) inflater.inflate(R.layout.pick_table_dialog_layout, container);
        mTableGrid = (GridView) mRootView.findViewById(R.id.table_grid);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBar);
        View mCancelBt = mRootView.findViewById(R.id.cancel_bt);
        mRetryBt = mRootView.findViewById(R.id.retry_bt);
        mCancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mRetryBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTables();
            }
        });
        loadTables();
        return mRootView;
    }

    private void loadTables() {
        mProgressBar.setVisibility(View.VISIBLE);
        new InoculationTablesProtocol().getDataFromNet(new BaseProtocol.LoadCallback<List<InoculationTableInfo>>() {
            @Override
            public void onSuccess(final List<InoculationTableInfo> data) {
                mProgressBar.setVisibility(View.GONE);
                mTableList = data;
                refreshGrid();
            }

            @Override
            public void onFailed(Throwable e, String errorCode) {
                mProgressBar.setVisibility(View.GONE);
                mRetryBt.setVisibility(View.VISIBLE);
            }
        });
    }

    private void refreshGrid() {
        mTableGrid.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mTableList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = View.inflate(getActivity(), R.layout.table_item_layout, null);
                TextView name = (TextView) itemView.findViewById(R.id.table_name);
                name.setText(mTableList.get(position).getName() + "(" + mTableList.get(position).getWorkbenchId() + ")");
                return itemView;
            }
        });
        mTableGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (callback != null) {
                    callback.onPicked(mTableList.get(position));
                }
                dismiss();
            }
        });
    }

    public void setTablePickedListener(TablePickedCallback callback) {
        this.callback = callback;
    }

    public interface TablePickedCallback {
        void onPicked(InoculationTableInfo tableInfo);
    }
}
