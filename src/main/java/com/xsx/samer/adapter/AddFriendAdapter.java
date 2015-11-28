package com.xsx.samer.adapter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.v3.listener.PushListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.utils.ImageLoadOptions;

/**
 * 添加朋友列表适配器
 * @author XSX
 *
 */
public class AddFriendAdapter extends CommonReclclerViewAdapter<BmobChatUser>{
    private View view;
    public AddFriendAdapter(List<BmobChatUser> list, Context context) {
        super(context, list);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewHolder, int viewType) {
        view= LayoutInflater.from(context).inflate(R.layout.item_add_friend,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        final BmobChatUser bmobChatUser=list.get(position);
        ImageView iv_avatar=(ImageView) viewHolder.getView(R.id.avatar);
        TextView tv_name=(TextView) viewHolder.getView(R.id.name);
        Button btn_add=(Button) viewHolder.getView(R.id.btn_add);
        String avatar=bmobChatUser.getAvatar();
        if(avatar!=null&& !avatar.equals("")){
            ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
        }else{
            //当没有头像时显示默认图片
            iv_avatar.setImageResource(R.mipmap.ic_launcher);
        }
        tv_name.setText(bmobChatUser.getUsername());
        btn_add.setText("添加");
        btn_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final ProgressDialog progress = new ProgressDialog(context);
                progress.setMessage("正在添加...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                //发送tag请求
                BmobChatManager.getInstance(context).sendTagMessage(MsgTag.ADD_CONTACT, bmobChatUser.getObjectId(),new PushListener() {
                    @Override
                    public void onSuccess() {
                        progress.dismiss();
                        Toast.makeText(context,"发送请求成功，等待对方验证!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int arg0, final String arg1) {
                        progress.dismiss();
                        Toast.makeText(context,"发送请求失败，请重新添加!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
