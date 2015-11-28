package com.xsx.samer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonBaseAdapter;
import com.xsx.samer.model.Club;
import com.xsx.samer.model.User;
import com.xsx.samer.ui.ClubActivity;
import com.xsx.samer.utils.ImageLoadOptions;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * 社团成员列表
 * Created by XSX on 2015/10/22.
 */
public class ClubMumberListFragment extends BaseFragment {

    private List<User> mumbers;

    private ListView listview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mumbers_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        Club club = (Club) getActivity().getIntent().getSerializableExtra("club");
        listview = (ListView) findViewById(R.id.listview);
        BmobQuery<User> query = new BmobQuery<>();
        //只查询会员列表
        query.addWhereRelatedTo("mumbers", new BmobPointer(club));
        query.findObjects(getActivity(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                mumbers = list;
                if (mumbers != null) {
                    listview.setAdapter(new ClubMumberListAdapter(mumbers, getActivity()));
                }else{
                    ShowToast("该社团暂无成员 orz");
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    class ClubMumberListAdapter extends CommonBaseAdapter<User> {

        public ClubMumberListAdapter(List<User> list, Context context) {
            super(list, context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User mumber = mumbers.get(position);
            ViewHolder viewHolder = ViewHolder.getViewHolder(context, convertView, parent, R.layout.fragment_mumbers_list_item, position);
            ImageView iv_avatar = (ImageView) viewHolder.getView(R.id.iv_avatar);
            TextView tv_name = (TextView) viewHolder.getView(R.id.tv_name);
            tv_name.setText(mumber.getNick() + "");
            if (mumber.getAvatar() != null) {
                ImageLoader.getInstance().displayImage(mumber.getAvatar(), iv_avatar, ImageLoadOptions.getOptions());
            } else {
                iv_avatar.setImageResource(R.drawable.samer);
            }
            return viewHolder.getConvertView();
        }
    }
}
