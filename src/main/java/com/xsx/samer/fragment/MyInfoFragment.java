package com.xsx.samer.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xsx.samer.R;
import com.xsx.samer.model.User;
import com.xsx.samer.ui.ChatActivity;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by XSX on 2015/10/18.
 */
public class MyInfoFragment extends BaseFragment implements View.OnClickListener {

    private TextView tv_account;
    private TextView tv_signature;
    private TextView tv_nick;
    private TextView tv_sex;
    private TextView tv_major;
    private User currentUser;
    private String from;
    private Button btn_add_friend;
    private Button btn_chat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mydes, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        from = getActivity().getIntent().getStringExtra("from");
        if (from == null || from.equals("me")) {
            currentUser = (User) userManager.getCurrentUser(User.class);
        } else {
            currentUser = (User) getActivity().getIntent().getSerializableExtra("target_user");
        }
        initViews();
    }

    private void initViews() {
        tv_account = (TextView) findViewById(R.id.tv_accout);
        tv_signature = (TextView) findViewById(R.id.tv_signature);
        tv_nick = (TextView) findViewById(R.id.tv_nick);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_major = (TextView) findViewById(R.id.tv_major);
        tv_account.setText(currentUser.getUsername());
        tv_signature.setText(currentUser.getSignature() != null ? currentUser.getSignature() : "暂未设置个性签名");
        tv_nick.setText(currentUser.getNick() != null ? currentUser.getNick() : "暂未设置昵称");
        tv_sex.setText(currentUser.getSex());
        tv_major.setText(currentUser.getMajor());
        if (from.equals("me")) {
            tv_signature.setOnClickListener(this);
            tv_nick.setOnClickListener(this);
        }else if(from.equals("other")){
            btn_add_friend= (Button) findViewById(R.id.btn_add_friend);
            btn_add_friend.setVisibility(View.VISIBLE);
            btn_add_friend.setOnClickListener(this);
        }
        else if (from.equals("friend")){
            btn_chat= (Button) findViewById(R.id.btn_chat);
            btn_chat.setVisibility(View.VISIBLE);
            btn_chat.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_signature:
                showUpdateDialog(R.id.tv_signature);
                break;
            case R.id.tv_nick:
                showUpdateDialog(R.id.tv_nick);
                break;
            case R.id.btn_add_friend:
                addFriend();
                break;
            case R.id.btn_chat:
                chat();
                break;
        }

    }

    private void chat() {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bmobChatUser", currentUser);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void addFriend() {
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage("正在添加...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //发送tag请求
        BmobChatManager.getInstance(getActivity()).sendTagMessage(MsgTag.ADD_CONTACT, currentUser.getObjectId(),new PushListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                Toast.makeText(getActivity(), "发送请求成功，等待对方验证!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int arg0, final String arg1) {
                progress.dismiss();
                Toast.makeText(getActivity(),"发送请求失败，请重新添加!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 修改的dialog
     */
    private void showUpdateDialog(int id) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.edittext_dialog, null);//这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.et);//获得输入框对象
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        if (id == R.id.tv_nick) {
            builder.setTitle("修改昵称");
            builder.setPositiveButton("确定",//提示框的两个按钮
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            if (edit.getText() == null) {
                                ShowToast("昵称不能为空");
                                return;
                            }
                            currentUser.setNick(edit.getText() + "");
                            currentUser.update(getActivity(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    ShowToast("修改成功");
                                    tv_nick.setText(currentUser.getNick());
                                }

                                @Override
                                public void onFailure(int arg0, String arg1) {
                                    ShowToast("Failure:" + arg1);
                                }
                            });
                        }
                    });
        } else {
            builder.setTitle("修改个性签名");
            builder.setPositiveButton("确定",//提示框的两个按钮
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            if (edit.getText() == null) {
                                ShowToast("个性签名不能为空");
                                return;
                            }
                            currentUser.setSignature(edit.getText() + "");
                            currentUser.update(getActivity(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    ShowToast("修改成功");
                                    tv_signature.setText(currentUser.getSignature());
                                }

                                @Override
                                public void onFailure(int arg0, String arg1) {
                                    ShowToast("Failure:" + arg1);
                                }
                            });
                        }
                    });
        }

        builder.setNegativeButton("取消", null).create().show();
    }
}
