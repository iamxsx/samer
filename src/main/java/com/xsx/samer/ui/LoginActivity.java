package com.xsx.samer.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xsx.samer.R;
import com.xsx.samer.model.User;
import com.xsx.samer.utils.CommonUtil;
import com.xsx.samer.widget.CircleImageView;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by XSX on 2015/10/11.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private TextView btn_register;
    private EditText et_username,et_password;
    private Button btn_login;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void initView(){
        et_username=(EditText) findViewById(R.id.et_username);
        et_password=(EditText) findViewById(R.id.et_password);
        btn_login=(Button) findViewById(R.id.btn_login);
        btn_register=(TextView) findViewById(R.id.btn_register);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    public void regist(){
        startAnimActivity(RegisterActivity1.class);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                regist();
                break;
        }

    }

    public void login() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ShowToast(R.string.toast_error_username_null);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ShowToast(R.string.toast_error_password_null);
            return;
        }
        // 连接网络
        boolean isConnectAvailable = CommonUtil.isNetworkAvailable(this);
        if (!isConnectAvailable) {
            ShowToast(R.string.network_tips);
            return;
        }
        // 连接网络时显示一个进度条，界面友好
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("正在登陆...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        // 访问bmob数据库
        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        //通过用户名和密码登陆
        user.login(this, new SaveListener() {
            @Override
            public void onSuccess() {
                ShowToast("登陆成功");
                startAnimActivity(MainActivity.class);
                progress.dismiss();
                finish();
            }
            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast("登陆失败");
                progress.dismiss();
            }
        });
    }
}
