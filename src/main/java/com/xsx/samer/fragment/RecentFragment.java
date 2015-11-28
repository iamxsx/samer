package com.xsx.samer.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonReclclerViewAdapter;
import com.xsx.samer.adapter.MyViewHolder;
import com.xsx.samer.adapter.PostAdapter;
import com.xsx.samer.adapter.RecentAdapter;
import com.xsx.samer.model.Post;
import com.xsx.samer.ui.ChatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

/**
 * Created by XSX on 2015/10/11.
 */
public class RecentFragment extends BaseFragment{

    private static final String TAG = "RecentFragment";
    private RecyclerView mRecyclerView;
    /**
     * 最近联系人
     */
    private List<BmobRecent> list;
    private RecentAdapter mRecentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_recent_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = new ArrayList<BmobRecent>();
        list = BmobDB.create(getActivity()).queryRecents();
        Log.i(TAG,"list="+list);
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecentAdapter = new RecentAdapter(getActivity(), list);
        mRecentAdapter.setOnItemClickLitener(new CommonReclclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                BmobRecent bmobRecent = list.get(position);
                String targetId = bmobRecent.getTargetid();
                //resetUnread重置未读消息，将未读消息置0
                BmobDB.create(getActivity()).resetUnread(targetId);
                //构建一个聊天对象
                BmobChatUser bmobChatUser = new BmobChatUser();
                bmobChatUser.setAvatar(bmobRecent.getAvatar());
                bmobChatUser.setNick(bmobRecent.getNick());
                bmobChatUser.setUsername(bmobRecent.getUserName());
                bmobChatUser.setObjectId(bmobRecent.getTargetid());
                //包在Intent里
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("bmobChatUser", bmobChatUser);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                BmobRecent recent = list.get(position);
                showDeleteDialog(recent);
            }
        });

        initViews();
    }

    private void initViews() {




    }

    private void showDeleteDialog(final BmobRecent recent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示").
                setMessage("是否删除与"+recent.getUserName()+"的对话")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
                        BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
    private boolean hidden;
    /*
     * 当fragment显示时，hidden为false，隐藏时hidden为true
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if(!hidden){
            refresh();
        }
    }

    /*
     * 在fragment中，这玩意只调用一次
     */
    @Override
    public void onResume() {
        super.onResume();
        if(!hidden){
            refresh();
        }
    }

    /**
     *刷新最近回话界面
     */
    public void refresh() {
        //更新UI需要在主线程进行
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list = BmobDB.create(getActivity()).queryRecents();
                mRecyclerView.setAdapter(new RecentAdapter(getActivity(),list));
            }
        });
    }

}
