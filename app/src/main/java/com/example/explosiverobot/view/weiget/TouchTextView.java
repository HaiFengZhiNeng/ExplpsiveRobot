package com.example.explosiverobot.view.weiget;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.explosiverobot.modle.Spot;

/**
 * Created by zhangyuanyuan on 2017/10/31.
 */

public class TouchTextView extends TextView {

    private OnTextTimeListener onTimeListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mCount++;
                    if (onTimeListener != null) {
                        onTimeListener.onTextTimecount(TouchTextView.this, mCount);
                    }
                    if(isDown) {
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    }
                    break;
            }
        }
    };

    private int mCount;

    private boolean isDown;

    public TouchTextView(Context context) {
        super(context);
    }

    public TouchTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCount = 0;
                if(!isDown) {
                    isDown = true;
                    mHandler.sendEmptyMessageDelayed(0, 500);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                isDown = false;
                if(onTimeListener != null){
                    if(mCount != 0) {
                        onTimeListener.onTextDownFinish(this);
                    }
                }
                break;
        }

        return true;
    }


    public void setOnTimeListener(OnTextTimeListener onTimeListener) {
        this.onTimeListener = onTimeListener;
    }

    public interface OnTextTimeListener {
        void onTextTimecount(View view, int count);
        void onTextDownFinish(View view);
    }
}
