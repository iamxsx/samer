package com.xsx.samer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.model.Post;
import com.xsx.samer.model.Reply;
import com.xsx.samer.model.User;
import com.xsx.samer.ui.ImageBrowserActivity;
import com.xsx.samer.ui.MyDetailActivity;
import com.xsx.samer.ui.ReplyActivity;
import com.xsx.samer.utils.FaceTextUtil;
import com.xsx.samer.utils.ImageLoadOptions;
import com.xsx.samer.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 瀑布流布局的item adapter
 * Created by XSX on 2015/10/11.
 */
public class PostAdapter2 extends CommonReclclerViewAdapter<Post> implements View.OnClickListener {

    private static final String TAG = "PostAdapter";
    private Post post;
    //private TextView tv_praise;
    private ImageView iv_avator;
    private ImageView iv_content;

    private LinearLayout layout_praise;

    private int position;
    private int replyCount;
    private View view;

    public PostAdapter2(Context context, List<Post> list) {
        super(context, list);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_post_stagger, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        //多种颜色的背景
        switch ((position + 7) % 7) {
            case 0:
                //view.setBackgroundColor();
                view.setBackgroundResource(R.color.item_color1);
                break;
            case 1:
                //view.setBackgroundColor(Color.parseColor("#8552a1"));
                view.setBackgroundResource(R.color.item_color2);
                break;
            case 2:
                //view.setBackgroundColor(Color.parseColor("#1d953f"));
                view.setBackgroundResource(R.color.item_color3);
                break;
            case 3:
                //view.setBackgroundColor(Color.parseColor("#f58220"));
                view.setBackgroundResource(R.color.item_color4);
                break;
            case 4:
                //view.setBackgroundColor(Color.parseColor("#f58220"));
                view.setBackgroundResource(R.color.item_color5);
                break;
            case 5:
                //view.setBackgroundColor(Color.parseColor("#f58220"));
                view.setBackgroundResource(R.color.item_color6);
                break;
            case 6:
                //view.setBackgroundColor(Color.parseColor("#f58220"));
                view.setBackgroundResource(R.color.item_color7);
                break;

        }
        post = list.get(position);
        iv_avator = (ImageView) viewHolder.getView(R.id.iv_avator);
        TextView tv_author = (TextView) viewHolder.getView(R.id.tv_author);
        TextView tv_time = (TextView) viewHolder.getView(R.id.tv_time);
        TextView tv_content = (TextView) viewHolder.getView(R.id.tv_content);
        //tv_praise = (TextView) viewHolder.getView(R.id.tv_praise);

        //layout_praise = (LinearLayout) viewHolder.getView(R.id.layout_praise);
        //layout_praise.setTag(position);
        //layout_praise.setOnClickListener(this);

        //tv_praise.setText(post.getPraiseCount() + "");

        iv_avator.setOnClickListener(this);
        iv_avator.setTag(position);

        //btn_praise.setOnClickListener(this);
        //btn_praise.setTag(position);


        String avatar = post.getAvator();
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_avator,
                    ImageLoadOptions.getOptions());
        } else {
            if(currentUser.getSex().equals("男")){
                iv_avator.setImageResource(R.mipmap.male_default_icon);
            }else{
                iv_avator.setImageResource(R.mipmap.female_default_icon);

            }
        }
        // 设置时间
        long time = TimeUtil.stringToLong(post.getCreatedAt(),
                "yyyy-MM-dd HH:mm:ss");
        tv_time.setText(TimeUtil.getDescriptionTimeFromTimestamp(time));
        // 设置用户名
        tv_author.setText(post.getAuthor().getUsername());
        // 设置文字内容
        SpannableString spannableString = FaceTextUtil.toSpannableString(
                context, post.getContent());
        tv_content.setText(spannableString);
        tv_content.setVisibility(View.VISIBLE);
        final List<String> imagesUrl = post.getImages();
        iv_content = (ImageView) viewHolder.getView(R.id.iv_content);
        if (imagesUrl != null && imagesUrl.size() > 0) {
            //只取出第一张图片
            String url = imagesUrl.get(0);
            iv_content.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(url, iv_content,
                    ImageLoadOptions.getOptions());
        } else {
            iv_content.setVisibility(View.GONE);
        }

        iv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,
                        ImageBrowserActivity.class);
                ArrayList<String> photos = new ArrayList<String>();
                photos.add(imagesUrl.get(0));
                intent.putStringArrayListExtra("photos", photos);
                intent.putExtra("position", 0);
                context.startActivity(intent);
            }
        });

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
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

    @Override
    public void onClick(View view) {
        //防止item错乱
        int position = (int) view.getTag();
        switch (view.getId()) {
//            case R.id.layout_praise:
//                //praise(position);
//                break;
            case R.id.iv_avator:
                //进入用户详情界面
                post = list.get(position);
                Intent intent = new Intent(context, MyDetailActivity.class);
                User user = post.getAuthor();
                String currentUserObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
                if (user.getObjectId().equals(currentUserObjectId)) {
                    intent.putExtra("from", "me");
                } else {
                    intent.putExtra("from", "other");
                    intent.putExtra("target_user", post.getAuthor());
                }
                context.startActivity(intent);
            default:
                break;
        }
    }

    private List<User> praiseUsers=new ArrayList<>();

    /**
     * 查询出所有赞了的人
     */
    public void queryAllPrasieUsers() {
        BmobQuery<User> query = new BmobQuery<User>();
        //likes是Post表中的字段，用来存储所有喜欢该帖子的用户
        Post post1=new Post();
        post1.setObjectId(post.getObjectId());
        query.addWhereRelatedTo("likes", new BmobPointer(post1));
        query.findObjects(context, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> object) {
                for (User user : object) {
                    Log.i(TAG, "user id=" + user.getObjectId());
                }
                praiseUsers = object;
            }

            @Override
            public void onError(int code, String msg) {
            }
        });

    }


    /*先判断post是否被赞过
        是：
            判断自己是否赞过
            是：
                赞-1
                判断赞的数量，如果赞数量等于0，改变button和文本的状态，post没有被赞
            否：
                赞+1，因为已经是被赞的状态，button图片不用改变，改变文本即可
        否：
            赞+1，改变button状态


    */
    //private List<User> praiseUsers;
    User currentUser = BmobUserManager.getInstance(context).getCurrentUser(User.class);

    boolean isMePraise;

    /**
     * 点赞功能
     *
     * @param position 点击控件所对应item的位置
     */
