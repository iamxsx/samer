package com.xsx.samer.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.xsx.samer.R;
import com.xsx.samer.adapter.NewFriendAdapter;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;


/**
 * 新朋友
 * @author XSX
 *
 */
public class NewFriendActivity extends BaseActivity implements OnItemLongClickListener{
    private ListView listview;
    private NewFriendAdapter newFriendAdapter;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_new_friend);
        initViews();
    }
    private void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("邀请列表");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listview = (ListView)findViewById(R.id.list_newfriend);
        listview.setOnItemLongClickListener(this);
        newFriendAdapter = new NewFriendAdapter(BmobDB.create(this).queryBmobInviteList(),this);
        listview.setAdapter(newFriendAdapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        BmobInvitation invitation = (BmobInvitation) newFriendAdapter.getItem(position);
        showDeleteDialog(position,invitation);
        return true;
    }
    private void showDeleteDialog(int position, final BmobInvitation invite) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("提示").
                setMessage("是否删除"+invite.getFromname()+"的好友请求")
                .setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobDB.create(NewFriendActivity.this).deleteInviteMsg(invite.getFromid(), Long.toString(invite.getTime()));
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
}
