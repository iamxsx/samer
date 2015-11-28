package com.xsx.samer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.model.Post;
import com.xsx.samer.model.Reply;
import com.xsx.samer.model.Topic;
import com.xsx.samer.utils.ImageLoadOptions;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;

/**
 * Created by XSX on 2015/10/12.
 */
public class TopicAdapter extends CommonReclclerViewAdapter<Topic>{
    private View view;
    public TopicAdapter(Context context, List<Topic> list) {
        super(context, list);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view=LayoutInflater.from(context).inflate(R.layout.item_topic, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        Topic topic=list.get(position);
        TextView tv_topic_name=(TextView) viewHolder.getView(R.id.tv_topic_name);
        final TextView tv_discuss_num=(TextView) viewHolder.getView(R.id.tv_discuss_num);
        ImageView iv_topic_bg=(ImageView) viewHolder.getView(R.id.iv_topic_bg);

        tv_topic_name.setText(topic.getTitle());

        BmobQuery<Post> query = new BmobQuery<Post>();
        query.addWhereEqualTo("topic", topic);
        query.count(context, Post.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                tv_discuss_num.setText(i + "人");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
        ImageLoader.getInstance().displayImage(topic.getTitleImg(), iv_topic_bg, ImageLoadOptions.getOptions());
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