//    public void praise(final int position) {
//        post = list.get(position);
//        BmobQuery<User> query = new BmobQuery<User>();
//        //likes是Post表中的字段，用来存储所有喜欢该帖子的用户
//        query.addWhereRelatedTo("likes", new BmobPointer(post));
//        query.findObjects(context, new FindListener<User>() {
//            @Override
//            public void onSuccess(List<User> object) {
//                for (User user : object) {
//                    Log.i(TAG, "user id=" + user.getObjectId());
//                }
//                praiseUsers = object;
//            }
//
//            @Override
//            public void onError(int code, String msg) {
//            }
//        });
//        // 查询喜欢这个帖子的所有用户，因此查询的是用户表
//        //先得到帖子的点赞数
//        int count = praiseUsers.size();
//        if (post.getIsPraise()) {
//            //循环整个praiseUsers，查看自己是否已经点过赞
//            for (User user : praiseUsers) {
//                if (user.getObjectId().equals(currentUser.getObjectId())) {
//                    isMePraise = true;
//                    break;
//                }
//            }
//            if (isMePraise) {
//                //判断当前user是否赞过
//                Log.i(TAG, "当前用户有赞过，取消赞");
//                //赞过，要取消赞
//                post.increment("praiseCount", -1);
//                BmobRelation relation = new BmobRelation();
//                relation.remove(currentUser);
//                post.setLikes(relation);
//                //取消赞之后判断赞的数量
//                if (count == 1) {
//                    tv_praise.setText("0");
//                    post.setIsPraise(false);
//                } else {
//                    tv_praise.setText(""+count);
//                }
//                //找到一个就不必再找了
//            } else {
//                //当前用户没有赞过，点赞
//                Log.i(TAG, "当前用户没有赞过，点赞");
//                post.increment("praiseCount");
//                tv_praise.setText("" + post.getPraiseCount());
//                BmobRelation relation = new BmobRelation();
//                //将点赞的人添加到集合中
//                relation.add(currentUser);
//                post.setLikes(relation);
//                post.setIsPraise(true);
//            }
//        } else
//
//        {
//            Log.i(TAG, "无赞，点赞");
//            count++;
//            tv_praise.setText("" + (count));
//            post.increment("praiseCount");
//            post.setIsPraise(true);
//            BmobRelation relation = new BmobRelation();
//            //将点赞的人添加到集合中
//            relation.add(currentUser);
//            post.setLikes(relation);
//        }
//        notifyItemChanged(position);
//        post.update(context, new
//
//                UpdateListener() {
//                    @Override
//                    public void onFailure(int arg0, String arg1) {
//                    }
//
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                });
//
//    }
}