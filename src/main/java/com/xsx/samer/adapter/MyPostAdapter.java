package com.xsx.samer.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.model.Post;
import com.xsx.samer.model.Reply;
import com.xsx.samer.utils.FaceTextUtil;
import com.xsx.samer.utils.ImageLoadOptions;
import com.xsx.samer.utils.PixelUtil;
import com.xsx.samer.utils.TimeUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 在个人中心显示的我的帖子
 * Created by XSX on 2015/10/14.
 */
public class MyPostAdapter extends CommonReclclerViewAdapter<Post>{
    private View view;
    private Post post;
    public MyPostAdapter(Context context, List<Post> list) {
        super(context, list);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view= LayoutInflater.from(context).inflate(R.layout.item_my_post,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        post = list.get(position);
        TextView tv_time = (TextView) viewHolder.getView(R.id.tv_time);
        TextView tv_content = (TextView) viewHolder.getView(R.id.tv_content);


        // 设置时间
        long time = TimeUtil.stringToLong(post.getCreatedAt(),
                "yyyy-MM-dd HH:mm:ss");
        tv_time.setText(TimeUtil.getDescriptionTimeFromTimestamp(time));
        // 设置文字内容
        SpannableString spannableString = FaceTextUtil.toSpannableString(
                context, post.getContent());
        tv_content.setText(spannableString);
        tv_content.setVisibility(View.VISIBLE);
        // 设置图片内容
        GridLayout layout_content = (GridLayout) viewHolder
                .getView(R.id.layout_content);
        layout_content.removeAllViews();
        layout_content.setVisibility(View.VISIBLE);
        List<String> imagesUrl = post.getImages();
        if (imagesUrl != null && imagesUrl.size() > 0) {
            //layout_content.removeAllViews();
            layout_content.setVisibility(View.VISIBLE);
            for (String imageUrl : imagesUrl) {
                if (imageUrl != null && !imageUrl.equals("")) {
                    // imageView会被生成多次
                    ImageView imageView = new ImageView(context);
                    // 记得设置高度和宽度
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(PixelUtil.dp2px(10), 0, 0, 0);
                    ImageLoader.getInstance().displayImage(imageUrl, imageView,
                            ImageLoadOptions.getOptions());
                    layout_content.addView(imageView, params);
                }
            }

        } else {
            layout_content.setVisibility(View.GONE);
        }

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
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


