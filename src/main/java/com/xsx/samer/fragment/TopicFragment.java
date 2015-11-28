package com.xsx.samer.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonReclclerViewAdapter;
import com.xsx.samer.adapter.TopicAdapter;
import com.xsx.samer.model.Post;
import com.xsx.samer.model.Topic;
import com.xsx.samer.ui.AddTopicActivity;
import com.xsx.samer.ui.PostActivity;
import com.xsx.samer.utils.CollectionUtil;
import com.xsx.samer.utils.PixelUtil;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by XSX on 2015/10/12.
 */
public class TopicFragment extends BaseFragment implements View.OnClickListener{
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView==null){
            rootView=inflater.inflate(
                    R.layout.fragment_list2, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent，如何有parent需要从parent中删除，
        //要不然就会发生这个rootview已经有parent的错误
        ViewGroup parent= (ViewGroup) rootView.getParent();
        if (parent!=null){
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initDatas();
    }

    private void initViews() {
//        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
//        mSwipeRefreshLayout.setColorScheme(R.color.color1, R.color.color2,
//                R.color.color3, R.color.color4);
//        mSwipeRefreshLayout.setOnRefreshListener(this);
//        // 这句话是为了，第一次进入页面的时候显示加载进度条
//        mSwipeRefreshLayout.setProgressViewOffset(false, 0, PixelUtil.dp2px(24, getActivity()));
//        mSwipeRefreshLayout.setRefreshing(true);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setRefreshing(true);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG,"onRefresh");
                BmobQuery<Topic> query = new BmobQuery<Topic>();
                query.count(getActivity(), Topic.class, new CountListener() {
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
        mTopicAdapter=new TopicAdapter(getActivity(),topics);
        mTopicAdapter.setOnItemClickLitener(new CommonReclclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Topic topic=topics.get(position);
                Intent intent = new Intent(getActivity(),PostActivity.class);
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
        query.findObjects(getActivity(), new FindListener<Topic>() {
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
        query.findObjects(getActivity(), new FindListener<Topic>() {

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
