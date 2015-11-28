package com.xsx.samer.adapter;

import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.model.Reply;
import com.xsx.samer.utils.FaceTextUtil;
import com.xsx.samer.utils.ImageLoadOptions;
import com.xsx.samer.utils.TimeUtil;
import com.xsx.samer.widget.CircleImageView;

public class ReplyAdapter extends CommonReclclerViewAdapter<Reply>{

    private static final String TAG = "ReplyAdapter";
    private View view;
    public ReplyAdapter(List<Reply> list, Context context) {
        super(context,list);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view= LayoutInflater.from(context).inflate(R.layout.item_reply,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        Reply reply=list.get(position);
        CircleImageView iv_avator=(CircleImageView)viewHolder.getView(R.id.iv_avator);

        String avatar=reply.getAvator();
        if(avatar!=null&& !avatar.equals("")){
            ImageLoader.getInstance().displayImage(avatar, iv_avator, ImageLoadOptions.getOptions());
        }else{
            if(reply.getAuthor().getSex().equals("男")){
                iv_avator.setImageResource(R.mipmap.male_default_icon);
            }else{
                iv_avator.setImageResource(R.mipmap.female_default_icon);

            }
        }

        TextView tv_author=(TextView) viewHolder.getView(R.id.tv_author);
        tv_author.setText(reply.getAuthor().getUsername());
        Log.i(TAG, "getUsername:"+reply.getAuthor().getUsername());

        TextView floor_num=(TextView) viewHolder.getView(R.id.floor_num);
        floor_num.setText("("+(list.size()-position)+"楼)");

        TextView tv_time=(TextView) viewHolder.getView(R.id.tv_time);
        long time= TimeUtil.stringToLong(reply.getCreatedAt(), "yyyy-MM-dd HH:mm:ss");
        tv_time.setText(TimeUtil.getDescriptionTimeFromTimestamp(time));
        //回复的内容
        TextView tv_reply_content =(TextView) viewHolder.getView(R.id.tv_reply_content);
        SpannableString spannableString = FaceTextUtil
                .toSpannableString(context, reply.getContent());
        tv_reply_content.setText(spannableString);
        //被回复的内容
        TextView tv_bereply_content =(TextView) viewHolder.getView(R.id.tv_bereply_content);
        String replyContent=reply.getReplyContent();
        SpannableString spannableString1 = FaceTextUtil
                .toSpannableString(context, replyContent);
        //先清空回复内容防止被重用
        tv_bereply_content.setText("");
        tv_bereply_content.setVisibility(View.GONE);
        //当有被回复的内容时才显示
        if(!"".equals(replyContent) && replyContent!=null){
            tv_bereply_content.setText(spannableString1);
            tv_bereply_content.setVisibility(View.VISIBLE);
            tv_bereply_content.setBackgroundResource(R.drawable.bereply_bg);
        }
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
}
