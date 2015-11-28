package com.xsx.samer.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xsx.samer.R;
import com.xsx.samer.model.User;
import com.xsx.samer.utils.CommonUtil;
import com.xsx.samer.widget.CircleImageView;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by XSX on 2015/10/11.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {


    private LinearLayout ll_male,ll_female;

    private Toolbar toolbar;

    private TextInputLayout til_password,til_repassword,til_nick,til_username;

    private CircleImageView iv_avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist2);
        initView();

    }

    public void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("注册");
        toolbar.setSubtitle("填写个人信息");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setSupportActionBar(toolbar);

        ll_male= (LinearLayout) findViewById(R.id.ll_male);
        ll_female= (LinearLayout) findViewById(R.id.ll_female);
        ll_male.setOnClickListener(this);
        ll_female.setOnClickListener(this);

        til_password= (TextInputLayout) findViewById(R.id.til_password);
        til_repassword= (TextInputLayout) findViewById(R.id.til_repassword);
        til_nick= (TextInputLayout) findViewById(R.id.til_nick);
        //til_username= (TextInputLayout) findViewById(R.id.til_username);
        iv_avatar= (CircleImageView) findViewById(R.id.iv_avatar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chose, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.sure){
            regist();
        }
        return super.onOptionsItemSelected(item);
    }

    public void regist() {
        //String username = til_username.getEditText().getText().toString();
        String password = til_password.getEditText().getText().toString();
        String repassword = til_repassword.getEditText().getText().toString();
        String nick=til_nick.getEditText().getText().toString();
//        if (TextUtils.isEmpty(username)) {
//            til_username.setError("用户名不能为空");
//            return;
//        }
//        til_username.setErrorEnabled(false);
        if (TextUtils.isEmpty(password)) {
            til_password.setError("密码不能为空");
            return;
        }
        til_password.setErrorEnabled(false);
        if (!repassword.equals(password)) {
            til_repassword.setError("重复密码错误");
            return;
        }
        til_repassword.setErrorEnabled(false);
        if (TextUtils.isEmpty(nick)) {
            til_nick.setError("昵称不能为空");
            return;
        }
        til_nick.setErrorEnabled(false);
        if (sex==null){
            ShowToast("请选择性别");
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
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        // 访问bmob数据库
        User user = new User();
        //用户名即为手机号
        user.setUsername(getIntent().getStringExtra("phoneNumber"));
        user.setPassword(password);
        user.setNick(nick);
        user.setSex(sex);
        user.setMobilePhoneNumber(getIntent().getStringExtra("phoneNumber"));
        user.setMobilePhoneNumberVerified(true);

        // 将user和设备id进行绑定
        user.setInstallId(BmobInstallation.getInstallationId(this));
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                ShowToast("注册成功");
                startAnimActivity(MainActivity.class);
                finish();
            }
            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast("error:"+arg1);
                progress.dismiss();
            }
        });
    }
    private String sex;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_male:
                iv_avatar.setImageResource(R.mipmap.male_default_icon);
                ll_male.setSelected(true);
                ll_female.setSelected(false);
                sex="男";
                break;
            case R.id.ll_female:
                iv_avatar.setImageResource(R.mipmap.female_default_icon);
                ll_male.setSelected(false);
                ll_female.setSelected(true);
                sex="女";
                break;
        }
    }

}
