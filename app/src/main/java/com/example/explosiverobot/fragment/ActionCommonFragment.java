package com.example.explosiverobot.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.explosiverobot.R;
import com.example.explosiverobot.activity.AddActionActivity;
import com.example.explosiverobot.adapter.ActionAdapter;
import com.example.explosiverobot.base.adapter.BaseRecyclerAdapter;
import com.example.explosiverobot.base.fragment.BaseFragment;
import com.example.explosiverobot.db.manager.ActionItemDbManager;
import com.example.explosiverobot.modle.ActionItem;
import com.example.explosiverobot.util.JumpItent;
import com.example.explosiverobot.view.manager.FullyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by zhangyuanyuan on 2017/10/19.
 */

public class ActionCommonFragment extends BaseFragment implements View.OnClickListener {

    private static final int ADD_ACTION_REQUEST_TCODE = 200;
    private static final int ACTION_RESUL_TCODE = 2;

    @BindView(R.id.ry_actionAll)
    RecyclerView ryActionAll;
    @BindView(R.id.iv_addAction)
    ImageView ivAddAction;
    //Tab本地数据
    private ActionItemDbManager actionItemDbManager;

    private List<ActionItem> actionItems = new ArrayList<>();
    private ActionAdapter actionAdapter;


    private String theme_name = "";//主题ID

    @Override
    protected int getLayoutId() {
        return R.layout.layout_action_common;
    }

    @Override
    protected void initView(View view) {

        theme_name = getArguments().getString("theme_name");
        actionItemDbManager = new ActionItemDbManager();
        ryActionAll.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    protected void initData() {
        if ("全部".equals(theme_name)) {
            actionItems = actionItemDbManager.loadAll();
        } else {
            actionItems = actionItemDbManager.queryByItemName(theme_name);
        }
        if (actionItems != null || actionItems.size() > 0) {
            ryActionAll.setVisibility(View.VISIBLE);
            actionAdapter = new ActionAdapter(getActivity(), actionItems);
            ryActionAll.setAdapter(actionAdapter);
            //热点要闻
            ryActionAll.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        } else {
            ryActionAll.setVisibility(View.GONE);
        }

        actionAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        actionAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                showDialog(position);
                return false;
            }
        });
    }

    @Override
    protected void setListener(View view) {
        ivAddAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_addAction:
                JumpItent.jump(ActionCommonFragment.this, getActivity(), AddActionActivity.class, ADD_ACTION_REQUEST_TCODE);
                break;
        }
    }

    private void showDialog(final int position) {

        final SweetAlertDialog pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText("是否删除");
        pDialog.setCancelText("删除所有").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismiss();
                if (actionItemDbManager.deleteAll()) {
                    actionAdapter.clear();
                }
            }
        });
        pDialog.setConfirmText("删除此条").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismiss();
                if (actionItemDbManager.delete(actionItems.get(position))) {
                    actionAdapter.removeItem(actionItems.get(position));
                }
            }
        });
        pDialog.show();


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ACTION_REQUEST_TCODE && resultCode == ACTION_RESUL_TCODE) {
            actionItems = actionItemDbManager.loadAll();
            if (actionItems != null && actionItems.size() > 0) {
                ryActionAll.setVisibility(View.VISIBLE);
                actionAdapter.refreshData(actionItems);
            }
        }
    }
}
