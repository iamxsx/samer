package com.xsx.samer.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonReclclerViewAdapter;
import com.xsx.samer.adapter.PostAdapter;
import com.xsx.samer.adapter.PostAdapter2;
import com.xsx.samer.fragment.BaseFragment;
import com.xsx.samer.model.Post;
import com.xsx.samer.model.Topic;
import com.xsx.samer.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 各个topic版块的帖子
 * Created by XSX on 2015/10/11.
 */
public class PostActivity extends BaseActivity{

    private static final String TAG = "PostActivity";
    private List<Post> list;
    private PostAdapter mPostAdapter;
    private PostAdapter2 mPostAdapter2;
    private RecyclerView mRecyclerView;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView backdrop;
    private Topic topic;
    private FloatingActionButton fab;
    private Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initViews();
        initDatas();
    }



    private void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerview);
        list = new ArrayList<Post>();
        mPostAdapter=new PostAdapter(this,list);
        mPostAdapter2=new PostAdapter2(this,list);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(16));
        mRecyclerView.setAdapter(mPostAdapter2);
        Intent intent=getIntent();
        String title=intent.getStringExtra("title");
        String titleImg=intent.getStringExtra("titleImg");
        topic = (Topic) intent.getSerializableExtra("topic");
        Log.i(TAG,"topic="+topic);
        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);
        backdrop= (ImageView) findViewById(R.id.backdrop);
        ImageLoader.getInstance().displayImage(titleImg, backdrop);

        fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostActivity.this, AddPostActivity.class);
                intent.putExtra("topic", topic);
                startActivity(intent);
            }
        });

    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space=space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left=space;
            outRect.right=space;
            outRect.bottom=space;
            if(parent.getChildAdapterPosition(view)==0){
                outRect.top=space;
            }
        }
    }

    ProgressDialog progress;
    //MetaballView mv;
    private void initDatas() {
            progress = new ProgressDialog(this);
            progress.setMessage("正在加载信息...");
            progress.setCanceledOnTouchOutside(true);
            progress.show();
        BmobQuery<Post> query = new BmobQuery<Post>();
        // 返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        query.order("-createdAt");
        //把兼职贴的作者也查出来
        query.include("author");
        //查询出对应topic的帖子
        query.addWhereEqualTo("topic",topic);
        // 执行查询方法
        query.findObjects(this, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> object) {
                if (CollectionUtil.isNotNull(object)) {
                    mPostAdapter2.removeAll();
                    mPostAdapter2.addAll(object);

                } else {
                    ShowToast("暂无信息");
                    if (list != null) {
                        list.clear();
                    }
                }
                progress.dismiss();
            }

            @Override
            public void onError(int code, String msg) {
                if (list != null) {
                    list.clear();
                }
                ShowToast("数据不存在");
            }
        });
    }





    protected void queryMore() {
        Log.i(TAG, "queryMore");
        BmobQuery<Post> query = new BmobQuery<Post>();
        // 返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        query.order("-createdAt");
        query.addWhereEqualTo("topic",topic);
        query.include("author");
        query.findObjects(this, new FindListener<Post>() {

            @Override
            public void onSuccess(List<Post> arg0) {
                if (CollectionUtil.isNotNull(arg0)) {
                    mPostAdapter2.removeAll();
                    mPostAdapter2.addAll(arg0);
                    mRecyclerView.setAdapter(mPostAdapter2);
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                ShowToast(arg1);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.addWhereEqualTo("topic",topic);
        query.count(this, Post.class, new CountListener() {
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

}
