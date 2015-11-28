package com.xsx.samer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.xsx.samer.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;

/**
 * Created by XSX on 2015/10/27.
 */
public class RegisterActivity1 extends BaseActivity {

    private static final String TAG = "RegisterActivity1";
    private TextInputLayout til_phonenumber;
    private TextInputLayout til_code;

    private Toolbar toolbar;

    private TextView tv_send_code;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what==200){
                tv_send_code.setText("发送验证码");
                tv_send_code.setClickable(true);
                tv_send_code.setSelected(false);
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_regist1);
        initViews();
    }

    private void initViews() {
        til_phonenumber = (TextInputLayout) findViewById(R.id.til_phonenumber);
        til_code = (TextInputLayout) findViewById(R.id.til_code);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("手机验证");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_send_code = (TextView) findViewById(R.id.tv_send_code);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chose, menu);
        return super.onCreateOptionsMenu(menu);
    }

    String phoneNumber;
    String code;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sure) {
            hideKeyboard();
            code = til_code.getEditText().getText().toString();
            if (TextUtils.isEmpty(code)) {
                til_code.setError("验证码不能为空");
                return false;
            }
            BmobSMS.verifySmsCode(RegisterActivity1.this, phoneNumber, code, new VerifySMSCodeListener() {
                @Override
                public void done(BmobException ex) {
                    if (ex == null) {//短信验证码已验证成功
                        Intent intent = new Intent(RegisterActivity1.this, RegisterActivity.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        startActivity(intent);
                        finish();
                    } else {
                        til_code.setError("验证码错误");
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);

    }

    private int i = 60;

    /**
     * 发送验证码
     *
     * @param view
     */
    public void sendCode(View view) {
        ShowToast("sendCode");
        hideKeyboard();
        phoneNumber = til_phonenumber.getEditText().getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            til_phonenumber.setError("手机号不能为空");
            return;
        }
        if (!checkPhoneNumber(phoneNumber)) {
            til_phonenumber.setError("手机号格式错误");
            return;
        }
        til_phonenumber.setError("");

        //定时器，改变发送验证码的状态，60秒只能发一次
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "RUN");
                //停滞1秒
                if (i > 0) {
                    //歇一秒
                    handler.postDelayed(this, 1000);
                    tv_send_code.setText((i--) + "秒后可再次获取验证码");
                } else {
                    handler.sendEmptyMessage(200);
                    //为下一次计时做准备
                    i = 60;
                    return;
                }

            }
        }, 1000);
        BmobSMS.requestSMSCode(this, phoneNumber, "注册模板", new RequestSMSCodeListener() {
            @Override
            public void done(Integer smsId, BmobException ex) {
                if (ex == null) {//验证码发送成功
                    Log.i("bmob", "短信id：" + smsId);//用于查询本次短信发送详情
                    ShowToast("验证码发送成功");
                    tv_send_code.setSelected(true);
                    tv_send_code.setClickable(false);
                }
            }
        });
    }

    private boolean checkPhoneNumber(String phoneNumber) {
        String reg = "^13\\d{9}|14[57]\\d{8}|15[012356789]\\d{8}|18[01256789]\\d{8}|17[0678]\\d{8}";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
