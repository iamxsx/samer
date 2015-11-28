package com.xsx.samer.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xsx.samer.R;
import com.xsx.samer.model.MyBmobInstallation;

import java.util.List;

import cn.bmob.im.BmobChat;
import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by XSX on 2015/10/11.
 */
public class SplashActivity extends BaseActivity{

    private static final String AppId="af3d0eadca80e0dc57c6f85572b77673";

    private static final int GO_HOME = 100;
    private static final int GO_LOGIN = 200;
    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    startAnimActivity(MainActivity.class);
                    finish();
                    break;
                case GO_LOGIN:
                    startAnimActivity(LoginActivity.class);
                    finish();
                    break;
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //可设置调试模式，当为true的时候，会在logcat的BmobChat下输出一些日志，包括推送服务是否正常运行，如果服务端返回错误，也会一并打印出来。方便开发者调试，正式发布应注释此句。
        //BmobIM SDK初始化--只需要这一段代码即可完成初始化
        BmobChat.getInstance(this).init(AppId);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        //Bmob的短信服务
        BmobSMS.initialize(this,AppId);

        if(userManager.getCurrentUser()!=null){
            //更新用户位置，好友信息等
            this.updateUserInfo();
            mHandler.sendEmptyMessageDelayed(GO_HOME, 500);
        }else{
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, 500);
        }



    }
}
