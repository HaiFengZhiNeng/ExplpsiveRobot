package com.example.explosiverobot.activity;

import android.opengl.GLSurfaceView;

import com.example.explosiverobot.R;
import com.example.explosiverobot.base.activity.BaseActivity;
import com.example.explosiverobot.ipcamera.MyRender;

import butterknife.BindView;

public class TaskActivity extends BaseActivity {


    @BindView(R.id.gl_surface_view)
    GLSurfaceView glSurfaceView;

    private MyRender myRender;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_task;
    }

    @Override
    protected void init() {

        myRender = new MyRender(glSurfaceView);
        glSurfaceView.setRenderer(myRender);


    }

}
