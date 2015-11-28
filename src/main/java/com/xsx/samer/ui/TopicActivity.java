package com.xsx.samer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonReclclerViewAdapter;
import com.xsx.samer.adapter.TopicAdapter;
import com.xsx.samer.fragment.BaseFragment;
import com.xsx.samer.model.Topic;
import com.xsx.samer.utils.CollectionUtil;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by XSX on 2015/10/12.
 */
public class TopicActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "TopicFragment";
    private RecyclerView mRecyclerView;
    private TopicAdapter mTopicAdapter;
    private List<Topic> topics;
    private FloatingActionButton fab;
    /**
     * 下拉刷新控件
     */
    //private SwipeRefreshLayout mSwipeRefreshLayout;
    private View rootView;

    private PullToRefreshView mPullToRefreshView;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_topic);
        initViews();
        initDatas();
    }



    private void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("热门话题");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setRefreshing(true);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG,"onRefresh");
                BmobQuery<Topic> query = new BmobQuery<Topic>();
                query.count(TopicActivity.this, Topic.class, new CountListener() {
                    @Override
                    public void onSuccess(int arg0) {
                        if (arg0 > topics.size()) {
                            queryMore();
                        }
                        mPullToRefreshView.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        ShowToast("数据加载失败");
                        mPullToRefreshView.setRefreshing(false);
                    }
                });
            }
        });

        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerview);
        fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        topics=new ArrayList<Topic>();
        mTopicAdapter=new TopicAdapter(this,topics);
        mTopicAdapter.setOnItemClickLitener(new CommonReclclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Topic topic=topics.get(position);
                Intent intent = new Intent(TopicActivity.this,PostActivity.class);
                intent.putExtra("title",topic.getTitle());
                intent.putExtra("titleImg",topic.getTitleImg());
                intent.putExtra("topic",topic);
                startActivity(intent);
                ShowToast("onItemClick" + position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                ShowToast("onItemLongClick"+position);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setAdapter(mTopicAdapter);
    }


    private void initDatas() {
        BmobQuery<Topic> query = new BmobQuery<Topic>();
        // 返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        query.order("-createdAt");
        //把兼职贴的作者也查出来
        query.include("author");
        // 执行查询方法
        query.findObjects(this, new FindListener<Topic>() {
            @Override
            public void onSuccess(List<Topic> object) {
                if (CollectionUtil.isNotNull(object)) {
                    mTopicAdapter.addAll(object);
                    mRecyclerView.setAdapter(mTopicAdapter);

                } else {
                    ShowToast("暂无话题信息");
                    if (topics != null) {
                        topics.clear();
                    }
                }
                //mSwipeRefreshLayout.setRefreshing(false);
                mPullToRefreshView.setRefreshing(false);
            }

            @Override
            public void onError(int code, String msg) {
                if (topics != null) {
                    topics.clear();
                }
                ShowToast("数据不存在");
                //mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }





    protected void queryMore() {
        Log.i(TAG, "queryMore");
        BmobQuery<Topic> query = new BmobQuery<Topic>();
        // 返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        query.order("-createdAt");
        query.include("author");
        query.findObjects(this, new FindListener<Topic>() {

            @Override
            public void onSuccess(List<Topic> arg0) {
                if (CollectionUtil.isNotNull(arg0)) {
                    mTopicAdapter.removeAll();
                    mTopicAdapter.addAll(arg0);
                    mRecyclerView.setAdapter(mTopicAdapter);
                    //mSwipeRefreshLayout.setRefreshing(false);
                    mPullToRefreshView.setRefreshing(false);
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                //mSwipeRefreshLayout.setRefreshing(false);
                mPullToRefreshView.setRefreshing(false);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                startAnimActivity(AddTopicActivity.class);
                break;

            default:
                break;
        }
    }


}
