package com.xsx.samer.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.xsx.samer.R;
import com.xsx.samer.adapter.AddFriendAdapter;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;


/**
 * 添加好友界面
 *
 * @author XSX
 */
public class AddFriendActivity extends BaseActivity implements OnClickListener {
    private List<BmobChatUser> users = new ArrayList<BmobChatUser>();
    private AddFriendAdapter adapter;
    private String searchName;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private EditText et;
    private Button btn_search;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_add_contact);
        initViews();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("查找好友");
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new AddFriendAdapter(users, this);
        mRecyclerView.setAdapter(adapter);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        et = (EditText) findViewById(R.id.et_find_name);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sure:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position,
//                            long id) {
//        BmobChatUser user = (BmobChatUser) adapter.getItem(position-1);
//        Intent intent =new Intent(this,UserDescActivity.class);
//        intent.putExtra("from", "add");
//        intent.putExtra("username", user.getUsername());
//        startActivity(intent);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                users.clear();
                searchName = et.getText().toString();
                if (searchName != null && !searchName.equals("")) {
                    initSearchList(false);
                } else {
                    ShowToast("请输入用户名");
                }
                break;
            default:
                break;
        }

    }

    ProgressDialog progress;

    private void initSearchList(final boolean isRefreshAction) {
        if (!isRefreshAction) {
            progress = new ProgressDialog(AddFriendActivity.this);
            progress.setMessage("正在搜索...");
            progress.setCanceledOnTouchOutside(true);
            progress.show();
        }
        userManager.queryUserByPage(isRefreshAction, 0, searchName, new FindListener<BmobChatUser>() {
            @Override
            public void onError(int arg0, String arg1) {
                BmobLog.i("查询错误:" + arg1);
                if (users != null) {
                    users.clear();
                }
                ShowToast("用户不存在");
                progress.dismiss();
            }

            @Override
            public void onSuccess(List<BmobChatUser> list) {
                if (list != null && list.size() > 0) {
                    if (isRefreshAction) {
                        users.clear();
                    }
                    adapter.addAll(list);
                    mRecyclerView.setAdapter(adapter);
                    if (list.size() < BRequest.QUERY_LIMIT_COUNT) {

                        ShowToast("用户搜索完成!");
                    } else {
                        //如果搜索出来的结果大于下拉菜单每页的显示条数，使加载更多可用
                    }
                } else {
                    BmobLog.i("查询成功:无返回值");
                    if (users != null) {
                        users.clear();
                    }
                    ShowToast("用户不存在");
                }
                progress.dismiss();
            }
        });
    }


}
