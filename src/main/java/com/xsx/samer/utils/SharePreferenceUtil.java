package com.xsx.samer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 统一管理SharePreference的类
 * Created by XSX on 2015/10/11.
 */
public class SharePreferenceUtil {
    /**
     * 是否允许推送消息
     */
    private String SHARED_KEY_NOTIFY = "shared_key_notify";
    /**
     * 是否允许声音提醒
     */
    private String SHARED_KEY_VOICE = "shared_key_sound";
    /**
     * 是否允许振动
     */
    private String SHARED_KEY_VIBRATE = "shared_key_vibrate";
    private SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor editor;

    public SharePreferenceUtil(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }


    // 是否允许推送通知
    public boolean isAllowPushNotify() {
        return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
    }

    public void setPushNotifyEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_NOTIFY, isChecked);
        editor.commit();
    }

    // 允许声音
    public boolean isAllowVoice() {
        return mSharedPreferences.getBoolean(SHARED_KEY_VOICE, true);
    }

    public void setAllowVoiceEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_VOICE, isChecked);
        editor.commit();
    }

    // 允许震动
    public boolean isAllowVibrate() {
        return mSharedPreferences.getBoolean(SHARED_KEY_VIBRATE, true);
    }

    public void setAllowVibrateEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_VIBRATE, isChecked);
        editor.commit();
    }

}
