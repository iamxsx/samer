package com.xsx.samer.adapter;

import android.content.Context;
import android.content.Intent;
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
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by XSX on 2015/10/11.
 */
public class PostAdapter extends CommonReclclerViewAdapter<Post> implements View.OnClickListener{

    private static final String TAG ="PostAdapter" ;
    private Post post;
    private LinearLayout ll_praise;
    private LinearLayout ll_share;
    private Button btn_praise;
    private Button btn_share;
    private Button btn_reply;
    private TextView tv_praise;
    private TextView tv_reply;
    private ImageView iv_avator;
    private ImageView iv_content;

    private ImageView iv_sex;

    private int position;
    private int replyCount;
    private View view;

    public PostAdapter(Context context, List<Post> list) {
        super(context, list);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view=LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        view.setOnClickListener(this);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        post = list.get(position);
        iv_avator = (ImageView) viewHolder.getView(R.id.iv_avator);
        TextView tv_author = (TextView) viewHolder.getView(R.id.tv_author);
        TextView tv_time = (TextView) viewHolder.getView(R.id.tv_time);
        TextView tv_content = (TextView) viewHolder.getView(R.id.tv_content);
        tv_praise = (TextView) viewHolder.getView(R.id.tv_praise);
        tv_reply = (TextView) viewHolder.getView(R.id.tv_reply);

        btn_praise = (Button) viewHolder.getView(R.id.btn_praise);
        btn_share = (Button) viewHolder.getView(R.id.btn_share);
        btn_reply = (Button) viewHolder.getView(R.id.btn_reply);

        ll_praise= (LinearLayout) viewHolder.getView(R.id.ll_praise);
        ll_share= (LinearLayout) viewHolder.getView(R.id.ll_share);

        iv_sex= (ImageView) viewHolder.getView(R.id.iv_sex);

        if (post.getAuthor().getSex().equals("男")){
            iv_sex.setImageResource(R.drawable.blue_male);
        }else{
            iv_sex.setImageResource(R.drawable.red_female);
        }
        tv_praise.setTag(position);
        tv_praise.setText("赞");
        if (post.getPraiseCount() == 0) {
            tv_praise.setText("赞");
        } else {
            tv_praise.setText(post.getPraiseCount() + "");
        }
        tv_reply.setText("评论");
        BmobQuery<Reply> query = new BmobQuery<Reply>();
        query.addWhereEqualTo("post", post);
        query.count(context, Reply.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                if (i != 0) {
                    tv_reply.setText(i + "");
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

        iv_avator.setOnClickListener(this);
        iv_avator.setTag(position);


        ll_praise.setOnClickListener(this);
        ll_praise.setTag(position);
        ll_share.setOnClickListener(this);
        ll_share.setTag(position);


        if (post.getIsPraise()) {
            btn_praise.setBackgroundResource(R.drawable.praise_press);
        } else {
            btn_praise.setBackgroundResource(R.drawable.praise_normal);
        }

        String avatar = post.getAvator();
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_avator,
                    ImageLoadOptions.getOptions());
        } else {
            if(post.getAuthor().getSex().equals("男")){
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
        tv_author.setText(post.getAuthor().getNick());
        // 设置文字内容
        SpannableString spannableString = FaceTextUtil.toSpannableString(
                context, post.getContent());
        tv_content.setText(spannableString);
        tv_content.setVisibility(View.VISIBLE);
        final List<String> imagesUrl = post.getImages();
        iv_content= (ImageView) viewHolder.getView(R.id.iv_content);
        if (imagesUrl != null && imagesUrl.size() > 0) {
            //只取出第一张图片
            String url=imagesUrl.get(0);
            iv_content.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(url, iv_content,
                            ImageLoadOptions.getOptions());


        } else {
            //layout_content.setVisibility(View.GONE);
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

    @Override
    public void onClick(View view) {
        //防止item错乱
        int position = Integer.parseInt(view.getTag().toString());
        switch (view.getId()) {
            case R.id.ll_praise:
                praise(position);
                break;
            case R.id.ll_share:
                share(position);
                break;
            case R.id.iv_avator:
                //进入用户详情界面
                post = list.get(position);
                Intent intent = new Intent(context, MyDetailActivity.class);
                User user = post.getAuthor();
                String currentUserObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
                if (user.getObjectId().equals(currentUserObjectId)) {
                    intent.putExtra("from", "me");
                } else {
                    //如果是好友。
                    if (BmobDB.create(context).getContactList().contains((BmobChatUser)user)){
                        intent.putExtra("from", "friend");
                    }else{
                        intent.putExtra("from", "other");
                    }
                    intent.putExtra("target_user",post.getAuthor());
                }
                context.startActivity(intent);
            default:
                break;
        }
    }

    /**
     * 分享功能
     *
     * @param position
     */

    public void share(int position) {
        post = list.get(position);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        intent.putExtra(Intent.EXTRA_TEXT, "发现有趣事物，快来看吧！ (分享自samer)");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent,
                "发现有趣事物，快来看吧 (分享自samer)"));
    }

    private List<User> praiseUsers;
    /**
     * 查询出所有赞了的人
     */
    public void queryAllPrasieUsers(){
        BmobQuery<User> query = new BmobQuery<User>();
        //likes是Post表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("likes", new BmobPointer(post));
        query.findObjects(context, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> object) {
                for (User user: object) {
                    Log.i(TAG,"user="+user.getObjectId());
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

    User currentUser=BmobUserManager.getInstance(context).getCurrentUser(User.class);
    /**
     * 点赞功能
     *
     * @param position 点击控件所对应item的位置
     */
    public void praise(final int position) {
        post = list.get(position);
        // 查询喜欢这个帖子的所有用户，因此查询的是用户表
        queryAllPrasieUsers();
        //先得到帖子的点赞数
        int count=post.getPraiseCount();
        //Log.i(TAG,"currentUser="+currentUser.getObjectId());
        if (post.getIsPraise()) {
            post.increment("praiseCount");
            tv_praise.setText("" + (count+1));
//            if(!praiseUsers.contains(currentUser)){
//               //判断当前user是否赞过
//                Log.i(TAG, "当前用户有赞过，取消赞");
//                //赞过，要取消赞
//                post.increment("praiseCount", -1);
//                BmobRelation relation=new BmobRelation();
//                relation.remove(currentUser);
//                post.setLikes(relation);
//                //取消赞之后判断赞的数量
//                if(post.getPraiseCount()==0){
//                    btn_praise.setBackgroundResource(R.drawable.praise_normal);
//                    tv_praise.setText("赞");
//                    post.setIsPraise(false);
//                }else{
//                    btn_praise.setBackgroundResource(R.drawable.praise_press);
//                    tv_praise.setText("" + post.getPraiseCount());
//                }
//            }else{
//                //当前用户没有赞过，点赞
//                Log.i(TAG, "当前用户没有赞过，点赞");
//                btn_praise.setBackgroundResource(R.drawable.praise_press);
//                post.increment("praiseCount");
//                tv_praise.setText("" + post.getPraiseCount());
//                BmobRelation relation=new BmobRelation();
//                //将点赞的人添加到集合中
//                relation.add(currentUser);
//                post.setLikes(relation);
//            }
        } else {
            Log.i(TAG, "无赞，点赞");
            post.increment("praiseCount");
            tv_praise.setText("" + (count + 1));
            btn_praise.setBackgroundResource(R.drawable.praise_press);
            post.setIsPraise(true);
//            BmobRelation relation=new BmobRelation();
//            //将点赞的人添加到集合中
//            relation.add(currentUser);
//            post.setLikes(relation);
        }

        post.update(context, new UpdateListener() {
            @Override
            public void onFailure(int arg0, String arg1) {
            }

            @Override
            public void onSuccess() {
                //查找出更新后的内容，否则界面不会改变
                BmobQuery<Post> query=new BmobQuery<Post>();
                query.addWhereEqualTo("objectId", post.getObjectId());
                query.addQueryKeys("praiseCount");
                query.findObjects(context, new FindListener<Post>() {
                    @Override
                    public void onSuccess(List<Post> list) {
                        post.setPraiseCount(list.get(0).getPraiseCount());
                        notifyItemChanged(position, null);
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

            }
        });
    }


}