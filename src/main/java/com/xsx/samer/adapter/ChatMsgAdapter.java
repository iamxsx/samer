package com.xsx.samer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xsx.samer.R;
import com.xsx.samer.model.User;
import com.xsx.samer.ui.ImageBrowserActivity;
import com.xsx.samer.ui.LocationActivity;
import com.xsx.samer.ui.MyDetailActivity;
import com.xsx.samer.utils.FaceTextUtil;
import com.xsx.samer.utils.ImageLoadOptions;
import com.xsx.samer.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.im.BmobDownloadManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.DownloadListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by XSX on 2015/10/12.
 */
public class ChatMsgAdapter extends CommonReclclerViewAdapter<BmobMsg>{

    private View view;
    // 8种聊天信息
    // 收到的文字信息
    private final int TYPE_RECEIVER_TXT = 0;
    // 发出的文字信息
    private final int TYPE_SEND_TXT = 1;
    // 发出的图片
    private final int TYPE_SEND_IMAGE = 2;
    // 收到的图片
    private final int TYPE_RECEIVER_IMAGE = 3;
    // 发出的位置
    private final int TYPE_SEND_LOCATION = 4;
    // 收到的位置
    private final int TYPE_RECEIVER_LOCATION = 5;
    // 发出的语音
    private final int TYPE_SEND_VOICE = 6;
    // 收到的语音
    private final int TYPE_RECEIVER_VOICE = 7;

