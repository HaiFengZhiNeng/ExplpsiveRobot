package com.example.explosiverobot.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.explosiverobot.R;
import com.example.explosiverobot.base.adapter.BaseRecyclerAdapter;
import com.example.explosiverobot.base.adapter.BaseRecyclerViewHolder;
import com.example.explosiverobot.base.adapter.SimpleAdapter;
import com.example.explosiverobot.modle.ActionItem;
import com.example.explosiverobot.modle.ChatMessageBean;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dell on 2017/10/21.
 */

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<ActionItem> mActionItems = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public ActionAdapter(Context context, List<ActionItem> actionItems) {
        this.mContext = context;
        this.mActionItems = actionItems;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ActionViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ActionViewHolder extends RecyclerView.ViewHolder {

        ImageView mActionPic;
        TextView mActionName;
        TextView mHeadStatus;
        TextView mActionGroup;


        public ActionViewHolder(View itemView) {
            super(itemView);
        }
    }


    /**
     * 添加一项
     *
     * @param t
     */
    public void addItem(ChatMessageBean t) {
        cmbs.add(t);
        notifyDataSetChanged();

    }

    /**
     * 清除所有
     */
    public void clear() {
        if (cmbs == null || cmbs.size() <= 0)
            return;
        for (Iterator it = cmbs.iterator(); it.hasNext(); ) {

            ChatMessageBean t = (ChatMessageBean) it.next();
            int position = cmbs.indexOf(t);
            it.remove();
            notifyItemRemoved(position);
        }
    }

    /**
     * 移除某一项
     *
     * @param t
     */
    public void removeItem(ChatMessageBean t) {
        int position = cmbs.indexOf(t);
        cmbs.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 刷新
     *
     * @param list
     */
    public void refreshData(List<ChatMessageBean> list) {
        clear();
        if (list != null && list.size() > 0) {

            int size = list.size();
            for (int i = 0; i < size; i++) {
                cmbs.add(i, list.get(i));
                notifyItemInserted(i);
            }
        }
    }

    /**
     * 加载更多
     *
     * @param list
     */
    public void loadMoreData(List<ChatMessageBean> list) {
        if (list != null && list.size() > 0) {
            int size = list.size();
            int begin = cmbs.size();
            for (int i = 0; i < size; i++) {
                cmbs.add(list.get(i));
                notifyItemInserted(i + begin);
            }
        }
    }

    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, ActionItem item, int pos) {
        viewHolder.getTextView(R.id.tv_actionName).setText(item.getItem_name());
        viewHolder.getTextView(R.id.tv_actionGroup).setText(item.getItem_group());
        Picasso.with(mContext).load(new File(item.getItem_pic())).into(viewHolder.getImageView(R.id.iv_actionPic));
        //判断手臂夹状态
        if (item.getItem_isOpen().equals("1")) {
            viewHolder.getTextView(R.id.tv_headStatus).setText("手臂夹开");
        } else {
            viewHolder.getTextView(R.id.tv_headStatus).setText("手臂夹关");
        }

    }

}
