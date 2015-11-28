package com.xsx.samer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonBaseAdapter;
import com.xsx.samer.model.Club;
import com.xsx.samer.model.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by XSX on 2015/10/21.
 */
public class ChoseClubActivity extends BaseActivity implements OnItemClickListener{

    private Toolbar toolbar;

    private ListView listview;
    private List<Club> clubs;
    private ClubAdapter clubAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chose_club);
        initViews();
        initDatas();

        //initData();

    }

//    private void initData() {
//        List<Club> clubs=new ArrayList<Club>();
//        List<User> mumbers=new ArrayList<>();
//        mumbers.add(userManager.getCurrentUser(User.class));
//        Club club1=new Club(
//                "创E网络联盟社团",
//                "",
//                "创E网络联盟社团，成立于2009年，本着“宣扬计算机网络文化，营造计算机网络氛围，培养社员创新能力，丰富社员课余生活”的社团宗旨，聚集学校计算机爱好者共同研究、讨论网页设计技术、图片美工、平面设计技术、视频制作技术、网络工作原理。",
//                0,
//                mumbers
//        );
//        club1.save(this, new SaveListener() {
//            @Override
//            public void onSuccess() {
//                ShowToast("成功");
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                ShowToast(s);
//            }
//        });
//
//
//    }

    private void initDatas() {
        clubs = new ArrayList<>();

        clubAdapter = new ClubAdapter(clubs,this);

        BmobQuery<Club> query = new BmobQuery<>();
        //查询指定列，查询出社团名，社团详情，会员数，社团头像
        query.addQueryKeys("clubName,desc,numbers,clubImg,clubManager");
        query.findObjects(this, new FindListener<Club>() {
            @Override
            public void onSuccess(List<Club> list) {
                clubAdapter.addAll(list);
                listview.setAdapter(clubAdapter);
            }

            @Override
            public void onError(int i, String s) {
                ShowToast(s);
            }
        });
    }

    private void initViews() {
        listview = (ListView) findViewById(R.id.listview);
        listview.setOnItemClickListener(this);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Club club=clubs.get(i);
        Intent intent=new Intent(ChoseClubActivity.this,ClubActivity.class);
        intent.putExtra("club",club);
        startActivity(intent);
    }

    class ClubAdapter extends CommonBaseAdapter<Club> {

        public ClubAdapter(List<Club> list, Context context) {
            super(list, context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.getViewHolder(context, convertView, parent, R.layout.item_club, position);Club club=list.get(position);
            ImageView iv_club= (ImageView) holder.getView(R.id.iv_club);
            TextView tv_club_name= (TextView) holder.getView(R.id.tv_club_name);
            TextView tv_club_desc= (TextView) holder.getView(R.id.tv_club_desc);
            TextView tv_club_number= (TextView) holder.getView(R.id.tv_club_number);

            tv_club_name.setText(club.getClubName()+"");
            tv_club_desc.setText(club.getDesc()+"");
            tv_club_number.setText(club.getNumbers()+"");

            return holder.getConvertView();
        }


    }


}
