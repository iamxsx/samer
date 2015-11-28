package com.xsx.samer.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.adapter.EmoAdapter;
import com.xsx.samer.adapter.EmoViewPagerAdapter;
import com.xsx.samer.adapter.ReplyAdapter;
import com.xsx.samer.model.ClubEvent;
import com.xsx.samer.model.FaceText;
import com.xsx.samer.model.Post;
import com.xsx.samer.model.Reply;
import com.xsx.samer.model.User;
import com.xsx.samer.utils.CollectionUtil;
import com.xsx.samer.utils.FaceTextUtil;
import com.xsx.samer.utils.ImageLoadOptions;
import com.xsx.samer.utils.TimeUtil;
import com.xsx.samer.widget.EmoticonsEditText;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by XSX on 2015/10/25.
 */
public class ClubEventActivity extends BaseActivity implements View.OnClickListener{

    private ClubEvent clubEvent;
    private Button btn_chat_send;
    private EmoticonsEditText et_comment;
    private TextView tv_reply_num;
    private Button btn_emo;

    private ViewPager pager_emo;
    private List<FaceText> emos;

    private LinearLayout layout_emo;
    private Toolbar toolbar;

    private List<Reply> list;

    private ReplyAdapter replyAdapter;

    private RecyclerView mRecyclerView;

    private TextView tv_event_name;
    private TextView tv_event_time;
    private TextView tv_event_place;
    private TextView tv_event_person;
    private TextView tv_event_desc;
    private TextView tv_event_organizer;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_club_event);
        initRecyclerView();
        initViews();
        initDatas();
    }


    private void initViews() {
        initEmoView();
        clubEvent = (ClubEvent) getIntent().getSerializableExtra(
                "clubEvent");

        tv_event_name= (TextView) findViewById(R.id.tv_event_name);
        tv_event_time= (TextView) findViewById(R.id.tv_event_time);
        tv_event_place= (TextView) findViewById(R.id.tv_event_place);
        tv_event_person= (TextView) findViewById(R.id.tv_event_person);
        tv_event_desc= (TextView) findViewById(R.id.tv_event_desc);
        tv_event_organizer= (TextView) findViewById(R.id.tv_event_organizer);

        tv_event_name.setText(""+clubEvent.getEventName());
        tv_event_time.setText(""+clubEvent.getEventTime());
        tv_event_place.setText(""+clubEvent.getEventPlace());
        tv_event_person.setText(""+clubEvent.getEventPerson());
        tv_event_desc.setText(""+clubEvent.getEventDesc());
        tv_event_organizer.setText(""+clubEvent.getEventOrganizer());

        // 回复栏--------------------
        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(this);
        et_comment = (EmoticonsEditText) findViewById(R.id.et_comment);
        tv_reply_num = (TextView) findViewById(R.id.tv_reply_num);
        btn_emo = (Button) findViewById(R.id.btn_emo);
        btn_emo.setOnClickListener(this);
        layout_emo = (LinearLayout) findViewById(R.id.layout_emo);

    }
    ProgressDialog progress;
    boolean isUpdate = false;

    private void initDatas() {
        progress = new ProgressDialog(this);
        progress.setMessage("正在加载回复信息...");
        progress.setCanceledOnTouchOutside(true);
        progress.show();
        // 查询出某个帖子的所有评论,同时将该评论的作者的信息也查询出来
        BmobQuery<Reply> query = new BmobQuery<Reply>();
        query.addWhereEqualTo("clubEvent", clubEvent);
        query.include("author");
        query.order("-createdAt");
        query.findObjects(this, new FindListener<Reply>() {

            @Override
            public void onSuccess(List<Reply> objects) {
                tv_reply_num.setText("评论" + objects.size() + "条");
                if (CollectionUtil.isNotNull(objects)) {
                    if (isUpdate) {
                        list.clear();
                    }
                    tv_reply_num.setText("评论" + objects.size() + "条");
                    replyAdapter.addAll(objects);
                    mRecyclerView.setAdapter(replyAdapter);
                } else {
                    ShowToast("暂无回复信息");
                    if (list != null) {
                        list.clear();
                    }
                }
                if (!isUpdate) {
                    progress.dismiss();
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                ShowToast("查找回复失败");
                progress.dismiss();
            }
        });

    }


    private void initRecyclerView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("回复");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        list = new ArrayList<Reply>();
        replyAdapter = new ReplyAdapter(list, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(replyAdapter);
    }



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
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.getText().toString();
                try {
                    if (et_comment != null && !TextUtils.isEmpty(key)) {
                        int start = et_comment.getSelectionStart();
                        CharSequence content = et_comment.getText().insert(
                                start, key);
                        et_comment.setText(content);
                        // 定位光标位置
                        CharSequence info = et_comment.getText();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_chat_send:
                // 回复主贴（楼主）
                sendReply(position);
                break;
            case R.id.btn_emo:
                if (layout_emo.getVisibility() == View.GONE) {
                    showEditState(true);
                } else {
                    showEditState(false);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 用来接受点击到的是哪个位置的position
     */
    private int position = 0;
    /**
     * 回复
     *
     * @param position position为0则为回复楼主 其他则为回复相应楼层
     */
    private void sendReply(int position) {
        // 添加一对多关联
        String content = et_comment.getText().toString();
        if ("".equals(content) || "".equals(content.trim())) {
            ShowToast("回复内容不能为空");
            return;
        }
        // 生成一个回复
        User user = userManager.getCurrentUser(User.class);
        Reply reply = new Reply();

        // 是哪个帖子的回复
        reply.setClubEvent(clubEvent);
        // 是哪个user的回复
        reply.setAuthor(user);
        reply.setAvator(user.getAvatar());
        reply.setContent(content);
        // 判断是回复主贴还是回复其他的人,position==0代表楼主
        if (position == 0) {
            //reply.setReplyTo(post.getAuthor());
        } else {
            Reply reply1 = list.get(position - 1);
            // 得到要回复的人说的话
            reply.setReplyContent("回复 @ " + (list.size() - position + 1)
                    + "楼  : \n\t" + reply1.getContent());
            reply.setReplyTo(reply1.getAuthor());
        }
        reply.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                ShowToast("回复成功");
                //更新UI
                et_comment.setText("");
                queryMore();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast("回复失败:" + arg1);
            }
        });
    }

    protected void queryMore() {
        BmobQuery<Reply> query = new BmobQuery<Reply>();
        query.addWhereEqualTo("clubEvent", clubEvent);
        query.include("author");
        // 返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        query.order("-createdAt");
        query.findObjects(this, new FindListener<Reply>() {

            @Override
            public void onSuccess(List<Reply> arg0) {
                if (CollectionUtil.isNotNull(arg0)) {
                    replyAdapter.removeAll();
                    replyAdapter.addAll(arg0);
                    tv_reply_num.setText("评论" + arg0.size() + "条");
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
            }
        });

    }

    private void showEditState(boolean isEmo) {
        et_comment.setVisibility(View.VISIBLE);
        et_comment.requestFocus();
        // 需要让表情面板获取到焦点
        layout_emo.requestFocus();
        if (isEmo) {
            layout_emo.setVisibility(View.VISIBLE);
            hideSoftInputView();
        } else {
            layout_emo.setVisibility(View.GONE);
            showSoftInputView();
        }
    }

    // 显示软键盘
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(et_comment, 0);
        }
    }
}
