package com.example.explosiverobot.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.explosiverobot.R;
import com.example.explosiverobot.activity.AddActionActivity;
import com.example.explosiverobot.activity.TaskActivity;
import com.example.explosiverobot.adapter.ActionAdapter;
import com.example.explosiverobot.base.fragment.BaseFragment;
import com.example.explosiverobot.db.manager.ActionItemDbManager;
import com.example.explosiverobot.modle.ActionItem;
import com.example.explosiverobot.util.JumpItent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zhangyuanyuan on 2017/10/19.
 */

public class ActionCommonFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int ADD_ACTION_REQUEST_TCODE = 200;
    private static final int ACTION_RESUL_TCODE = 2;

    @BindView(R.id.ry_actionAll)
    RecyclerView ryActionAll;
    @BindView(R.id.smartRefreshLayout)
    SwipeRefreshLayout smartRefreshLayout;
    Unbinder unbinder;
    @BindView(R.id.tv_noInfo)
    TextView tvNoInfo;
    Unbinder unbinder1;
    //Tab本地数据
    private ActionItemDbManager actionItemDbManager;

    private List<ActionItem> actionItems = new ArrayList<>();
    private ActionAdapter actionAdapter;

    private String theme_name = "";//主题ID

    private boolean isVisiable;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_action_common;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isVisiable)
            initData();
    }

    @Override
    protected void initView(View view) {
        theme_name = getArguments().getString("theme_name");
        actionItemDbManager = new ActionItemDbManager();
        ryActionAll.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        ryActionAll.setLayoutManager(new GridLayoutManager(getActivity(), 3));
    }

    @Override
    protected void initData() {
        if ("全部".equals(theme_name)) {
            actionAdapter = new ActionAdapter(getActivity(), actionItems, theme_name);
            ryActionAll.setAdapter(actionAdapter);
            actionItems = actionItemDbManager.loadAll();
        } else {
            actionAdapter = new ActionAdapter(getActivity(), actionItems, theme_name);
            ryActionAll.setAdapter(actionAdapter);
            actionItems = actionItemDbManager.queryByItemName(theme_name);
            actionItems.add(new ActionItem("", "", "", "", ActionAdapter.ADD));
        }

        if (actionItems != null && actionItems.size() > 0) {
            tvNoInfo.setVisibility(View.GONE);
            ryActionAll.setVisibility(View.VISIBLE);

            actionAdapter.refreshData(actionItems);

            actionAdapter.setmOnClickimageListener(new ActionAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    if (!theme_name.equals("全部")) {
                        if (position == actionItems.size() - 1) {
                            Bundle bundle = new Bundle();
                            bundle.putString("tabName", theme_name);
                            JumpItent.jump(ActionCommonFragment.this, getActivity(), AddActionActivity.class, bundle, ADD_ACTION_REQUEST_TCODE);
                        } else {
                            JumpItent.jump(getActivity(), TaskActivity.class);
                        }
                    } else {
                        JumpItent.jump(getActivity(), TaskActivity.class);
                    }
                }
            });

            actionAdapter.setmOnLongClickimageListener(new ActionAdapter.OnItemLongClickListener() {
                @Override
                public void onLongClick(int position) {
                    showDialog(position);
                }
            });
        } else {
//            tvNoInfo.setVisibility(View.VISIBLE);
            ryActionAll.setVisibility(View.GONE);
        }


    }

    @Override
    protected void setListener(View view) {
        smartRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.iv_addAction:
//
        }
    }

    private void showDialog(final int position) {

        new MaterialDialog.Builder(mContext)
                .content("是否删除")
                .positiveText("删除所有").onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                materialDialog.dismiss();
                if (actionItemDbManager.deleteAll()) {
                    actionAdapter.clear();
                }
            }
        })
                .negativeText("删除此条").onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                materialDialog.dismiss();
                if (actionItemDbManager.delete(actionItems.get(position))) {
                    actionAdapter.removeItem(actionItems.get(position));
                }
            }
        })
                .show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ACTION_REQUEST_TCODE && resultCode == ACTION_RESUL_TCODE) {
            Log.e("GG", "onactivity result " + theme_name);
//            actionItems = actionItemDbManager.queryByItemName(theme_name);
//            if (actionItems != null && actionItems.size() > 0) {
//                ryActionAll.setVisibility(View.VISIBLE);
//                actionAdapter.refreshData(actionItems);
//            }
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        // 开始刷新，设置当前为刷新状态
        smartRefreshLayout.setRefreshing(true);
        Log.e("GG", theme_name + "");
        if ("全部".equals(theme_name)) {
            actionItems = actionItemDbManager.loadAll();
        } else {
            actionItems = actionItemDbManager.queryByItemName(theme_name);
            actionItems.add(new ActionItem("", "", "", "", ActionAdapter.ADD));
        }
        if (actionItems != null && actionItems.size() > 0) {
            ryActionAll.setVisibility(View.VISIBLE);
            actionAdapter.refreshData(actionItems);
        }
        smartRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisiable = getUserVisibleHint();
        if (rootView != null && getUserVisibleHint()) {
//            initData();
            Log.e("GG", "setUserVisibleHint: 当你滑动到这个页面我才改变的");
        }

        Log.e("GG", "setUserVisibleHint: " + isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }
}

