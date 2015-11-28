package com.xsx.samer.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xsx.samer.R;
import com.xsx.samer.model.News;


public class NewsAdapter extends CommonBaseAdapter<News>{

	public NewsAdapter(List<News> list, Context context) {
		super(list, context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=ViewHolder.getViewHolder(context, convertView, parent, R.layout.item_news, position);
		News news=list.get(position);
		ImageView iv_avator=(ImageView) viewHolder.getView(R.id.iv_avator);
		TextView tv_author=(TextView) viewHolder.getView(R.id.tv_author);
		TextView tv_time=(TextView) viewHolder.getView(R.id.tv_time);
		TextView tv_title=(TextView) viewHolder.getView(R.id.tv_title);
		
		iv_avator.setImageResource(R.drawable.school);
		tv_author.setText("嘉应学院");
		tv_time.setText(news.getDate());
		tv_title.setText(news.getTitle());
		return viewHolder.getConvertView();
	}
	
	

}
