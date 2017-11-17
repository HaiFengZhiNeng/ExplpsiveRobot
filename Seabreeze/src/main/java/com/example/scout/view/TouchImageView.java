package com.example.scout.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by zhangyuanyuan on 2017/10/31.
 */

public class TouchImageView extends ImageView {

    private OnImageTimeListener onTimeListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mCount++;
                    if (isDown) {
                        if (onTimeListener != null) {
                            onTimeListener.onImageTimecount(TouchImageView.this, mCount);
                        }

                        mHandler.sendEmptyMessageDelayed(0, delayYime);
                    }
                    break;
            }
        }
    };

    private int mCount;

    private boolean isDown;
    
    private long delayYime = 500;

    public TouchImageView(Context context) {
        super(context);
    }

    public TouchImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCount = 0;
                if (!isDown) {
                    isDown = true;
                    mHandler.sendEmptyMessageDelayed(0, delayYime);
                    if(onTimeListener != null){
                        onTimeListener.onImageDown(this);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                isDown = false;
                if (onTimeListener != null) {
                    if(mCount != 0) {
                        mHandler.removeMessages(0);
                        onTimeListener.onImageDownFinish(this);
                    }else{
                        onTimeListener.onImageDownFinish(this);
                    }
                }
                break;
        }

        return true;
    }


    public void setOnTimeListener(OnImageTimeListener onTimeListener) {
        this.onTimeListener = onTimeListener;
    }

    public interface OnImageTimeListener {
        void onImageDown(View view);

        void onImageTimecount(View view, int count);

        void onImageDownFinish(View view);
    }
}
