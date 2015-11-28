package com.xsx.samer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * Created by XSX on 2015/10/11.
 */
public class CommonUtil {

    /**
     * 获取网络信息
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


    /**
     * 检查是否有网
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context){
        NetworkInfo info=getNetworkInfo(context);
        if(info!=null){
            return info.isAvailable();
        }
        return false;
    }


    /**
     * 检查是否是移动网络
     * @param context
     * @return
     */
    public static boolean isMobile(Context context){
        NetworkInfo info=getNetworkInfo(context);
        if(info!=null){
            if(info.getType()==ConnectivityManager.TYPE_MOBILE){
                return true;
            }
        }
        return false;
    }

    public static boolean checkSdCard(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }

}
