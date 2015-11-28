package com.xsx.samer.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by XSX on 2015/10/12.
 */
public class Topic extends BmobObject{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String title;
    private String titleImg;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitleImg() {
        return titleImg;
    }
    public void setTitleImg(String titleImg) {
        this.titleImg = titleImg;
    }

}
