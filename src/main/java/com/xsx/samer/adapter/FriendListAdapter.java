package com.xsx.samer.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.utils.ImageLoadOptions;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;

/**
 * 好友列表适配器
 * @author XSX
 *
 */
public class FriendListAdapter extends CommonBaseAdapter<BmobChatUser>{

    public FriendListAdapter(List<BmobChatUser> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BmobChatUser chatUser=list.get(position);
        ViewHolder viewHolder = ViewHolder.getViewHolder(context, convertView,
                parent, R.layout.item_user_friend, position);
        ImageView img_friend_avatar=(ImageView) viewHolder.getView(R.id.img_friend_avatar);
        TextView tv_friend_name=(TextView) viewHolder.getView(R.id.tv_friend_name);
        String avatar=chatUser.getAvatar();
        if(avatar!=null&& !avatar.equals("")){
            ImageLoader.getInstance().displayImage(avatar, img_friend_avatar, ImageLoadOptions.getOptions());
        }else{
            img_friend_avatar.setImageResource(R.mipmap.male_default_icon);
        }
        tv_friend_name.setText(chatUser.getNick());
        return viewHolder.getConvertView();
    }


}
