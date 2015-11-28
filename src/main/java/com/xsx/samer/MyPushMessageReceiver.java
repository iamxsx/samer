package com.xsx.samer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.xsx.samer.model.ClubEvent;
import com.xsx.samer.model.ClubInnerEvent;
import com.xsx.samer.model.EventMsg;
import com.xsx.samer.ui.ClubEventActivity;
import com.xsx.samer.ui.ClubInnerEventActivity;
import com.xsx.samer.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 社团消息推送接收器
 * Created by XSX on 2015/10/24.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {

    private static final String TAG = "MyPushMessageReceiver";
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_FLAG = 1;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("cn.bmob.push.action.MESSAGE")) {
            String jsonStr = intent.getStringExtra("msg");
            Log.i(TAG, "客户端收到推送内容：" + jsonStr);
            try {
                String object = new JSONObject(jsonStr).getString("alert");
                Log.i(TAG, object);
                Gson gson = new Gson();
                ClubEvent clubEvent = gson.fromJson(object, ClubEvent.class);
                ClubInnerEvent clubInnerEvent = gson.fromJson(object, ClubInnerEvent.class);
                if(clubEvent.getEventOrganizer()!=null){
                    Intent intent1=new Intent(context, ClubEventActivity.class);
                    intent1.putExtra("clubEvent",clubEvent);
                    PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0,
                            intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notify2 = new Notification.Builder(context)
                            .setSmallIcon(R.drawable.samer)
                            .setTicker(clubEvent.getEventOrganizer() + "发布新活动啦")
                            .setContentTitle(clubEvent.getEventOrganizer()+"发布新活动啦")
                            .setContentText("点击查看详情")
                            .setContentIntent(pendingIntent2)
                            .setNumber(1)
                            .getNotification();
                    // 16及之后增加的，在API11中可以使用getNotificatin()来代替
                    notify2.flags |= Notification.FLAG_AUTO_CANCEL;
                    notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_FLAG, notify2);
                }else if(clubInnerEvent.getAuthor()!=null){
                    Log.i(TAG,"author="+clubInnerEvent.getAuthor()+" content="+clubInnerEvent.getContent());
                    Intent intent2=new Intent(context, ClubInnerEventActivity.class);
                    intent2.putExtra("clubInnerEvent", clubInnerEvent);
                    PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0,
                            intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notify2 = new Notification.Builder(context)
                            .setSmallIcon(R.drawable.samer)
                            .setTicker(clubInnerEvent.getClubName() + "有新的内部活动通知")
                            .setContentTitle(clubInnerEvent.getAuthor()+"发布了新的内部活动")
                            .setContentText("点击查看详情")
                            .setContentIntent(pendingIntent2)
                            .setNumber(1)
                            .getNotification();
                    // 16及之后增加的，在API11中可以使用getNotificatin()来代替
                    notify2.flags |= Notification.FLAG_AUTO_CANCEL;
                    notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_FLAG, notify2);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
