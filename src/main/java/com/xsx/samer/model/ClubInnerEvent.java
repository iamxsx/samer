package com.xsx.samer.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by XSX on 2015/10/25.
 */
public class ClubInnerEvent extends BmobObject{

    private String content;

    /**
     * 发布人
     */
    private String author;

    private String clubName;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }
}