    // 当前登录用户的ObjectId
    String currentObjectId = "";
    /**
     * 加载图片的配置
     */
    private DisplayImageOptions options;
    /**
     * 读取图片的监听器
     */
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    // 图片加载的监听器
    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {
        // 用来存储已经加载过的图片的url，不必每次都加载
        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                // 尚未加载过的图片
                if (!displayedImages.contains(imageUri)) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    public ChatMsgAdapter(List<BmobMsg> list, Context context) {
        // 调用父类的构造器
        super(context,list);
        // 获取当前登录用户的ObjectId
        currentObjectId = BmobUserManager.getInstance(context)
                .getCurrentUserObjectId();
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }


    /**
     * 得到消息中图片的地址
     * @param bmobMsg
     * @return
     */
    protected String getImageUrl(BmobMsg bmobMsg) {
        String showUrl = "";
        String text = bmobMsg.getContent();
        if(bmobMsg.getBelongId().equals(currentObjectId)){
            //如果是发送的图片的话，因为开始发送存储的地址是本地地址，发送成功之后存储的是本地地址+"&"+网络地址，因此需要判断下
            if(text.contains("&")){
                showUrl = text.split("&")[0];
            }else{
                showUrl = text;
            }
        }else{//如果是收到的消息，则需要从网络下载
            showUrl = text;
        }
        return showUrl;
    }

    private void dealWithImage(int position, final ProgressBar progress_load,
                               ImageView iv_fail_resend, TextView tv_send_status,
                               ImageView iv_picture, BmobMsg bmobMsg) {
        String text = bmobMsg.getContent();
        if(getItemViewType(position)==TYPE_SEND_IMAGE){//发送的消息
            if(bmobMsg.getStatus()==BmobConfig.STATUS_SEND_START){
                progress_load.setVisibility(View.VISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
            }else if(bmobMsg.getStatus()==BmobConfig.STATUS_SEND_SUCCESS){
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.VISIBLE);
                tv_send_status.setText("已发送");
            }else if(bmobMsg.getStatus()==BmobConfig.STATUS_SEND_FAIL){
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.VISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
            }else if(bmobMsg.getStatus()==BmobConfig.STATUS_SEND_RECEIVERED){
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.VISIBLE);
                tv_send_status.setText("已阅读");
            }
//			如果是发送的图片的话，因为开始发送存储的地址是本地地址，发送成功之后存储的是本地地址+"&"+网络地址，因此需要判断下
            String showUrl = "";
            if(text.contains("&")){
                showUrl = text.split("&")[0];
            }else{
                showUrl = text;
            }
            //为了方便每次都是取本地图片显示
            ImageLoader.getInstance().displayImage(showUrl, iv_picture);
        }else{
            ImageLoader.getInstance().displayImage(text, iv_picture,options,new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progress_load.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view,
                                            FailReason failReason) {
                    progress_load.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progress_load.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progress_load.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    /**
     * @param parent
     * @param viewType 是getItemViewType返回的值
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = 0;
        switch (viewType) {
            case TYPE_RECEIVER_TXT:
                layoutId = R.layout.item_chat_received_message;
                break;
            case TYPE_SEND_TXT:
                layoutId = R.layout.item_chat_sent_message;
                break;
            case TYPE_SEND_IMAGE:
                layoutId = R.layout.item_chat_sent_image;
                break;
            case TYPE_RECEIVER_IMAGE:
                layoutId = R.layout.item_chat_received_image;
                break;
            case TYPE_SEND_LOCATION:
                layoutId = R.layout.item_chat_sent_location;
                break;
            case TYPE_RECEIVER_LOCATION:
                layoutId = R.layout.item_chat_received_location;
                break;
            case TYPE_SEND_VOICE:
                layoutId = R.layout.item_chat_sent_voice;
                break;
            case TYPE_RECEIVER_VOICE:
                layoutId = R.layout.item_chat_received_voice;
                break;
        }
        view=LayoutInflater.from(context).inflate(layoutId,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
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


        final BmobMsg bmobMsg=list.get(position);
        // 时间
        TextView tv_time = (TextView) viewHolder.getView(R.id.tv_time);
        // 头像
        ImageView iv_avatar = (ImageView) viewHolder.getView(R.id.iv_avatar);
        // 消息内容
        TextView tv_message = (TextView) viewHolder.getView(R.id.tv_message);
        // 图片信息中的图片
        ImageView iv_picture = (ImageView) viewHolder.getView(R.id.iv_picture);
        // 当图片发送失败时显示的图片
        ImageView iv_fail_resend = (ImageView) viewHolder
                .getView(R.id.iv_fail_resend);
        // 发送状态的文本信息
        TextView tv_send_status = (TextView) viewHolder
                .getView(R.id.tv_send_status); // 发送中，已送达，发送失败
        // 发送时显示的进度条
        final ProgressBar progress_load = (ProgressBar) viewHolder
                .getView(R.id.progress_load);
        // 语音消息的图片
        final ImageView iv_voice = (ImageView) viewHolder.getView(R.id.iv_voice);
        // 语音消息的长度
        final TextView tv_voice_length = (TextView) viewHolder
                .getView(R.id.tv_voice_length);
        // 位置信息
        TextView tv_location = (TextView) viewHolder.getView(R.id.tv_location);

        // 得到用户头像的url
        String avatar = bmobMsg.getBelongAvatar();
        if (avatar != null && !avatar.equals("")) {// 加载头像-为了不每次都加载头像
            ImageLoader.getInstance().displayImage(avatar, iv_avatar,
                    ImageLoadOptions.getOptions(), animateFirstListener);
        } else {
            // 设置成默认图片
            iv_avatar.setImageResource(R.mipmap.ic_launcher);
        }

        iv_avatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final Intent intent = new Intent(context, MyDetailActivity.class);
                if (getItemViewType(position) == TYPE_RECEIVER_TXT
                        || getItemViewType(position) == TYPE_RECEIVER_IMAGE
                        || getItemViewType(position) == TYPE_RECEIVER_LOCATION
                        || getItemViewType(position) == TYPE_RECEIVER_VOICE) {
                    User user=new User();
                    //查找出对应的用户
                    BmobQuery<User> query=new BmobQuery<User>();
                    query.addWhereEqualTo("objectId",bmobMsg.getBelongId());
                    query.findObjects(context, new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            intent.putExtra("target_user",list.get(0));
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                    intent.putExtra("from", "friend");
                    intent.putExtra("username", bmobMsg.getBelongUsername());
                } else {
                    intent.putExtra("from", "me");
                }
                context.startActivity(intent);
            }
        });
        // 设置消息的发送时间，要格式化
        tv_time.setText(TimeUtil.getChatTime(Long.parseLong(bmobMsg
                .getMsgTime())));
        // 对于进度条，发送状态，重发按钮的设置
        if (getItemViewType(position) == TYPE_SEND_TXT
                // ||getItemViewType(position)==TYPE_SEND_IMAGE//图片单独处理
                || getItemViewType(position) == TYPE_SEND_LOCATION
                || getItemViewType(position) == TYPE_SEND_VOICE) {// 只有自己发送的消息才有重发机制
            // 状态描述
            if (bmobMsg.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// 发送成功
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                if (bmobMsg.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_send_status.setVisibility(View.GONE);
                    tv_voice_length.setVisibility(View.VISIBLE);
                } else {
                    tv_send_status.setVisibility(View.VISIBLE);
                    tv_send_status.setText("已发送");
                }
            } else if (bmobMsg.getStatus() == BmobConfig.STATUS_SEND_FAIL) {// 服务器无响应或者查询失败等原因造成的发送失败，均需要重发
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.VISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
                if (bmobMsg.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_voice_length.setVisibility(View.GONE);
                }
            } else if (bmobMsg.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {// 对方已接收到
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                if (bmobMsg.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_send_status.setVisibility(View.GONE);
                    tv_voice_length.setVisibility(View.VISIBLE);
                } else {
                    tv_send_status.setVisibility(View.VISIBLE);
                    tv_send_status.setText("已阅读");
                }
            } else if (bmobMsg.getStatus() == BmobConfig.STATUS_SEND_START) {// 开始上传
                progress_load.setVisibility(View.VISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
                if (bmobMsg.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_voice_length.setVisibility(View.GONE);
                }
            }
        }
        // 设置发送的内容
        // 根据类型显示内容
        final String text = bmobMsg.getContent();
        switch (bmobMsg.getMsgType()) {
            case BmobConfig.TYPE_TEXT:
                try {
                    SpannableString spannableString = FaceTextUtil
                            .toSpannableString(context, text);
                    tv_message.setText(spannableString);
                } catch (Exception e) {
                }
                break;

            case BmobConfig.TYPE_IMAGE:// 图片类
                try {
                    if (text != null && !text.equals("")) {
                        // 发送成功之后存储的图片类型的content和接收到的是不一样的
                        dealWithImage(position, progress_load, iv_fail_resend,
                                tv_send_status, iv_picture, bmobMsg);
                    }
                    iv_picture.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(context,
                                    ImageBrowserActivity.class);
                            ArrayList<String> photos = new ArrayList<String>();
                            photos.add(getImageUrl(bmobMsg));
                            intent.putStringArrayListExtra("photos", photos);
                            intent.putExtra("position", 0);
                            context.startActivity(intent);
                        }
                    });

                } catch (Exception e) {
                }
                break;

            case BmobConfig.TYPE_LOCATION:// 位置信息
                try {
                    if (text != null && !text.equals("")) {
                        String address = text.split("&")[0];
                        final String latitude = text.split("&")[1];// 维度
                        final String longtitude = text.split("&")[2];// 经度
                        tv_location.setText(address);
                        tv_location.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                Intent intent = new Intent(context,
                                        LocationActivity.class);
                                intent.putExtra("type", "scan");
                                intent.putExtra("latitude",
                                        Double.parseDouble(latitude));// 维度
                                intent.putExtra("longtitude",
                                        Double.parseDouble(longtitude));// 经度
                                context.startActivity(intent);
                            }
                        });
                    }
                } catch (Exception e) {

                }
                break;
            case BmobConfig.TYPE_VOICE:// 语音消息
                try {
                    if (text != null && !text.equals("")) {
                        tv_voice_length.setVisibility(View.VISIBLE);
                        String content = bmobMsg.getContent();
                        if (bmobMsg.getBelongId().equals(currentObjectId)) {// 发送的消息
                            if (bmobMsg.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED
                                    || bmobMsg.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// 当发送成功或者发送已阅读的时候，则显示语音长度
                                tv_voice_length.setVisibility(View.VISIBLE);
                                String length = content.split("&")[2];
                                tv_voice_length.setText(length + "\''");
                            } else {
                                tv_voice_length.setVisibility(View.INVISIBLE);
                            }
                        } else {// 收到的消息
                            boolean isExists = BmobDownloadManager
                                    .checkTargetPathExist(currentObjectId, bmobMsg);
                            if (!isExists) {// 若指定格式的录音文件不存在，则需要下载，因为其文件比较小，故放在此下载
                                String netUrl = content.split("&")[0];
                                final String length = content.split("&")[1];
                                BmobDownloadManager downloadTask = new BmobDownloadManager(
                                        context, bmobMsg, new DownloadListener() {

                                    @Override
                                    public void onStart() {
                                        // TODO Auto-generated method stub
                                        progress_load
                                                .setVisibility(View.VISIBLE);
                                        tv_voice_length
                                                .setVisibility(View.GONE);
                                        iv_voice.setVisibility(View.INVISIBLE);// 只有下载完成才显示播放的按钮
                                    }

                                    @Override
                                    public void onSuccess() {
                                        // TODO Auto-generated method stub
                                        progress_load
                                                .setVisibility(View.GONE);
                                        tv_voice_length
                                                .setVisibility(View.VISIBLE);
                                        tv_voice_length.setText(length
                                                + "\''");
                                        iv_voice.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError(String error) {
                                        // TODO Auto-generated method stub
                                        progress_load
                                                .setVisibility(View.GONE);
                                        tv_voice_length
                                                .setVisibility(View.GONE);
                                        iv_voice.setVisibility(View.INVISIBLE);
                                    }
                                });
                                downloadTask.execute(netUrl);
                            } else {
                                String length = content.split("&")[2];
                                tv_voice_length.setText(length + "\''");
                            }
                        }
                    }
                    // 播放语音文件
                    iv_voice.setOnClickListener(new NewRecordPlayClickListener(
                            context, bmobMsg, iv_voice));
                } catch (Exception e) {

                }

                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        BmobMsg bmobMsg = list.get(position);
        int msgType = bmobMsg.getMsgType();
        // bmobMsg.getBelongId()获得当前消息的来源id，如果与当前登录用户的id相同，则是发出的消息，反之则是收到的消息
        if (msgType == BmobConfig.TYPE_IMAGE) {
            return bmobMsg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE
                    : TYPE_RECEIVER_IMAGE;
        } else if (msgType == BmobConfig.TYPE_LOCATION) {
            return bmobMsg.getBelongId().equals(currentObjectId) ? TYPE_SEND_LOCATION
                    : TYPE_RECEIVER_LOCATION;
        } else if (msgType == BmobConfig.TYPE_VOICE) {
            return bmobMsg.getBelongId().equals(currentObjectId) ? TYPE_SEND_VOICE
                    : TYPE_RECEIVER_VOICE;
        } else {
            return bmobMsg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT
                    : TYPE_RECEIVER_TXT;
        }

    }

}
