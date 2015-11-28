package com.xsx.samer.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.xsx.samer.R;
import com.xsx.samer.model.ClubInnerEvent;

import org.jsoup.Connection;

/**
 * Created by XSX on 2015/10/25.
 */
public class ClubInnerEventActivity extends BaseActivity{
    private static final String TAG ="ClubInnerEventActivity" ;
    private TextView tv_author;
    private TextView tv_content;
    private ClubInnerEvent clubInnerEvent;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_club_inner_event);
        clubInnerEvent= (ClubInnerEvent) getIntent().getSerializableExtra("clubInnerEvent");
        initViews();
    }

    private void initViews() {
        Log.i(TAG,""+clubInnerEvent.getAuthor()+""+clubInnerEvent.getContent());
        tv_author= (TextView) findViewById(R.id.tv_author);
        tv_content= (TextView) findViewById(R.id.tv_content);
        tv_author.setText(clubInnerEvent.getAuthor()+"");
        tv_content.setText(clubInnerEvent.getContent()+"");
    }
}
