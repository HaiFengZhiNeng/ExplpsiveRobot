package com.example.explosiverobot.view.weiget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.explosiverobot.R;

/**
 * Created by dell on 2017/11/13.
 */

public class MainPopWindow extends PopupWindow {
    private Activity context;




    public MainPopWindow(Activity context, View.OnClickListener itemsOnClick) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_popwindow, null);

        TextView tv_shape = (TextView) view.findViewById(R.id.tv_shape);
        TextView tv_work = (TextView) view.findViewById(R.id.tv_work);

        tv_shape.setOnClickListener(itemsOnClick);
        tv_work.setOnClickListener(itemsOnClick);
        setWidth(200);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(0));
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        update();
    }

}
