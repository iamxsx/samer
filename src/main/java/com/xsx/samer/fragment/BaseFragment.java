package com.xsx.samer.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.xsx.samer.CustomApplication;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;

/**
 * Created by XSX on 2015/10/11.
 */
public class BaseFragment extends Fragment {

    protected CustomApplication applicaiton;
    protected BmobUserManager userManager;
    protected BmobChatManager chatManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        applicaiton= CustomApplication.getInstance();
        userManager=BmobUserManager.getInstance(getActivity());
        chatManager=BmobChatManager.getInstance(getActivity());
    }

    public View findViewById(int paramInt) {
        return getView().findViewById(paramInt);
    }

    public void startAnimActivity(Class clazz){
        startActivity(new Intent(getActivity(),clazz));
    }

    public void ShowToast(int errorId) {
        Toast.makeText(getActivity(), errorId, Toast.LENGTH_SHORT).show();
    }

    public void ShowToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


    public void hideSoftInputView(){
        InputMethodManager manager = ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
