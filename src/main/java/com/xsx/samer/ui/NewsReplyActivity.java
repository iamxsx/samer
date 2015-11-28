package com.xsx.samer.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.model.News;

public class NewsReplyActivity extends BaseActivity{
    private static final String TAG = "NewsReplyActivity";
    private ImageView iv_avator;
    private TextView tv_time;
    private TextView tv_content;
    private TextView tv_title;
    private TextView tv_author;
    private News news;

    /**
     * 新闻链接
     */
    private String href;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_news_reply);
        initViews();
    }



    private void initViews() {
        news=(News) getIntent().getSerializableExtra("post");
        iv_avator=(ImageView) findViewById(R.id.iv_avator);
        tv_time=(TextView) findViewById(R.id.tv_time);
        tv_content=(TextView) findViewById(R.id.tv_content);
        tv_title=(TextView) findViewById(R.id.tv_title);
        tv_author=(TextView) findViewById(R.id.tv_author);
        iv_avator.setImageResource(R.drawable.school);
        tv_author.setText("嘉应学院");
        tv_time.setText(news.getDate());
        href=news.getHref();
        tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());
        new MyAsyncTask().execute();
    }



    class MyAsyncTask extends AsyncTask<Integer, Integer, HashMap<String,String>>{

        @Override
        protected HashMap<String,String> doInBackground(Integer... params) {
            HashMap<String,String> map=new HashMap<String,String>();
            StringBuilder sb = new StringBuilder();
            String content = null;
            String title=null;
            try {
                Document doc= Jsoup.connect(href).get();
                title=doc.getElementsByClass("biaoti").first().text();
                Element tbody=doc.getElementsByTag("tbody").get(5);
                Elements els=tbody.getAllElements();
                for(int i=0;i<els.size(); i++){
                    if(i>2){
                        sb.append(els.get(i).text()+"\n");
                    }
                }
                content=sb.toString();
                map.put("content", content);
                map.put("title", title);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> result) {
            super.onPostExecute(result);
            String content=result.get("content");
            String title=result.get("title");
            tv_content.setText(content);
            tv_title.setText(title);
        }

    }


}
