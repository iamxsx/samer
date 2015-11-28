package com.xsx.samer.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xsx.samer.R;
import com.xsx.samer.adapter.FriendListAdapter;
import com.xsx.samer.model.User;
import com.xsx.samer.ui.MyDetailActivity;
import com.xsx.samer.ui.NewFriendActivity;
import com.xsx.samer.utils.CollectionUtil;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


/**
 * 联系人界面
 * @author XSX
 *
 */
public class ContactFragment extends BaseFragment implements
        OnItemClickListener, OnItemLongClickListener {

    private static final String TAG = "ContactFragment";
    private EditText et_msg_search;
    private ListView listview;
    /**
     * 新朋友
     */
    private LinearLayout layout_new;
    /**
     * 附近的人
     */
    private LinearLayout layout_near;
    /**
     * 显示新好友的tip
     */
    private ImageView iv_msg_tips;
    private FriendListAdapter adapter;

    private List<BmobChatUser> friends=new ArrayList<BmobChatUser>();
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView==null){
            rootView=inflater.inflate(
                    R.layout.fragment_contacts, container, false);
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
        iv_msg_tips=(ImageView) findViewById(R.id.iv_msg_tips);
        listview=(ListView) findViewById(R.id.list_friends);
        layout_new=(LinearLayout) findViewById(R.id.layout_new);
        layout_near=(LinearLayout) findViewById(R.id.layout_near);
        //fridends=BmobDB.create(getActivity()).getAllContactList();
        friends= CollectionUtil.map2list(applicaiton.getContactList());
        layout_new.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewFriendActivity.class);
                intent.putExtra("from", "contact");
                startActivity(intent);
            }
        });
        layout_near.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //startAnimActivity(NearPeopleActivity.class);
            }
        });
        adapter=new FriendListAdapter(friends, getActivity());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
        listview.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                hideSoftInputView();
                return false;
            }
        });
        refresh();
    }

    /**
     * 获取好友列表
     */
    private void queryMyfriends(){
        //检测是否有新的好友请求
        if(BmobDB.create(getActivity()).hasNewInvite()){
            iv_msg_tips.setVisibility(View.VISIBLE);
        }else{
            iv_msg_tips.setVisibility(View.INVISIBLE);
        }
        //在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
        // 重新设置下内存中保存的好友列表
        applicaiton.setContactList(CollectionUtil.list2map(BmobDB.create(getActivity()).getContactList()));
        Map<String,BmobChatUser> friends = applicaiton.getContactList();
        if(adapter==null){
            adapter = new FriendListAdapter( CollectionUtil.map2list(friends),getActivity());
            listview.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }
    User user;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        BmobChatUser bmobChatUser = friends.get(position);

        //由于BmobChatUser无法强制转化为User,因此需拿到ObjectId再去查看用户
        BmobQuery<User> query=new BmobQuery<>();
        String objectId=bmobChatUser.getObjectId();
        query.addWhereEqualTo("objectId", objectId);
        query.findObjects(getActivity(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                user = list.get(0);
                //包在Intent里
                Intent intent = new Intent(getActivity(), MyDetailActivity.class);
                intent.putExtra("from", "friend");
                intent.putExtra("target_user", user);
                startActivity(intent);
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        BmobChatUser bmobChatUser = friends.get(position);
        showDeleteDialog(bmobChatUser);
        return true;
    }

    /**
     * 长按好友弹出dialog
     * @param bmobChatUser
     */
    private void showDeleteDialog(final BmobChatUser bmobChatUser) {
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setTitle("提示").
                setMessage("是否删除好友"+bmobChatUser.getUsername())
                .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobDB.create(getActivity()).deleteContact(bmobChatUser.getObjectId());
                        dialog.dismiss();
                        refresh();
                    }
                });
        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * 刷新联系人界面
     */
    public void refresh() {
        //更新UI需要在主线程进行
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                queryMyfriends();
            }
        });
    }

    private boolean hidden;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if(!hidden){
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!hidden){
            refresh();
        }
    }


}
