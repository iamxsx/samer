package com.xsx.samer.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

import com.xsx.samer.R;
import com.xsx.samer.adapter.RecentAdapter2;
import com.xsx.samer.fragment.BaseFragment;
import com.xsx.samer.ui.ChatActivity;

public class RecentFragment2 extends BaseFragment implements
        OnItemClickListener, OnItemLongClickListener {
    private ListView listView;
    private List<BmobRecent> mDatas;
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView==null){
            rootView=inflater.inflate(
                    R.layout.fragment_recent_list, container, false);
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
    }


    private void initViews() {
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        mDatas = BmobDB.create(getActivity()).queryRecents();
        listView.setAdapter(new RecentAdapter2(mDatas,getActivity()));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        BmobRecent bmobRecent = mDatas.get(position);
        String targetId=bmobRecent.getTargetid();
        //resetUnread重置未读消息，将未读消息置0
        BmobDB.create(getActivity()).resetUnread(targetId);
        //构建一个聊天对象
        BmobChatUser bmobChatUser=new BmobChatUser();
        bmobChatUser.setAvatar(bmobRecent.getAvatar());
        bmobChatUser.setNick(bmobRecent.getNick());
        bmobChatUser.setUsername(bmobRecent.getUserName());
        bmobChatUser.setObjectId(bmobRecent.getTargetid());
        //包在Intent里
        Intent intent=new Intent(getActivity(),ChatActivity.class);
        intent.putExtra("bmobChatUser", bmobChatUser);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        BmobRecent recent = mDatas.get(position);
        showDeleteDialog(recent);
        return true;
    }

    private void showDeleteDialog(final BmobRecent recent) {
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setTitle("提示").
                setMessage("是否删除与"+recent.getUserName()+"的对话")
                .setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
                        BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("取消", new OnClickListener() {
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
                mDatas = BmobDB.create(getActivity()).queryRecents();
                listView.setAdapter(new RecentAdapter2(mDatas,getActivity()));
            }
        });
    }
}
