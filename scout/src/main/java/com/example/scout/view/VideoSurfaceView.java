package com.example.scout.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.opengl.GLSurfaceView.EGLContextFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.linkcard.media.LinkVideoCore;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class VideoSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private boolean isFirstChange = true;
    private boolean isAlie = true;

    private ThreadPoolExecutor threadExecutor;

    public VideoSurfaceView(Context paramContext) {
        super(paramContext);
        init();
    }

    public VideoSurfaceView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }


    public void init() {
        // 设置OpenGl ES的版本为2.0
        setEGLContextClientVersion(2);
        //设置EGLContext工厂，不设置就用默认的
//      setEGLContextFactory(new ContextFactory());
        //设置EglConfig，一般颜色深度等等，利用此方法设置。不设置就用默认的
//      setEGLConfigChooser(localConfigChooser);
        setRenderer(this);//调用此方法会开启一个新的线程，即GL线程
        setRenderMode(0);////渲染的时候要求调用requestRender，必须在setRenderer后调用

        this.threadExecutor = new ThreadPoolExecutor(5, 10, 30L, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadPoolExecutor.CallerRunsPolicy());
        this.threadExecutor.execute(this.runnable);
    }

    public void onDrawFrame(GL10 paramGL10) {
        LinkVideoCore.nativeDraw(1);
    }

    public void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2) {
        if (isFirstChange) {
            isFirstChange = false;
            LinkVideoCore.nativeChangeView(paramInt1, paramInt2);
        }
    }

    public void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig) {
        LinkVideoCore.nativeInitRenderer();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (isAlie) {
                try {
                    Thread.sleep(5L);
                    if (LinkVideoCore.nativeDecodeFrame() == 0) {
                        requestRender();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
