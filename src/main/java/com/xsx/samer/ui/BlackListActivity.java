package com.xsx.samer.ui;

import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xsx.samer.R;
import com.xsx.samer.adapter.BlackListAdapter;
import com.xsx.samer.utils.CollectionUtil;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;



/**
 * 黑名单列表界面
 * @author XSX
 *
 */
public class BlackListActivity extends BaseActivity implements OnItemClickListener{
    private ListView listview;
    private BlackListAdapter adapter;
    private List<BmobChatUser> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        initViews();
    }

    private void initViews() {
        list=BmobDB.create(this).getBlackList();
        adapter = new BlackListAdapter(list,this);
        listview = (ListView) findViewById(R.id.list_blacklist);
        listview.setOnItemClickListener(this);
        listview.setAdapter(adapter);
    }

    /** 显示移除黑名单对话框
     * @Title: showRemoveBlackDialog
     * @Description: TODO
     * @param @param position
     * @param @param invite
     * @return void
     * @throws
     */
    public void showRemoveBlackDialog(final int position, final BmobChatUser user) {
        new Builder(this).
                setTitle("提示").
                setMessage("你确定将"+user.getUsername()+"移出黑名单吗?")
                .setNegativeButton("取消", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.remove(position);
                        userManager.removeBlack(user.getUsername(),new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                ShowToast("移出黑名单成功");
                                //重新设置下内存中保存的好友列表
                                application.setContactList(CollectionUtil.list2map(BmobDB.create(getApplicationContext()).getContactList()));
                            }

                            @Override
                            public void onFailure(int arg0, String arg1) {
                                // TODO Auto-generated method stub
                                ShowToast("移出黑名单失败:"+arg1);
                            }
                        });
                    }
                }).show();


    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        BmobChatUser invite = (BmobChatUser) adapter.getItem(arg2);
        showRemoveBlackDialog(arg2,invite);
    }


}
