package com.example.scout.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.scout.R;
import com.example.scout.common.BaseActivity;
import com.example.scout.view.CircleViewByImage;
import com.seabreeze.log.Print;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @BindView(R.id.show_view)
    ImageView showView;
    @BindView(R.id.move_view)
    CircleViewByImage moveView;
    @BindView(R.id.ll_front_lighting)
    LinearLayout llFrontLighting;
    @BindView(R.id.ll_after_lighting)
    LinearLayout llAfterLighting;
    @BindView(R.id.ll_front_image)
    LinearLayout llFrontImage;
    @BindView(R.id.ll_after_image)
    LinearLayout llAfterImage;
    @BindView(R.id.ll_lift_image)
    LinearLayout llLiftImage;
    @BindView(R.id.ll_right_image)
    LinearLayout llRightImage;
    @BindView(R.id.iv_up)
    ImageView ivUp;
    @BindView(R.id.iv_down)
    ImageView ivDown;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initDb() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        moveView.setCallback(new CircleViewByImage.ActionCallback() {
            @Override
            public void forwardMove() {//上
                Print.e("上");
            }

            @Override
            public void backMove() {//下
                Print.e("下");
            }

            @Override
            public void leftMove() {//左
                Print.e("左");
            }

            @Override
            public void rightMove() {
                Print.e("右");//右
            }

            @Override
            public void actionUp() {//**
                Print.e("离开");
            }
        });
    }

    @OnClick({R.id.ll_front_lighting, R.id.ll_after_lighting, R.id.ll_front_image, R.id.ll_after_image, R.id.ll_right_image,
            R.id.ll_lift_image, R.id.iv_up, R.id.iv_down})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_front_lighting:

                break;
            case R.id.ll_after_lighting:

                break;
            case R.id.ll_front_image:

                break;
            case R.id.ll_after_image:

                break;
            case R.id.ll_lift_image:

                break;
            case R.id.ll_right_image:

                break;
            case R.id.iv_up:

                break;
            case R.id.iv_down:

                break;
        }


    }

}
