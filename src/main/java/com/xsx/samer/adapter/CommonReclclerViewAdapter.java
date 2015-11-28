package com.xsx.samer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by XSX on 2015/10/11.
 */
public abstract class CommonReclclerViewAdapter<T> extends RecyclerView.Adapter<MyViewHolder>{
    protected Context context;
    protected List<T> list;

    public CommonReclclerViewAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public void add(T t){
        list.add(t);
        //提醒listview内容已改动
        notifyDataSetChanged();
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<T> getList(){
        return list;
    }


    public void addAll(List<T> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        this.list.remove(position);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.list.clear();
        notifyDataSetChanged();
    }

    /**
     * 为recylerview设置item点击事件
     */
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    protected OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
