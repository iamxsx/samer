package com.xsx.samer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonReclclerViewAdapter;
import com.xsx.samer.adapter.MyViewHolder;
import com.xsx.samer.fragment.BaseFragment;
import com.xsx.samer.model.ClubEvent;
import com.xsx.samer.utils.CollectionUtil;
import com.xsx.samer.utils.ImageLoadOptions;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by XSX on 2015/10/25.
 */
public class ClubEventListActivity extends BaseActivity{

    private RecyclerView recyclerView;
    private HomeClubEventAdapter homeClubEventAdapter;
    private List<ClubEvent> events;
    private PullToRefreshView mPullToRefreshView;
    private ClubEvent clubEvent;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_event_list);
        initViews();
        initDatas();
    }

    private void initDatas() {
        BmobQuery<ClubEvent> query = new BmobQuery<ClubEvent>();
        // 返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        query.order("-createdAt");
        //把兼职贴的作者也查出来
        query.include("author");
        // 执行查询方法
        query.findObjects(ClubEventListActivity.this, new FindListener<ClubEvent>() {
            @Override
            public void onSuccess(List<ClubEvent> object) {
                if (CollectionUtil.isNotNull(object)) {
                    homeClubEventAdapter.addAll(object);
                    recyclerView.setAdapter(homeClubEventAdapter);

                } else {
                    ShowToast("暂无话题信息");
                    if (events != null) {
                        events.clear();
                    }
                }
                mPullToRefreshView.setRefreshing(false);
            }

            @Override
            public void onError(int code, String msg) {
                if (events != null) {
                    events.clear();
                }
                ShowToast("数据不存在");
            }
        });
    }

    private void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("校内活动");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setRefreshing(true);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobQuery<ClubEvent> query = new BmobQuery<ClubEvent>();
                query.count(ClubEventListActivity.this, ClubEvent.class, new CountListener() {
                    @Override
                    public void onSuccess(int arg0) {
                        if (arg0 > events.size()) {
                            queryMore();
                        }
                        mPullToRefreshView.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        ShowToast("数据加载失败");
                        mPullToRefreshView.setRefreshing(false);
                    }
                });
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(ClubEventListActivity.this));

        events = new ArrayList<>();
        homeClubEventAdapter = new HomeClubEventAdapter(ClubEventListActivity.this, events);
        homeClubEventAdapter.setOnItemClickLitener(new CommonReclclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                clubEvent=events.get(position);
                Intent intent = new Intent(ClubEventListActivity.this, ClubEventActivity.class);
                intent.putExtra("clubEvent",clubEvent);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(homeClubEventAdapter);


    }

    protected void queryMore() {
        BmobQuery<ClubEvent> query = new BmobQuery<ClubEvent>();
        // 返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        query.order("-createdAt");
        query.include("author");
        query.findObjects(ClubEventListActivity.this, new FindListener<ClubEvent>() {

            @Override
            public void onSuccess(List<ClubEvent> arg0) {
                if (CollectionUtil.isNotNull(arg0)) {
                    homeClubEventAdapter.removeAll();
                    homeClubEventAdapter.addAll(arg0);
                    recyclerView.setAdapter(homeClubEventAdapter);
                    mPullToRefreshView.setRefreshing(false);
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                mPullToRefreshView.setRefreshing(false);
            }
        });

    }
    
    class HomeClubEventAdapter extends CommonReclclerViewAdapter<ClubEvent> {

        private View view;

        public HomeClubEventAdapter(Context context, List<ClubEvent> list) {
            super(context, list);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = LayoutInflater.from(context).inflate(R.layout.item_club_event, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            ClubEvent event = list.get(position);
            ImageView iv_club_img = (ImageView) holder.getView(R.id.iv_club_img);
            TextView tv_event_name = (TextView) holder.getView(R.id.tv_event_name);
            TextView tv_event_time = (TextView) holder.getView(R.id.tv_event_time);
            TextView tv_event_place = (TextView) holder.getView(R.id.tv_event_place);
            TextView tv_event_person = (TextView) holder.getView(R.id.tv_event_person);
            TextView tv_event_organizer = (TextView) holder.getView(R.id.tv_event_organizer);
            TextView tv_event_desc = (TextView) holder.getView(R.id.tv_event_desc);

            tv_event_name.setText(event.getEventName() + "");
            tv_event_time.setText(event.getEventTime() + "");
            tv_event_place.setText(event.getEventPlace() + "");
            tv_event_person.setText(event.getEventPerson() + "");
            tv_event_organizer.setText(event.getEventOrganizer() + "");
            tv_event_desc.setText(event.getEventDesc() + "");
            if (event.getEventPoster() != null) {
                ImageLoader.getInstance().displayImage(event.getEventPoster(), iv_club_img, ImageLoadOptions.getOptions());
            }
            // 如果设置了回调，则设置点击事件
            if (mOnItemClickLitener != null)
            {
                view.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemClick(view, pos);
                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemLongClick(view, pos);
                        return false;
                    }
                });
            }
        }

    }

}
