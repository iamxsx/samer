package com.xsx.samer.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.CustomApplication;
import com.xsx.samer.MyMessageReceiver;
import com.xsx.samer.R;
import com.xsx.samer.fragment.ClubEventFragment;
import com.xsx.samer.fragment.ContactFragment;
import com.xsx.samer.fragment.HomeClubEventFragment;
import com.xsx.samer.fragment.PostFragment;
import com.xsx.samer.fragment.RecentFragment;
import com.xsx.samer.fragment.RecentFragment2;
import com.xsx.samer.fragment.TopicFragment;
import com.xsx.samer.model.User;
import com.xsx.samer.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.UpdateListener;


public class MainActivity extends BaseActivity implements View.OnClickListener, EventListener {
    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private ViewPager mViewPager;
    private CircleImageView avatar;
    private TextView nick;
    private LinearLayout drawer_bg;
    private User user;
    /**
     * 滑动导航条
     */
    private TabLayout mTabLayout;
    private FloatingActionButton mFloatingActionButton;

    /**
     * 新消息监听器
     */
    private NewMessageBroadCast newMessageBroadCast;
    /**
     * 添加好友消息监听器
     */
    private TagMessageBroadCast tagMessageBroadCast;

    private TopicFragment topicFragment;
    private PostFragment postFragment;
    private RecentFragment2 recentFragment2;
    private ContactFragment contactFragment;
    /**
     * 首页校园活动
     */
    private HomeClubEventFragment homeClubEventFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
        BmobChat.getInstance(this).startPollService(30);
        initNewMessageBroadCast();
        initTagMessageBroadCast();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //小圆点提示
        if (BmobDB.create(this).hasUnReadMsg()) {
            //iv_recent_tips.setVisibility(View.VISIBLE);
            ShowToast("有未读信息");
        } else {
            //iv_recent_tips.setVisibility(View.GONE);
        }
        if (BmobDB.create(this).hasNewInvite()) {
            //iv_contact_tips.setVisibility(View.VISIBLE);
            ShowToast("有未读请求");
        } else {
            //iv_contact_tips.setVisibility(View.GONE);
        }
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        //清空
        MyMessageReceiver.mNewNum = 0;

    }

    @Override
    protected void onPause() {
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
    }


    /**
     * 新消息接收器
     *
     * @author XSX
     */
    public class NewMessageBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 刷新最近会话界面
            refreshNewMsg(null);
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }

    private void refreshNewMsg(BmobMsg message) {
        // 声音提示
        boolean isAllow = CustomApplication.getInstance().getSpUtil()
                .isAllowVoice();
        if (isAllow) {
            CustomApplication.getInstance().getMediaPlayer().start();
        }
        //iv_recent_tips.setVisibility(View.VISIBLE);
        // 也要存储起来
        if (message != null) {
            BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(
                    true, message);
        }
        int currentIndex = mTabLayout.getSelectedTabPosition();
        if (currentIndex == 2) {
            // 当前页面如果为会话页面，刷新此页面
            if (recentFragment2 != null) {
                recentFragment2.refresh();
            }
        }
    }

    /**
     * 好友添加消息接收器
     *
     * @author XSX
     */
    public class TagMessageBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "有好友添加");
            // 得到好友邀请对象
            BmobInvitation invitation = (BmobInvitation) intent
                    .getSerializableExtra("invite");
            // 刷新好友邀请界面
            refreshInvite(invitation);
            // 记得把广播给终结掉
            abortBroadcast();
        }

    }

    /**
     * 监听新消息事件
     */
    private void initNewMessageBroadCast() {
        newMessageBroadCast = new NewMessageBroadCast();
        // 为广播设置过滤器
        IntentFilter filter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        // 设置优先级
        filter.setPriority(3);
        registerReceiver(newMessageBroadCast, filter);
    }

    /**
     * 监听好友添加事件
     */
    private void initTagMessageBroadCast() {
        tagMessageBroadCast = new TagMessageBroadCast();
        // 为广播设置过滤器
        IntentFilter filter = new IntentFilter(
                BmobConfig.BROADCAST_ADD_USER_MESSAGE);
        // 设置优先级
        filter.setPriority(3);
        registerReceiver(newMessageBroadCast, filter);
    }

    /**
     * 刷新好友邀请界面
     *
     * @param invitation
     */
    private void refreshInvite(BmobInvitation invitation) {
        boolean isAllow = CustomApplication.getInstance().getSpUtil()
                .isAllowVoice();
        if (isAllow) {
            CustomApplication.getInstance().getMediaPlayer().start();
        }
        int currentIndex = mTabLayout.getSelectedTabPosition();
        //iv_contact_tips.setVisibility(View.VISIBLE);
        if (currentIndex == 4) {
            // 当前页面如果为联系人页面，刷新此页面
            if (contactFragment != null) {
                contactFragment.refresh();
            }
        } else {
            // 同时提醒通知
            String tickerText = invitation.getFromname() + "请求添加好友";
            boolean isAllowVibrate =
                    CustomApplication.getInstance().getSpUtil().isAllowVibrate();
            BmobNotifyManager.getInstance(this).showNotify(isAllow, isAllowVibrate,
                    R.drawable.samer, tickerText,
                    invitation.getFromname(), tickerText.toString(),
                    NewFriendActivity.class);
        }
    }

    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //使用Toolbar
        setSupportActionBar(mToolbar);
        //得到toolbar设置相应的属性
        ActionBar ab = getSupportActionBar();
        //设置左边的图片
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        //设置图标是否显示
        ab.setDisplayHomeAsUpEnabled(true);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        //使用NavigationView作为抽屉
        setupDrawerContent(mNavigationView);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        //与viewpager相关联
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorHeight(5);
        mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
        avatar = (CircleImageView) findViewById(R.id.avatar);
        user = userManager.getCurrentUser(User.class);
        if (user.getAvatar() != null) {
            ImageLoader.getInstance().displayImage(user.getAvatar(), avatar);
        }else{
            if(user.getSex().equals("男")){
                avatar.setImageResource(R.mipmap.male_default_icon);
            }else{
                avatar.setImageResource(R.mipmap.female_default_icon);

            }
        }
        nick = (TextView) findViewById(R.id.nick);
        nick.setText(user.getNick() != null ? user.getNick() : "设置昵称");
        nick.setOnClickListener(this);
        drawer_bg = (LinearLayout) findViewById(R.id.drawer_bg);
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(user.getBgUrl());
        if (bitmap != null) {
            drawer_bg.setBackground(new BitmapDrawable(bitmap));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nick:
                showUpdateNickDialog();
                break;
        }
    }

    /**
     * 修改昵称的dialog
     */
    private void showUpdateNickDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.edittext_dialog, null);//这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.et);//获得输入框对象
        new AlertDialog.Builder(this)
                .setTitle("修改昵称")//提示框标题
                .setView(view)
                .setPositiveButton("确定",//提示框的两个按钮
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (edit.getText() == null) {
                                    ShowToast("昵称不能为空");
                                    return;
                                }
                                user.setNick(edit.getText() + "");
                                user.update(MainActivity.this, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        // TODO Auto-generated method stub
                                        ShowToast("修改成功");
                                        nick.setText(user.getNick());
                                    }

                                    @Override
                                    public void onFailure(int arg0, String arg1) {
                                        ShowToast("onFailure:" + arg1);
                                    }
                                });
                            }
                        }).setNegativeButton("取消", null).create().show();
    }


    /**
     * ViewPager的适配器
     */
    class MyPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments = new ArrayList<Fragment>();
        List<String> fragmentTitles = new ArrayList<String>();


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }


    }

    /**
     * 设置ViewPager的内容
     *
     * @param mViewPager
     */
    private void setupViewPager(ViewPager mViewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        homeClubEventFragment = new HomeClubEventFragment();
        topicFragment = new TopicFragment();
        postFragment = new PostFragment();
        recentFragment2 = new RecentFragment2();
        contactFragment = new ContactFragment();

        //adapter.addFragment(homeClubEventFragment, "校内活动");
        //adapter.addFragment(topicFragment, "话题");
        adapter.addFragment(postFragment, "嘿，我说");
        adapter.addFragment(recentFragment2, "消息");
        adapter.addFragment(contactFragment, "联系人");

        mViewPager.setAdapter(adapter);
        //控制viewpager可以显示的页面数，阻止fragment刷新
        mViewPager.setOffscreenPageLimit(2);

    }

    /**
     * 设置抽屉的响应
     *
     * @param mNavigationView
     */
    private void setupDrawerContent(NavigationView mNavigationView) {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.logout:
                        CustomApplication.getInstance().logout();
                        finish();
                        startAnimActivity(LoginActivity.class);
                        break;
                    case R.id.setting:
                        startAnimActivity(SettingsActivity.class);
                        break;
                    case R.id.black_list:
                        startAnimActivity(BlackListActivity.class);
                        break;
                    case R.id.search_score:
                        startAnimActivity(SelectScoreActivity.class);
                        break;
                    case R.id.hot_news:
                        startAnimActivity(NewsActivity.class);
                        break;
                    case R.id.home:
                        Intent intent = new Intent(MainActivity.this, MyDetailActivity.class);
                        intent.putExtra("from", "me");
                        startActivity(intent);
                        break;
                    case R.id.club:
                        startAnimActivity(ChoseClubActivity.class);
                        break;
                    case R.id.topic:
                        startAnimActivity(TopicActivity.class);
                        break;
                    case R.id.event:
                        startAnimActivity(ClubEventListActivity.class);
                        break;
                }
