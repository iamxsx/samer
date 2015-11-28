package com.xsx.samer.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.utils.ImageLoadOptions;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.UpdateListener;

public class NewFriendAdapter extends CommonBaseAdapter<BmobInvitation>{

    public NewFriendAdapter(List<BmobInvitation> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BmobInvitation invitation=list.get(position);
        ViewHolder viewHolder = ViewHolder.getViewHolder(context, convertView,
                parent, R.layout.item_add_friend, position);
        TextView name = (TextView) viewHolder.getView(R.id.name);
        ImageView iv_avatar = (ImageView) viewHolder.getView(R.id.avatar);

        final Button btn_add = (Button) viewHolder.getView(R.id.btn_add);

        String avatar = invitation.getAvatar();

        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.mipmap.ic_launcher);
        }

        int status = invitation.getStatus();
        //请求尚未通过的
        if(status==BmobConfig.INVITE_ADD_NO_VALIDATION||status==BmobConfig.INVITE_ADD_NO_VALI_RECEIVED){
            btn_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    BmobLog.i("点击同意按钮:"+invitation.getFromid());
                    agreeAdd(btn_add, invitation);
                }
            });
        }else if(status==BmobConfig.INVITE_ADD_AGREE){
            btn_add.setText("已同意");
            btn_add.setBackgroundDrawable(null);
            btn_add.setTextColor(context.getResources().getColor(R.color.base_color_text_black));
            btn_add.setEnabled(false);
        }
        name.setText(invitation.getFromname());
        return viewHolder.getConvertView();
    }

    private void agreeAdd(final Button btn_add, BmobInvitation invitation) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("正在添加...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobUserManager.getInstance(context).agreeAddContact(invitation, new UpdateListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                btn_add.setText("已同意");
                btn_add.setBackgroundDrawable(null);
                btn_add.setTextColor(context.getResources().getColor(R.color.base_color_text_black));
                btn_add.setEnabled(false);
            }
            @Override
            public void onFailure(int arg0, String arg1) {
                progress.dismiss();
                Toast.makeText(context, "添加失败: " +arg1, Toast.LENGTH_SHORT);
            }
        });
    }

}
