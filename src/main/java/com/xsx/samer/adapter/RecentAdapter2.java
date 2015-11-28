package com.xsx.samer.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonBaseAdapter;
import com.xsx.samer.utils.FaceTextUtil;
import com.xsx.samer.utils.ImageLoadOptions;
import com.xsx.samer.utils.TimeUtil;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobChat;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

/**
 * 最近会话的adapter
 * @author XSX
 *
 */
public class RecentAdapter2 extends CommonBaseAdapter<BmobRecent> {


    public RecentAdapter2(List<BmobRecent> list, Context context) {
        super(list, context);
        // TODO Auto-generated constructor stub
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BmobRecent bmobRecent=list.get(position);
        //将R.layout.item_conversation布局文件给管理
        ViewHolder viewHolder = ViewHolder.getViewHolder(context, convertView,
                parent, R.layout.item_conversation, position);
        //头像
        ImageView iv_recent_avatar=(ImageView) viewHolder.getView(R.id.iv_recent_avatar);
        //名称
        TextView tv_recent_name=(TextView) viewHolder.getView(R.id.tv_recent_name);
        //最近的消息
        TextView tv_recent_msg=(TextView) viewHolder.getView(R.id.tv_recent_msg);
        //最近消息的时间
        TextView tv_recent_time=(TextView) viewHolder.getView(R.id.tv_recent_time);
        //多少条未读
        TextView tv_recent_unread=(TextView) viewHolder.getView(R.id.tv_recent_unread);
        //填充相应的数据
        String avatar=bmobRecent.getAvatar();
        if(avatar!=null&& !avatar.equals("")){
            ImageLoader.getInstance().displayImage(avatar, iv_recent_avatar, ImageLoadOptions.getOptions());
        }else{
            //当没有头像时显示默认图片
            iv_recent_avatar.setImageResource(R.mipmap.ic_launcher);
        }
        tv_recent_name.setText(bmobRecent.getNick());
        tv_recent_time.setText(TimeUtil.getChatTime(bmobRecent.getTime()));
        String text=bmobRecent.getMessage();
        int type=bmobRecent.getType();
        switch (type) {
            case BmobConfig.TYPE_IMAGE:
                tv_recent_msg.setText("图片");
                break;
            case BmobConfig.TYPE_LOCATION:
                tv_recent_msg.setText("位置信息");
                break;
            case BmobConfig.TYPE_VOICE:
                tv_recent_msg.setText("语音");
                break;
            case BmobConfig.TYPE_TEXT:
                SpannableString spannableString = FaceTextUtil.toSpannableString(context, text);
                tv_recent_msg.setText(spannableString);
                break;
        }
        int num = BmobDB.create(context).getUnreadCount(bmobRecent.getTargetid());
        if (num > 0) {
            tv_recent_unread.setVisibility(View.VISIBLE);
            tv_recent_unread.setText(num + "");
        } else {
            tv_recent_unread.setVisibility(View.GONE);
        }

        return viewHolder.getConvertView();
    }




}
