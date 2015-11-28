package com.xsx.samer.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonReclclerViewAdapter;
import com.xsx.samer.adapter.MyViewHolder;
import com.xsx.samer.model.Club;
import com.xsx.samer.model.ClubEvent;
import com.xsx.samer.ui.ClubActivity;
import com.xsx.samer.utils.ImageLoadOptions;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by XSX on 2015/10/22.
 */
public class ClubEventFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private ClubEventAdapter clubEventAdapter;
    private List<ClubEvent> events;
    private Club club;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_club_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        club = (Club) getActivity().getIntent().getSerializableExtra("club");
        initViews();
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        events = new ArrayList<>();
        clubEventAdapter = new ClubEventAdapter(getActivity(), events);

        BmobQuery<ClubEvent> query = new BmobQuery<>();
        query.order("-createdAt");
        query.addWhereEqualTo("eventOrganizer", club.getClubName());
        query.findObjects(getActivity(), new FindListener<ClubEvent>() {
            @Override
            public void onSuccess(List<ClubEvent> list) {
                if (list != null && list.size() > 0) {
                    clubEventAdapter.addAll(list);
                    recyclerView.setAdapter(clubEventAdapter);
                } else {
                    ShowToast("该社团暂未发布活动");
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    class ClubEventAdapter extends CommonReclclerViewAdapter<ClubEvent> {
        private View view;

        public ClubEventAdapter(Context context, List<ClubEvent> list) {
            super(context, list);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_club_event_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ClubEvent event = events.get(position);
            ImageView iv_club = (ImageView) holder.getView(R.id.iv_club);
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

        }
    }
}