//                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    /**
     * toolbar右边的控件
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.add_friend:
                startAnimActivity(AddFriendActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
         * 好友请求
         */
    @Override
    public void onAddUser(BmobInvitation bmobInvitation) {
        refreshInvite(bmobInvitation);
    }

    /*
     * 接收到消息
     */
    @Override
    public void onMessage(BmobMsg bmobMsg) {
        refreshNewMsg(bmobMsg);
    }

    /*
     * 当网络状态改变
     */
    @Override
    public void onNetChange(boolean isNetConnected) {
        if (isNetConnected) {
            ShowToast(R.string.network_tips);
        }
    }

    /*
     * 当下线时
     */
    @Override
    public void onOffline() {
        // TODO Auto-generated method stub
    }

    /*
     * 已读回执
     */
    @Override
    public void onReaded(String arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    private static long firstTime;


    //对话框进入，退出时的动画
    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;

    /*
     * 当按下返回键时
     */
    @Override
    public void onBackPressed() {
//        // 两次按键时间相差2秒，不退出
//        if (firstTime != 0 && firstTime + 2000 > System.currentTimeMillis()) {
//            super.onBackPressed();
//        } else {
//            ShowToast("再按一次退出程序");
//        }
//        // 记录下第一次的时间
//        firstTime = System.currentTimeMillis();
        bas_in = new BounceTopEnter();
        bas_out = new SlideBottomExit();
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("亲,真的要走吗?再看会儿吧~(●—●)")//
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(23)//
                .btnText("继续逛逛", "残忍退出")//
                .btnTextColor(Color.parseColor("#383838"), Color.parseColor("#D4D4D4"))//
                .btnTextSize(16f, 16f)//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                        finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 终止广播
        try {
            unregisterReceiver(newMessageBroadCast);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(tagMessageBroadCast);
        } catch (Exception e) {
        }
        // 取消定时检测服务
        BmobChat.getInstance(this).stopPollService();
    }
}
