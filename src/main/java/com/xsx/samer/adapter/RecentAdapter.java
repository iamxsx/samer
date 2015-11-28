package com.xsx.samer.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.utils.FaceTextUtil;
import com.xsx.samer.utils.ImageLoadOptions;
import com.xsx.samer.utils.TimeUtil;

import java.util.List;

import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

/**
 * Created by XSX on 2015/10/11.
 */
public class RecentAdapter extends CommonReclclerViewAdapter<BmobRecent> implements View.OnClickListener {
    private static final String TAG = "RecentAdapter";

    public RecentAdapter(Context context, List<BmobRecent> list) {
        super(context, list);
    }
    private View view;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view=LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {

        BmobRecent bmobRecent=list.get(position);
        //头像
        ImageView iv_recent_avatar=(ImageView) viewHolder.getView(R.id.iv_recent_avatar);
        iv_recent_avatar.setOnClickListener(this);
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
        tv_recent_name.setText(bmobRecent.getUserName());
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
        if (mOnItemClickLitener != null)
        {
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.i(TAG,"onClick");
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(view, pos);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(view, pos);
                    return false;
                }
            });
        }

    }

    @Override
    public void onClick(View view) {

    }
}
