package com.xsx.samer.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonReclclerViewAdapter;
import com.xsx.samer.adapter.PostAdapter;
import com.xsx.samer.model.Post;
import com.xsx.samer.ui.AddPostActivity;
import com.xsx.samer.ui.ReplyActivity;
import com.xsx.samer.utils.CollectionUtil;
import com.xsx.samer.utils.PixelUtil;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 主界面随便聊模块
 * Created by XSX on 2015/10/11.
 */
public class PostFragment extends BaseFragment{

    private static final String TAG = "PostFragment";
    /**
     * 当从添加界面返回时刷新自身UI
     */
    private static final int REFRESH_VIEW =10 ;
    private List<Post> list;
    private PostAdapter mPostAdapter;
    private RecyclerView mRecyclerView;
    /**
     * 下拉刷新控件
     */
    //private SwipeRefreshLayout mSwipeRefreshLayout;
    private PullToRefreshView mPullToRefreshView;
    private FloatingActionButton fab;


    /**
     * 用来缓存fragment，不让每次切换是都刷新
     */
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initDatas();
    }

    private void initViews() {
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setRefreshing(true);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobQuery<Post> query = new BmobQuery<Post>();
                query.addWhereEqualTo("main", true);
                query.count(getActivity(), Post.class, new CountListener() {
                    @Override
                    public void onSuccess(int arg0) {
                        if (arg0 > list.size()) {
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

        list = new ArrayList<Post>();
        mPostAdapter=new PostAdapter(getActivity(),list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setAdapter(mPostAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mPostAdapter.setOnItemClickLitener(new CommonReclclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Post post = list.get(position);
                Intent intent = new Intent(getActivity(), ReplyActivity.class);
                intent.putExtra("post", post);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), AddPostActivity.class);
                //发布在主界面的帖子
                intent.putExtra("isMain",true);
                startActivityForResult(intent, REFRESH_VIEW);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==200){
            //刷新界面
            refreshView();
        }
    }


    private void refreshView() {
        Log.i(TAG,"refreshView");
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.count(getActivity(), Post.class, new CountListener() {
            @Override
            public void onSuccess(int arg0) {
                if (arg0 > list.size()) {
                    queryMore();
                }
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast(arg1);
            }
        });
    }

    private void initDatas() {
        Log.i(TAG, "initDatas");
        BmobQuery<Post> query = new BmobQuery<Post>();
        // 返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        query.order("-createdAt");
        //把兼职贴的作者也查出来
        query.include("author");
        query.addWhereEqualTo("main", true);
        // 执行查询方法
        query.findObjects(getActivity(), new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> object) {
                if (CollectionUtil.isNotNull(object)) {
                    mPostAdapter.removeAll();
                    mPostAdapter.addAll(object);
                    mRecyclerView.setAdapter(mPostAdapter);

                } else {
                    ShowToast("暂无兼职信息");
                    if (list != null) {
                        list.clear();
                    }
                }
                mPullToRefreshView.setRefreshing(false);
            }

            @Override
            public void onError(int code, String msg) {
                if (list != null) {
                    list.clear();
                }
                ShowToast("数据不存在");
                mPullToRefreshView.setRefreshing(false);
            }
        });
    }





    protected void queryMore() {
        Log.i(TAG, "queryMore");
        BmobQuery<Post> query = new BmobQuery<Post>();
        // 返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        query.order("-createdAt");
        query.addWhereEqualTo("main", true);
        query.include("author");
        query.findObjects(getActivity(), new FindListener<Post>() {

            @Override
            public void onSuccess(List<Post> arg0) {
                if (CollectionUtil.isNotNull(arg0)) {
                    mPostAdapter.removeAll();
                    mPostAdapter.addAll(arg0);
                    mRecyclerView.setAdapter(mPostAdapter);
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                ShowToast("搜索更多用户出错:" + arg1);
            }
        });

    }



}
