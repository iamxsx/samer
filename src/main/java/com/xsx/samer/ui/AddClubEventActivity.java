package com.xsx.samer.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.xsx.samer.R;
import com.xsx.samer.model.Club;
import com.xsx.samer.model.ClubEvent;
import com.xsx.samer.model.MyBmobInstallation;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by XSX on 2015/10/24.
 */
public class AddClubEventActivity extends BaseActivity {

    private static final String TAG ="AddClubEventActivity" ;
    private Toolbar toolbar;
    private EditText et_event_name;
    private EditText et_event_time;
    private EditText et_event_place;
    private EditText et_event_person;
    private EditText et_event_desc;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_add_club_event);
        initViews();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle("发布活动");
        setSupportActionBar(toolbar);
        et_event_name = (EditText) findViewById(R.id.et_event_name);
        et_event_time = (EditText) findViewById(R.id.et_event_time);
        et_event_place = (EditText) findViewById(R.id.et_event_place);
        et_event_person = (EditText) findViewById(R.id.et_event_person);
        et_event_desc = (EditText) findViewById(R.id.et_event_desc);

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
                send();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void send() {
        String event_name = et_event_name.getText().toString();
        String event_time = et_event_time.getText().toString();
        String event_person = et_event_person.getText().toString();
        String event_place = et_event_place.getText().toString();
        String event_desc = et_event_desc.getText().toString();

        final ClubEvent clubEvent = new ClubEvent();
        clubEvent.setEventName(event_name);
        clubEvent.setEventDesc(event_desc);
        clubEvent.setEventPerson(event_person);
        clubEvent.setEventPlace(event_place);
        clubEvent.setEventTime(event_time);

        final Club club = (Club) getIntent().getSerializableExtra("club");
        clubEvent.setEventOrganizer(club.getClubName());

        clubEvent.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                ShowToast("活动发布成功");
                //发送消息推送给全体社员
                BmobPushManager bmobPush = new BmobPushManager(AddClubEventActivity.this);
                Gson gson=new Gson();
                //得到json字符串
                String json=gson.toJson(clubEvent);
                bmobPush.pushMessageAll(json);
                //bmobPush.pushMessageAll(club.getClubName()+"发布新活动啦");
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                ShowToast("活动发布失败:"+s);
            }
        });

    }
}
