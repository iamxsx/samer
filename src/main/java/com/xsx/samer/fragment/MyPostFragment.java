package com.xsx.samer.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xsx.samer.R;
import com.xsx.samer.adapter.MyPostAdapter;
import com.xsx.samer.model.Post;
import com.xsx.samer.model.User;
import com.xsx.samer.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 个人界面的自己发的帖子的fragment
 * Created by XSX on 2015/10/18.
 */
public class MyPostFragment extends BaseFragment{
    private MyPostAdapter myPostAdapter;
    private RecyclerView recyclerView;
    private List<Post> myPosts;
    ProgressDialog progress;
    private User currentUser;
    private String from;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_list,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //判断是来自自己的点击还是他人的点击
        from=getActivity().getIntent().getStringExtra("from");
        if(from==null||from.equals("me")){
            currentUser= (User) userManager.getCurrentUser(User.class);
        }else{
            currentUser= (User) getActivity().getIntent().getSerializableExtra("target_user");
        }
        initViews();
        initDatas();
    }

    private void initViews() {
        recyclerView= (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myPosts=new ArrayList<Post>();
        myPostAdapter=new MyPostAdapter(getActivity(),myPosts);
        recyclerView.setAdapter(myPostAdapter);
    }

    private void initDatas() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("正在加载信息...");
        progress.setCanceledOnTouchOutside(true);
        progress.show();

        BmobQuery<Post> query=new BmobQuery<Post>();
        //查找出当前的用户所有发过的帖子
        query.addWhereEqualTo("author",currentUser);
        query.order("-createdAt");
        query.findObjects(getActivity(), new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (CollectionUtil.isNotNull(list)) {
                    myPostAdapter.addAll(list);
                    recyclerView.setAdapter(myPostAdapter);
                    progress.dismiss();
                }
            }

            @Override
            public void onError(int i, String s) {
                ShowToast("error=" + s);
                progress.dismiss();
            }
        });
    }
}
