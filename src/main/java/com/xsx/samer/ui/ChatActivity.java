package com.xsx.samer.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xsx.samer.MyMessageReceiver;
import com.xsx.samer.R;
import com.xsx.samer.adapter.ChatMsgAdapter;
import com.xsx.samer.adapter.CommonReclclerViewAdapter;
import com.xsx.samer.adapter.EmoAdapter;
import com.xsx.samer.adapter.EmoViewPagerAdapter;
import com.xsx.samer.adapter.NewRecordPlayClickListener;
import com.xsx.samer.config.BmobConstancts;
import com.xsx.samer.model.FaceText;
import com.xsx.samer.utils.CommonUtil;
import com.xsx.samer.utils.FaceTextUtil;
import com.xsx.samer.widget.EmoticonsEditText;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;


/**
 * 聊天界面
 * @author XSX
 *
 */
public class ChatActivity extends BaseActivity implements OnClickListener ,EventListener {
    private static final String TAG = "ChatActivity";
    /**
     * 表情按钮
     */
    private Button btn_chat_emo;
    /**
     * 发送按钮
     */
    private Button btn_chat_send;
    /**
     * 更多按钮
     */
    private Button btn_chat_add;
    /**
     * 切换为键盘输入的按钮
     */
    private Button btn_chat_keyboard;
    /**
     * 长按录音按钮
     */
    private Button btn_speak;
    /**
     * 切换为语音输入的按钮
     */
    private Button btn_chat_voice;
    /**
     * 回复框
     */
    private EmoticonsEditText edit_user_comment;
    /**
     * 底部更多面板照片，拍照，位置
     */
    private LinearLayout layout_add;
    /**
     * 发送照片
     */
    private TextView tv_picture;
    /**
     * 拍照
     */
    private TextView tv_camera;
    /**
     * 位置
     */
    private TextView tv_location;
    /**
     * 表情面板
     */
    private LinearLayout layout_more;
    private LinearLayout layout_emo;
    /**
     * 表情面板的内容
     */
    private List<View> views;
    /**
     * 表情面板的viewpager
     */
    private ViewPager pager_emo;
    /**
     * 聊天管理-用于管理聊天：包括发送（聊天消息、Tag消息）、保存消息、绑定用户等
     */
    private BmobChatManager chatManager;
    /**
     * 当前聊天对象
     */
    private BmobChatUser targetUser;
    /**
     * 当前聊天对象id
     */
    private String targetId;
    private ChatMsgAdapter mAdapter;
    private NewBroadcastReceiver receiver;

    /**
     * 表情页面数量
     */
    private static int MsgPagerNum;

