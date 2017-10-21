package com.example.explosiverobot.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.explosiverobot.R;
import com.example.explosiverobot.base.adapter.BaseRecyclerAdapter;
import com.example.explosiverobot.base.adapter.BaseRecyclerViewHolder;
import com.example.explosiverobot.base.adapter.SimpleAdapter;
import com.example.explosiverobot.modle.ActionItem;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by dell on 2017/10/21.
 */

public class ActionAdapter extends SimpleAdapter<ActionItem> {

    private Context mContext;

    public ActionAdapter(Context context, List<ActionItem> face_ids) {
        super(context, R.layout.layout_item_action, face_ids);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, ActionItem item, int pos) {
        viewHolder.getTextView(R.id.tv_actionName).setText(item.getItem_name());
        Picasso.with(mContext).load(new File(item.getItem_pic())).into(viewHolder.getImageView(R.id.iv_actionPic));
        //判断手臂夹状态
        if (item.getItem_isOpen().equals("1")) {
            viewHolder.getTextView(R.id.tv_headStatus).setText("手臂夹开");
        } else {
            viewHolder.getTextView(R.id.tv_headStatus).setText("手臂夹关");
        }

    }

}
