package com.xsx.samer.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.xsx.samer.R;
import com.xsx.samer.model.Club;
import com.xsx.samer.model.ClubInnerEvent;
import com.xsx.samer.model.MyBmobInstallation;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by XSX on 2015/10/25.
 */
public class AddClubInnerEventActivity extends BaseActivity {
    private EditText et_content;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_add_club_inner_event);
        initViews();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        et_content = (EditText) findViewById(R.id.et_content);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chose, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sure:
                String content = et_content.getText().toString();
                if ("".equals(content) || "".equals(content.trim())) {
                    ShowToast("内容不能为空");
                    return false;
                }
                final ClubInnerEvent clubInnerEvent = new ClubInnerEvent();
                final Club club = (Club) getIntent().getSerializableExtra("club");
                clubInnerEvent.setClubName(club.getClubName());
                clubInnerEvent.setContent(content);
                clubInnerEvent.setAuthor(userManager.getCurrentUserName());
                clubInnerEvent.save(AddClubInnerEventActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        ShowToast("发布内部消息成功");
                        //给社团成员发送推送
                        BmobPushManager bmobPush = new BmobPushManager(AddClubInnerEventActivity.this);
                        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();

                        List<String> channels = new ArrayList<String>();
                        channels.add(club.getClubName());
                        query.addWhereContainedIn("channels",channels);
                        //query.addWhereEqualTo("channels", channels);
                        bmobPush.setQuery(query);

                        Gson gson = new Gson();
                        //得到json字符串
                        String json = gson.toJson(clubInnerEvent);
                        bmobPush.pushMessage(json);

                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        ShowToast("活动发布失败：" + s);
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