    /**
     * 话筒动画
     */
    private Drawable[] drawable_Anims;
    /**
     * 话筒图片
     */
    private ImageView iv_record;

    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatManager=BmobChatManager.getInstance(this);
        targetUser=(BmobChatUser) this.getIntent().getSerializableExtra("bmobChatUser");
        targetId=targetUser.getObjectId();
        //注册新消息广播
        initNewMessageBroadCast();
        //初始化各种控件
        initView();
    }

    /**
     * 初始化广播
     */
    private void initNewMessageBroadCast(){
        // 注册接收消息广播
        receiver = new NewBroadcastReceiver();
        //设置广播的意图，当接收到该意图时才会发生广播
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onMessage(BmobMsg bmobMsg) {
        Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = bmobMsg;
        handler.sendMessage(handlerMsg);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        if (conversionId.split("&")[1].equals(targetId)) {
            // 修改界面上指定消息的阅读状态
            for (BmobMsg msg : mAdapter.getList()) {
                if (msg.getConversationId().equals(conversionId)
                        && msg.getMsgTime().equals(msgTime)) {
                    msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onNetChange(boolean b) {
        if (!CommonUtil.isNetworkAvailable(this)) {
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation bmobInvitation) {

    }

    @Override
    public void onOffline() {

    }

    /**
     * 新聊天消息的接收器
     * @author XSX
     *
     */
    private class NewBroadcastReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("fromId");
            String msgId = intent.getStringExtra("msgId");
            String msgTime = intent.getStringExtra("msgTime");
            Log.i(TAG,"from="+from+",msgId="+msgId+",msgTime="+msgTime);
            //得到消息对象
            BmobMsg msg=chatManager.getMessage(msgId, msgTime);
            if (!from.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
                return;
            //添加到当前页面
            mAdapter.add(msg);
            // 记得把广播给终结掉
            abortBroadcast();
        }

    }

    private void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle(targetUser.getNick());
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //初始化RecyclerView
        initRecyclerView();
        //初始化输入栏下部
        initBottomView();
        //初始化
        initVoiceView();
    }

    private void initRecyclerView() {
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new ChatMsgAdapter(initMsgData(),this);
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.setVerticalScrollbarPosition(mAdapter.getItemCount()-1);
        mRecyclerView.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1);
    }

    RelativeLayout layout_record;
    TextView tv_voice_tips;
    /**
     * 初始化语音布局
     */
    private void initVoiceView() {
        layout_record = (RelativeLayout) findViewById(R.id.layout_record);
        tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        btn_speak.setOnTouchListener(new VoiceTouchListen());
        initVoiceAnimRes();
        initRecordManager();
    }

    /**
     * 长按说话
     * @ClassName: VoiceTouchListen
     * @Description: TODO
     * @author smile
     * @date 2014-7-1 下午6:10:16
     */
    class VoiceTouchListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtil.checkSdCard()) {
                        ShowToast("发送语音需要sdcard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        // 开始录音
                        recordManager.startRecording(targetId);
                    } catch (Exception e) {
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        tv_voice_tips
                                .setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {// 放弃录音
                            recordManager.cancelRecording();
                            Log.i(TAG, "放弃发送语音");
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
                                Log.i(TAG, "发送语音");
                                sendVoiceMessage(
                                        recordManager.getRecordFilePath(targetId),
                                        recordTime);
                            } else {// 录音时间过短，则提示录音过短的提示
                                layout_record.setVisibility(View.GONE);
                                ShowToast("录音时间过短");
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * 发送语音消息
     * @Title: sendImageMessage
     * @Description: TODO
     * @param @param localPath
     * @return void
     * @throws
     */
    private void sendVoiceMessage(String local, int length) {
        chatManager.sendVoiceMessage(targetUser, local, length,
                new UploadListener() {

                    @Override
                    public void onStart(BmobMsg msg) {
                        refreshMessage(msg);
                    }

                    @Override
                    public void onSuccess() {
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int error, String arg1) {
                        Log.i(TAG,"上传语音失败 -->arg1：" + arg1);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == NEW_MESSAGE) {
                BmobMsg message = (BmobMsg) msg.obj;
                String uid = message.getBelongId();
                BmobMsg m = BmobChatManager.getInstance(ChatActivity.this).getMessage(message.getConversationId(), message.getMsgTime());
                if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
                    return;
                mAdapter.add(m);
                //取消当前聊天对象的未读标示
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
        }
    };

    public static final int NEW_MESSAGE = 0x001;// 收到消息

    BmobRecordManager recordManager;
    private void initRecordManager(){
        // 语音相关管理器
        recordManager = BmobRecordManager.getInstance(this);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumnChanged(int value) {
                iv_record.setImageDrawable(drawable_Anims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                Log.i(TAG, "已录音长度:" + recordTime);
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    // 取消录音框
                    layout_record.setVisibility(View.INVISIBLE);
                    // 发送语音消息
                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                } else {

                }
            }
        });
    }

    /**
     * 初始化语音动画资源
     */
    private void initVoiceAnimRes() {
        drawable_Anims = new Drawable[] {
                getResources().getDrawable(R.drawable.chat_icon_voice2),
                getResources().getDrawable(R.drawable.chat_icon_voice3),
                getResources().getDrawable(R.drawable.chat_icon_voice4),
                getResources().getDrawable(R.drawable.chat_icon_voice5),
                getResources().getDrawable(R.drawable.chat_icon_voice6) };
    }



    /**
     * 加载listview的内容
     */
    private void initOrRefresh() {
        if(mAdapter!=null){
            // 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
            if (MyMessageReceiver.mNewNum != 0) {
                //有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
                int news=  MyMessageReceiver.mNewNum;
                int size = initMsgData().size();
                for(int i=(news-1);i>=0;i--){
                    //从会话消息里取出最近的信息
                    mAdapter.add(initMsgData().get(size-(i+1)));// 添加最后一条消息到界面显示
                }
                //设置listview显示的位置
                //xListView.setSelection(mAdapter.getCount()- 1);
                //mRecyclerView.setVerticalScrollbarPosition(mAdapter.getItemCount()-1);
                mRecyclerView.getLayoutManager().scrollToPosition(mAdapter.getItemCount()-1);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }else{
            mAdapter=new ChatMsgAdapter(initMsgData(), this);
            mRecyclerView.setAdapter(mAdapter);
        }

    }
    /**加载消息历史，从数据库中读出
     * 针对单聊 获取指定会话id的所有消息 ，支持分页操作
     * @return
     */
    private List<BmobMsg> initMsgData() {
        //分页
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,MsgPagerNum);
        return list;
    }

    private void initBottomView() {
        // 最左边
        btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
        btn_chat_emo = (Button) findViewById(R.id.btn_chat_emo);
        btn_chat_add.setOnClickListener(this);
        btn_chat_emo.setOnClickListener(this);
        // 最右边
        btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
        btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
        btn_chat_voice.setOnClickListener(this);
        btn_chat_keyboard.setOnClickListener(this);
        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(this);
        // 最下面
        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
        layout_add = (LinearLayout) findViewById(R.id.layout_add);
        //初始化更多面板
        initAddView();
        //初始化表情面板
        initEmoView();
        // 语音框
        btn_speak = (Button) findViewById(R.id.btn_speak);
        // 输入框
        edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
        edit_user_comment.setOnClickListener(this);
        edit_user_comment.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private List<FaceText> emos;
    /**
     * 初始化表情布局,可以滑动的两页
     */
    private void initEmoView() {
        pager_emo = (ViewPager) findViewById(R.id.pager_emo);
        emos = FaceTextUtil.faceTexts;

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));
    }

    private View getGridView(final int i) {
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoAdapter gridAdapter = new EmoAdapter(this, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.getText().toString();
                try {
                    if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
                        int start = edit_user_comment.getSelectionStart();
                        CharSequence content = edit_user_comment.getText()
                                .insert(start, key);
                        edit_user_comment.setText(content);
                        // 定位光标位置
                        CharSequence info = edit_user_comment.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText,
                                    start + key.length());
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
        return view;
    }

    private void initAddView() {
        tv_picture = (TextView) findViewById(R.id.tv_picture);
        tv_camera = (TextView) findViewById(R.id.tv_camera);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_picture.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        BmobMsg message;
        switch (v.getId()) {
            case R.id.edit_user_comment:// 点击文本输入框
                //xListView.setSelection(xListView.getCount() - 1);
                mRecyclerView.getLayoutManager().scrollToPosition(mAdapter.getItemCount()-1);
                //当文本框被点击时，更多面板应消失
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
            case R.id.btn_chat_add :
                if (layout_more.getVisibility() == View.GONE) {
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    layout_emo.setVisibility(View.GONE);
                    //hideSoftInputView();
                } else {
                    if (layout_emo.getVisibility() == View.VISIBLE) {
                        layout_emo.setVisibility(View.GONE);
                        layout_add.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.btn_chat_emo :
                if (layout_more.getVisibility() == View.GONE) {
                    showEditState(true);
                } else {
                    if (layout_add.getVisibility() == View.VISIBLE) {
                        layout_add.setVisibility(View.GONE);
                        layout_emo.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.btn_chat_voice :
                edit_user_comment.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                hideSoftInputView();
                break;
            case R.id.btn_chat_keyboard :// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
                showEditState(false);
                break;
            case R.id.btn_chat_send :
                final String msg = edit_user_comment.getText().toString();
                if (msg.equals("")) {
                    ShowToast("请输入发送消息!");
                    return;
                }
                boolean isNetConnected = CommonUtil.isNetworkAvailable(this);
                if (!isNetConnected) {
                    ShowToast(R.string.network_tips);
                    return;
                }
                // 组装BmobMessage对象
                message= BmobMsg.createTextSendMsg(this, targetId, msg);
                // 默认发送完成，将数据保存到本地消息表和最近会话表中
                chatManager.sendTextMessage(targetUser, message);
                // 刷新界面
                refreshMessage(message);
                break;
            case R.id.tv_camera:// 拍照
                selectImageFromCamera();
                break;
            case R.id.tv_picture:// 图片
                selectImageFromLocal();
                break;
            case R.id.tv_location:// 位置
                selectLocationFromMap();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BmobConstancts.REQUESTCODE_TAKE_CAMERA:// 当取到值的时候才上传path路径下的图片到服务器
                    Log.i(TAG, "本地图片的地址：" + localCameraPath);
                    sendImageMessage(localCameraPath);
                    break;
                case BmobConstancts.REQUESTCODE_TAKE_LOCAL:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(
                                    selectedImage, null, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex("_data");
                            String localSelectPath = cursor.getString(columnIndex);
                            cursor.close();
                            if (localSelectPath == null
                                    || localSelectPath.equals("null")) {
                                ShowToast("找不到您想要的图片");
                                return;
                            }
                            sendImageMessage(localSelectPath);
                        }
                    }
                    break;
                case BmobConstancts.REQUESTCODE_TAKE_LOCATION:// 地理位置
                    double latitude = data.getDoubleExtra("x", 0);// 维度
                    double longtitude = data.getDoubleExtra("y", 0);// 经度
                    String address = data.getStringExtra("address");
                    if (address != null && !address.equals("")) {
                        sendLocationMessage(address, latitude, longtitude);
                    } else {
                        ShowToast("无法获取到您的位置信息!");
                    }

                    break;
            }
        }
    }

    /**
     * 发送位置信息
     * @Title: sendLocationMessage
     * @Description: TODO
     * @param @param address
     * @param @param latitude
     * @param @param longtitude
     * @return void
     * @throws
     */
    private void sendLocationMessage(String address, double latitude,
                                     double longtitude) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }
        // 组装BmobMessage对象
        BmobMsg message = BmobMsg.createLocationSendMsg(this, targetId,
                address, latitude, longtitude);
        // 默认发送完成，将数据保存到本地消息表和最近会话表中
        chatManager.sendTextMessage(targetUser, message);
        // 刷新界面
        refreshMessage(message);
    }

    /**
     * 默认先上传本地图片，之后才显示出来 sendImageMessage
     * @Title: sendImageMessage
     * @Description: TODO
     * @param @param localPath
     * @return void
     * @throws
     */
    private void sendImageMessage(String local) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }
        chatManager.sendImageMessage(targetUser, local, new UploadListener() {

            @Override
            public void onStart(BmobMsg msg) {
                // TODO Auto-generated method stub
                Log.i(TAG, "开始上传onStart：" + msg.getContent() + ",状态："
                        + msg.getStatus());
                refreshMessage(msg);
            }

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int error, String arg1) {
                // TODO Auto-generated method stub
                Log.i(TAG, "上传失败 -->arg1：" + arg1);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 启动地图
     *
     * @Title: selectLocationFromMap
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void selectLocationFromMap() {
//        Intent intent = new Intent(this, LocationActivity.class);
//        intent.putExtra("type", "select");
//        startActivityForResult(intent, BmobConstancts.REQUESTCODE_TAKE_LOCATION);
    }

    private String localCameraPath = "";// 拍照后得到的图片地址

    /**
     * 启动相机拍照 startCamera
     *
     * @Title: startCamera
     * @throws
     */
    public void selectImageFromCamera() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(BmobConstancts.BMOB_PICTURE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis())
                + ".jpg");
        localCameraPath = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent,
                BmobConstancts.REQUESTCODE_TAKE_CAMERA);
    }

    /**
     * 选择图片
     * @Title: selectImage
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    public void selectImageFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, BmobConstancts.REQUESTCODE_TAKE_LOCAL);
    }


    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     * @param @param isEmo: 用于区分文字和表情
     */
    private void showEditState(boolean isEmo) {
        edit_user_comment.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_user_comment.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
            showSoftInputView();
        }
    }

    // 显示软键盘
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_user_comment, 0);
        }
    }

    private void refreshMessage(BmobMsg message) {
        // 更新界面
        mAdapter.add(message);
        mRecyclerView.getLayoutManager().scrollToPosition(mAdapter.getItemCount()-1);
        edit_user_comment.setText("");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 新消息到达，重新刷新界面
        initOrRefresh();
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
        BmobNotifyManager.getInstance(this).cancelNotify();
        BmobDB.create(this).resetUnread(targetId);
        //清空消息未读数-这个要在刷新之后
        MyMessageReceiver.mNewNum=0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 监听推送的消息
        // 停止录音
        if (recordManager.isRecording()) {
            recordManager.cancelRecording();
            layout_record.setVisibility(View.GONE);
        }
        // 停止播放录音
        if (NewRecordPlayClickListener.isPlaying
                && NewRecordPlayClickListener.currentPlayListener != null) {
            NewRecordPlayClickListener.currentPlayListener.stopPlayRecord();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideSoftInputView();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }

    }


}
