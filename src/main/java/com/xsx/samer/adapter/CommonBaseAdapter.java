package com.xsx.samer.adapter;

import java.util.List;


import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 通用的Adpater
 * @author XSX
 *
 * @param <T>
 */
public abstract class CommonBaseAdapter<T> extends BaseAdapter{
    //数据
    protected List<T> list;
    protected Context context;
    protected LayoutInflater inflater;
    protected int layoutId;

    public CommonBaseAdapter(List<T> list, Context context) {
        super();
        this.list = list;
        this.context = context;
    }

    public CommonBaseAdapter(List<T> list, Context context,
                             LayoutInflater inflater,int layoutId) {
        super();
        this.list = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.layoutId=layoutId;
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


    public void addAll(List<T> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        this.list.remove(position);
        notifyDataSetChanged();
    }

    public void removeAll(){
        this.list.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }


    public static class ViewHolder{
        //用来存放布局控件的容器
        private SparseArray<View> mViews;
        private View convertView;
        //当convertView是空时才需要new一个ViewHolder，因此这里不必将convertewView传进来
        public ViewHolder(Context context,ViewGroup parent,int layoutId,int position){
            mViews=new SparseArray<View>();
            this.convertView=LayoutInflater.from(context).inflate(layoutId, parent,false);
            this.convertView.setTag(this);

        }

        //返回一个ViewHolder
        public static ViewHolder getViewHolder(Context context,View convertView,
                                               ViewGroup parent,int layoutId,int position){
            if(convertView==null){
                return new ViewHolder(context, parent, layoutId, position);
            }else{
                return (ViewHolder) convertView.getTag();
            }
        }

        //返回一个convertView
        public View getConvertView(){
            return convertView;
        }

        //给个id，ViewHolder将所对应id的控件返回
        public View getView(int id){
            //去容器里找
            View view=mViews.get(id);
            if(view==null){
                view=convertView.findViewById(id);
                mViews.put(id, view);
            }
            return convertView.findViewById(id);
        }
    }


}
