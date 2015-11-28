package com.xsx.samer.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.xsx.samer.CustomApplication;
import com.xsx.samer.R;
import com.xsx.samer.utils.CollectionUtil;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by XSX on 2015/10/11.
 */
public class BaseActivity extends AppCompatActivity{
    /**
     * 操作用户的类
     */
    protected BmobUserManager userManager;
    protected BmobChatManager chatManager;
    protected CustomApplication application;
    /**
     * 屏幕宽
     */
    protected int mScreenWidth;
    /**
     * 屏幕高
     */
    protected int mScreenHeight;

    protected Toolbar toolbar;

    public void startAnimActivity(Class clazz){
        startActivity(new Intent(this,clazz));
    }

    public void ShowToast(int errorId) {
        Toast.makeText(this, errorId, Toast.LENGTH_SHORT).show();
    }

    public void ShowToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        application=CustomApplication.getInstance();
        userManager=BmobUserManager.getInstance(this);
        chatManager=BmobChatManager.getInstance(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
    }



    /**
     * 更新用户信息，包括位置信息，好友信息
     */
    public void updateUserInfo(){
        //查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
        //这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
        userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

            @Override
            public void onSuccess(List<BmobChatUser> arg0) {
                // 保存到application内存中方便比较，采用键值对方便读取
                CustomApplication.getInstance().setContactList(CollectionUtil.list2map(arg0));
            }

            @Override
            public void onError(int arg0, String arg1) {
                if(arg0== BmobConfig.CODE_COMMON_NONE){
                    Toast.makeText(BaseActivity.this,arg1,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BaseActivity.this, "查询好友列表失败："+arg1, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





    public void hideSoftInputView(){
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void initToolbar(){
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
