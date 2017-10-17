package com.example.explosiverobot.actionOrder;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.explosiverobot.R;
import com.example.explosiverobot.base.BaseFragment;
import com.ocean.mvp.library.presenter.BasePresenter;

import java.util.ArrayList;

import butterknife.Bind;

public class ActionCommonFragment extends BaseFragment<ActionCommonPresenter> implements IActionCommonView {

    @Bind(R.id.rv_action)
    RecyclerView mRvAction;

    //    private List<LiveCommon> commonList = new ArrayList<>();
//    private LiveCommonAdapter commonAdapter;
    private String theme_id = "";

    @Override
    public int getContentViewResource() {
        return R.layout.layout_action_common;
    }

    @Override
    public ActionCommonPresenter createPresenter() {
        return new ActionCommonPresenter(this);
    }


}
