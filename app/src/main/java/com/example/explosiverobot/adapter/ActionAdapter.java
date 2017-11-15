package com.example.explosiverobot.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dell on 2017/10/21.
 */

public class ActionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private List<ActionItem> mActionItems = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private String mTheme;

    public static final int ADD = 0;//添加按钮
    public static final int OTHER = 1;//发送文本消息类型

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public ActionAdapter(Context context, List<ActionItem> actionItems, String theme) {
        this.mContext = context;
        this.mActionItems = actionItems;
        this.mTheme = theme;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        if (mTheme.equals("全部")) {
            view = mLayoutInflater.inflate(R.layout.layout_item_action, parent, false);
            holder = new ActionItemViewHolder(view);
        } else {
            if (viewType == OTHER) {
                view = mLayoutInflater.inflate(R.layout.layout_item_action, parent, false);
                holder = new ActionItemViewHolder(view);
            } else if (viewType == ADD) {
                view = mLayoutInflater.inflate(R.layout.layout_item_add, parent, false);
                holder = new ActionAddViewHolder(view);
            }

        }
//        view.setTag(viewType);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ActionItem actionItem = mActionItems.get(position);
        int itemViewType = getItemViewType(position);
        if (mTheme.equals("全部")) {
            fromItemLayout((ActionItemViewHolder) holder, actionItem, position);
        } else {
            if (itemViewType == OTHER) {
                fromItemLayout((ActionItemViewHolder) holder, actionItem, position);
            } else if (itemViewType == ADD) {
                fromAddItemLayout((ActionAddViewHolder) holder, actionItem, position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mActionItems != null ? mActionItems.size() : 0;
    }

    private void fromAddItemLayout(ActionAddViewHolder holder, ActionItem actionItem, int position) {
        holder.itemView.setTag(position);
    }

    private void fromItemLayout(ActionItemViewHolder holder, ActionItem actionItem, int position) {
        holder.mActionGroup.setText(actionItem.getItem_group());
        holder.mActionName.setText(actionItem.getItem_name());
        //判断手臂夹状态`
        if (actionItem.getItem_isOpen().equals("1")) {
            holder.mHeadStatus.setText("手臂夹开");
        } else {
            holder.mHeadStatus.setText("手臂夹关");
        }
        if (actionItem.getItem_pic() != null) {
            Glide.with(mContext).load(actionItem.getItem_pic()).into(holder.mActionPic);
        }
        holder.itemView.setTag(position);
    }

    public void setmOnClickimageListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setmOnLongClickimageListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mActionItems.get(position).getType();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onClick((int) view.getTag());
        }

    }

    @Override
    public boolean onLongClick(View view) {
        if (mOnItemLongClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemLongClickListener.onLongClick((int) view.getTag());
        }

        return false;
    }


    class ActionItemViewHolder extends RecyclerView.ViewHolder {

        ImageView mActionPic;
        TextView mActionName;
        TextView mHeadStatus;
        TextView mActionGroup;

        public ActionItemViewHolder(View itemView) {
            super(itemView);
            mActionPic = (ImageView) itemView.findViewById(R.id.iv_actionPic);

            mActionName = (TextView) itemView.findViewById(R.id.tv_actionName);

            mHeadStatus = (TextView) itemView.findViewById(R.id.tv_headStatus);

            mActionGroup = (TextView) itemView.findViewById(R.id.tv_actionGroup);
        }
    }

    class ActionAddViewHolder extends RecyclerView.ViewHolder {

        public ActionAddViewHolder(View itemView) {
            super(itemView);
        }
    }


    /**
     * 添加一项
     *
     * @param t
     */
    public void addItem(ActionItem t) {
        mActionItems.add(t);
        notifyDataSetChanged();

    }

    /**
     * 清除所有
     */
    public void clear() {
        if (mActionItems == null || mActionItems.size() <= 0)
            return;
        for (Iterator it = mActionItems.iterator(); it.hasNext(); ) {

            ActionItem t = (ActionItem) it.next();
            int position = mActionItems.indexOf(t);
            it.remove();
            notifyItemRemoved(position);
        }
    }

    /**
     * 移除某一项
     *
     * @param t
     */
    public void removeItem(ActionItem t) {
        int position = mActionItems.indexOf(t);
        mActionItems.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 刷新
     *
     * @param list
     */
    public void refreshData(List<ActionItem> list) {
        clear();
        if (list != null && list.size() > 0) {

            int size = list.size();
            for (int i = 0; i < size; i++) {
                mActionItems.add(i, list.get(i));
                notifyItemInserted(i);
            }
        }
    }

    /**
     * 加载更多
     *
     * @param list
     */
    public void loadMoreData(List<ActionItem> list) {
        if (list != null && list.size() > 0) {
            int size = list.size();
            int begin = mActionItems.size();
            for (int i = 0; i < size; i++) {
                mActionItems.add(list.get(i));
                notifyItemInserted(i + begin);
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public interface OnItemLongClickListener {
        void onLongClick(int position);
    }

}
