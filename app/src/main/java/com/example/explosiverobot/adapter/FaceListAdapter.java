package com.example.explosiverobot.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/10/16.
 */

public class FaceListAdapter extends SimpleAdapter<String> {

    public FaceListAdapter(Context context, List<String> face_ids) {
        super(context, android.R.layout.simple_list_item_1, face_ids);
    }


    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, String item, int pos) {
        viewHolder.getTextView(android.R.id.text1).setText(item);
    }
}
