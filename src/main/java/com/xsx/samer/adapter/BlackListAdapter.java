package com.xsx.samer.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.utils.ImageLoadOptions;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;

public class BlackListAdapter extends CommonBaseAdapter<BmobChatUser>{

    public BlackListAdapter(List<BmobChatUser> list, Context context) {
        super(list, context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(context, convertView,
                parent, R.layout.item_blacklist, position);
        final BmobChatUser contract = list.get(position);
        TextView tv_friend_name = (TextView) viewHolder.getView(R.id.tv_friend_name);
        ImageView iv_avatar = (ImageView) viewHolder.getView(R.id.img_friend_avatar);
        String avatar = contract.getAvatar();
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.mipmap.ic_launcher);
        }
        tv_friend_name.setText(contract.getUsername());
        return super.getView(position, convertView, parent);
    }

}
