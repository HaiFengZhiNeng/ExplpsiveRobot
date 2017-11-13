package com.example.explosiverobot.view.weiget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.explosiverobot.R;

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
                        mHandler.sendEmptyMessageDelayed(0, delayYime);
                    }
                    break;
            }
        }
    };

    private int mCount;

    private boolean isDown;
    
    private long delayYime = 2000;

    public TouchTextView(Context context) {
        super(context);
        init();
    }

    public TouchTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setUnpress();
    }

    private void setUnpress() {
        GradientDrawable gd = new GradientDrawable();
//        int roundRadius = 15; // 8dp 圆角半径
//        gd.setCornerRadius(roundRadius);
        gd.setColor(getResources().getColor(R.color.steelblue));
        setBackgroundDrawable(gd);
    }

    private void setPress() {
        GradientDrawable gd = new GradientDrawable();
//        int roundRadius = 15; // 8dp 圆角半径
//        gd.setCornerRadius(roundRadius);
        gd.setColor(getResources().getColor(R.color.antiquewhite));
        setBackgroundDrawable(gd);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCount = 0;
                if(!isDown) {
                    isDown = true;
                    mHandler.sendEmptyMessageDelayed(0, delayYime);
                    if(onTimeListener != null){
                        onTimeListener.onTextDowm();
                    }
                }
                setPress();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                isDown = false;
                if(onTimeListener != null){
                    if(mCount != 0) {
                        mHandler.removeMessages(0);
                        onTimeListener.onTextDownFinish(this);
                    }
                }
                setUnpress();
                break;
        }

        return true;
    }


    public void setOnTimeListener(OnTextTimeListener onTimeListener) {
        this.onTimeListener = onTimeListener;
    }

    public interface OnTextTimeListener {
        void onTextDowm();

        void onTextTimecount(View view, int count);

        void onTextDownFinish(View view);
    }
}
