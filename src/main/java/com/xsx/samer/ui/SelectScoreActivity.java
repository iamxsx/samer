package com.xsx.samer.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xsx.samer.R;
import com.xsx.samer.fragment.BaseFragment;

/**
 * @author XSX
 *
 */
public class SelectScoreActivity extends BaseActivity implements OnKeyListener {
    public static final String SELECT_URL = "http://www.iweizhijia.com/mobile/school/function/index/id/3/fromUserName/HxFVFwkJZRUHES0FCwATDEFfdyxjMkAPRwhRS1NFIggoCnRcK1ZFUywmEBERR0w%3D/token/CwEBAEUGW2oGUFECVFZRXgIFVVlOJm8GClcCVA--/_/1443701300";
    private WebView wv;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fragment_select_score);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("查成绩");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        wv = (WebView) findViewById(R.id.wv);
        // 设置WebView属性，能够执行Javascript脚本
        wv.getSettings().setJavaScriptEnabled(true);
        // 加载需要显示的网页
        wv.loadUrl(SELECT_URL);
        // 设置Web视图
        wv.setWebViewClient(new HelloWebViewClient());
    }

    // Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
            wv.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return false;
    }

}
