package com.xsx.samer.utils;

import com.xsx.samer.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;

/**
 * 集合操作的工具类
 * Created by XSX on 2015/10/11.
 */
public class CollectionUtil {

    /**
     * 判断集合是否为空
     * @param collection
     * @return
     */
    public static boolean isNotNull(Collection<?> collection){
        if(collection!=null && collection.size()>0){
            return  true;
        }
        return false;
    }

    /**
     * 将list存储的好友列表转化为以map保存
     * @param users
     * @return
     */
    public static Map<String,BmobChatUser> list2map(List<BmobChatUser> users){
        Map<String,BmobChatUser> friends = new HashMap<String,BmobChatUser>();
        for(BmobChatUser user:users){
            friends.put(user.getUsername(),user);
        }
        return friends;
    }

    public static List<BmobChatUser> map2list(Map<String,BmobChatUser> maps){
        List<BmobChatUser> users=new ArrayList<BmobChatUser>();
        Iterator<Map.Entry<String,BmobChatUser>> iterator= maps.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,BmobChatUser> entry=iterator.next();
            users.add(entry.getValue());
        }
        return users;
    }



}
