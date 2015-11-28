package com.xsx.samer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.xsx.samer.R;
import com.xsx.samer.fragment.ClubEventFragment;
import com.xsx.samer.fragment.ClubInnerEventFragment;
import com.xsx.samer.fragment.ClubMumberListFragment;
import com.xsx.samer.model.Club;
import com.xsx.samer.model.User;
import com.xsx.samer.utils.ImageLoadOptions;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by XSX on 2015/10/22.
 */
public class ClubActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG ="ClubActivity" ;
    private ImageView backdrop;

    private Club club;

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private Toolbar toolbar;
    private User currentUser;
    public List<User> mumbers;

    private int number;
    //private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_club);
        club = (Club) getIntent().getSerializableExtra("club");
        currentUser = userManager.getCurrentUser(User.class);
        queryClubMumbers();
        initViews();
    }

    private void initViews() {
        initToolbar();
        String clubName = club.getClubName();
        String bgUrl = club.getBgUrl();

        toolbar.setTitle(clubName);
        if (bgUrl != null) {
            ImageLoader.getInstance().displayImage(bgUrl, backdrop, ImageLoadOptions.getOptions());
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ClubMumberListFragment(), "社团成员");
        adapter.addFragment(new ClubEventFragment(), "校内活动");
        adapter.addFragment(new ClubInnerEventFragment(), "内部活动");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        //fab= (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(this);

        initFloatActionButton();


    }

    public void queryClubMumbers(){
        mumbers=new ArrayList<>();
        BmobQuery<User> query = new BmobQuery<>();
        //只查询会员列表
        query.addWhereRelatedTo("mumbers",new BmobPointer(club));
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                mumbers = list;
                number = mumbers.size();
                Log.i(TAG, "社员的人数为:" + number);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 初始化按钮
     */
    private void initFloatActionButton() {
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.club_more);
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        //按钮1
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageResource(R.drawable.add_event);
        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();
        //按钮2
        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageResource(R.drawable.add_club);
        SubActionButton button2 = itemBuilder.setContentView(itemIcon2).build();

        //按钮3
        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageResource(R.drawable.exit_club);
        SubActionButton button3 = itemBuilder.setContentView(itemIcon3).build();

        //按钮4
        ImageView itemIcon4 = new ImageView(this);
        itemIcon4.setImageResource(R.drawable.add_inner_event);
        SubActionButton button4 = itemBuilder.setContentView(itemIcon4).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .addSubActionView(button4)
                .attachTo(actionButton)
                .build();





        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClubEvent();
            }
        });
        //加入社团
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinClub();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitClub();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClubInnerEvent();
            }
        });
    }

    /**
     * 添加社团内部事务
     */
    private void addClubInnerEvent() {
        User clubManager=club.getClubManager();
        if(clubManager==null || !clubManager.getObjectId().equals(userManager.getCurrentUser(User.class).getObjectId())){
            ShowToast("你不是社团管理员，无法发布活动，申请管理员请联系Boss肖~~~");
            return;
        }
        Intent intent=new Intent(ClubActivity.this,AddClubInnerEventActivity.class);
        intent.putExtra("club", club);
        startActivity(intent);
    }

    /**
     * 发布活动
     */
    private void addClubEvent() {
        User clubManager=club.getClubManager();
        if(clubManager==null || !clubManager.getObjectId().equals(userManager.getCurrentUser(User.class).getObjectId())){
            ShowToast("你不是社团管理员，无法发布活动，申请管理员请联系Boss肖~~~");
            return;
        }
        Intent intent=new Intent(ClubActivity.this,AddClubEventActivity.class);
        intent.putExtra("club", club);
        startActivity(intent);
    }

    private void exitClub() {
        mumbers=new ArrayList<>();
        BmobQuery<User> query = new BmobQuery<>();
        //只查询会员列表
        query.addWhereRelatedTo("mumbers",new BmobPointer(club));
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                mumbers = list;
                number = mumbers.size();
                Log.i(TAG, "社员的人数为:" + number);
                //先判断当前用户是否已加入该社团
                //通过判断id来判断是否已经加入了社团
                String currentId=currentUser.getObjectId();
                List<String> ids = new ArrayList<String>();
                for (User user :mumbers){
                    ids.add(user.getObjectId());
                }
                if(!ids.contains(currentId)){
                    //已加入该社团
                    ShowToast("你尚未加入该社团~~~");
                    return;
                }
                BmobRelation relation=new BmobRelation();
                relation.remove(userManager.getCurrentUser(User.class));
                club.setMumbers(relation);
                club.increment("numbers",-1);
                club.update(ClubActivity.this, club.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        ShowToast("退出社团成功");
                        //取消消息推送
                        BmobInstallation installation = BmobInstallation.getCurrentInstallation(ClubActivity.this);
                        installation.unsubscribe(club.getClubName());
                        installation.save();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        ShowToast(s);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    /**
     * 加入社团
     */
    public void joinClub(){
        mumbers=new ArrayList<>();
        //先查询出所有社团的人员，必须得等查询到之后才判断
        BmobQuery<User> query = new BmobQuery<>();
        //只查询会员列表
        query.addWhereRelatedTo("mumbers",new BmobPointer(club));
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                mumbers = list;
                number = mumbers.size();
                Log.i(TAG, "社员的人数为:" + number);
                //先判断当前用户是否已加入该社团
                //通过判断id来判断是否已经加入了社团
                String currentId=currentUser.getObjectId();
                List<String> ids = new ArrayList<String>();
                for (User user :mumbers){
                    Log.i(TAG,"user.getObjectId()="+user.getObjectId());
                    ids.add(user.getObjectId());
                }
                if(ids.contains(currentId)){
                    //已加入该社团
                    ShowToast("你已经是该社团成员咯~~~");
                    return;
                }
                BmobRelation relation=new BmobRelation();
                relation.add(userManager.getCurrentUser(User.class));
                club.setMumbers(relation);
                club.increment("numbers");
                club.update(ClubActivity.this, club.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        ShowToast("加入社团成功");
                        BmobInstallation installation = BmobInstallation.getCurrentInstallation(ClubActivity.this);
                        //当前设备订阅了该社团的消息推送
                        installation.subscribe(club.getClubName());
                        installation.save();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        ShowToast(s);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:

                break;
        }
    }

    /**
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
}
