package com.xsx.samer.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xsx.samer.R;
import com.xsx.samer.model.FaceText;

public class EmoAdapter extends BaseArrayListAdapter{

    public EmoAdapter(Context context, List<FaceText> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonBaseAdapter.ViewHolder viewHolder=CommonBaseAdapter.ViewHolder.getViewHolder(context, convertView, parent, R.layout.item_face_text, position);
        ImageView iv_face_text=(ImageView) viewHolder.getView(R.id.iv_face_text);
        FaceText faceText = (FaceText) getItem(position);
        String key = faceText.getText().substring(1);
        Drawable drawable =context.getResources().getDrawable(context.getResources().getIdentifier(key, "drawable", context.getPackageName()));
        iv_face_text.setImageDrawable(drawable);
        return viewHolder.getConvertView();
    }






}
