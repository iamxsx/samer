package com.xsx.samer.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * RecyclerView的通用ViewHolder
 * Created by XSX on 2015/10/11.
 */
public class MyViewHolder extends RecyclerView.ViewHolder{
    //存放ViewHolder中各种控件的集合
    private SparseArray<View> views;
    private View itemView;
    public MyViewHolder(View itemView) {
        super(itemView);
        views=new SparseArray<View>();
        this.itemView=itemView;
    }

    //给个id，ViewHolder将所对应id的控件返回
    public View getView(int id){
        //去容器里找
        View view=views.get(id);
        if(view==null){
            view=itemView.findViewById(id);
            views.put(id, view);
        }
        return itemView.findViewById(id);
    }


}
