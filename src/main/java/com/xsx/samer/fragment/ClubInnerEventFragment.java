package com.xsx.samer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonReclclerViewAdapter;
import com.xsx.samer.adapter.MyViewHolder;
import com.xsx.samer.model.Club;
import com.xsx.samer.model.ClubEvent;
import com.xsx.samer.model.ClubInnerEvent;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by XSX on 2015/10/25.
 */
public class ClubInnerEventFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private ClubInnerEventAdapter clubInnerEventAdapter;
    private List<ClubInnerEvent> events;
    private Club club;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_club_event,container,false);
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
        clubInnerEventAdapter = new ClubInnerEventAdapter(getActivity(), events);

        BmobQuery<ClubInnerEvent> query = new BmobQuery<>();
        query.order("-createdAt");
        query.addWhereEqualTo("clubName", club.getClubName());
        query.findObjects(getActivity(), new FindListener<ClubInnerEvent>() {
            @Override
            public void onSuccess(List<ClubInnerEvent> list) {
                if (list != null && list.size() > 0) {
                    clubInnerEventAdapter.addAll(list);
                    recyclerView.setAdapter(clubInnerEventAdapter);
                } else {
                    ShowToast("该社团暂未发布活动");
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    class ClubInnerEventAdapter extends CommonReclclerViewAdapter<ClubInnerEvent>{
        private View view;
        public ClubInnerEventAdapter(Context context, List<ClubInnerEvent> list) {
            super(context, list);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_club_inner_event_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ClubInnerEvent event=list.get(position);
            TextView tv_author= (TextView) holder.getView(R.id.tv_author);
            TextView tv_content= (TextView) holder.getView(R.id.tv_content);
            tv_author.setText(event.getAuthor()+" : ");
            tv_content.setText(event.getContent()+"");
        }
    }
}
