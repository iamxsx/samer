package com.xsx.samer.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.xsx.samer.R;
import com.xsx.samer.adapter.NewsAdapter;
import com.xsx.samer.model.AdDomain;
import com.xsx.samer.model.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;


public class NewsActivity extends BaseActivity implements OnItemClickListener{
    private static final String TAG = "NewsFragment";
    public static String IMAGE_CACHE_PATH = "imageloader/Cache";
    public static final String NEWS_URL="http://www.jyu.edu.cn/news/index_3.html";

    private int currentItem = 0;

    //RecyclerView
    private RecyclerView recyclerView;

    private List<AdDomain> adList;
    private ListView index_lv;
    private List<News> newss=new ArrayList<News>();


    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_news);
        index_lv=(ListView) findViewById(R.id.index_lv);
        index_lv.setOnItemClickListener(this);
        index_lv.setAdapter(new NewsAdapter(newss, this));
        initViews();
        new MyAsyncTask().execute();
    }

    private void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("校园公告");
        setSupportActionBar(toolbar);
        ActionBar ab=getSupportActionBar();
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    class MyAsyncTask extends AsyncTask<Integer, Integer, List<News>>{


        @Override
        protected List<News> doInBackground(Integer... params) {
            try {
                Document document= Jsoup.connect(NEWS_URL).get();
                Elements elements=document.getElementsByTag("ul");
                //得到了有包含有校园公告信息的ul
                Element element=elements.first();
                Elements liList=element.getElementsByTag("li");
                for (Element e :liList) {
                    //Log.i(TAG, e.html());
                    Element a=e.getElementsByTag("a").first();
                    Element span=e.getElementsByTag("span").first();
                    String title=a.attr("title");
                    String href=a.attr("href");
                    String date=span.html();

                    News news=new News();
                    news.setTitle(title);
                    news.setHref(href);
                    news.setDate(date);
                    newss.add(news);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return newss;
        }

        @Override
        protected void onPostExecute(List<News> result) {
            super.onPostExecute(result);
            index_lv.setAdapter(new NewsAdapter(result,NewsActivity.this));
        }

    }







    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        News news=newss.get(position);
        Intent intent=new Intent(this,NewsReplyActivity.class);
        intent.putExtra("post", (Serializable)news);
        startActivity(intent);
    }



}
