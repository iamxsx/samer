package com.xsx.samer.model;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 *社团
 * Created by XSX on 2015/10/22.
 */
public class Club extends BmobObject{

    private String clubName;
    /**
     * 社团头像
     */
    private String clubImg;
    /**
     *
     */
    private Integer numbers;

    /**
     *
     */
    private String desc;

    /**
     * 照片墙
     */
    private String bgUrl;

    /**
     * 多对多关系，社团成员
     */
    private BmobRelation mumbers;

    /**
     * 社团管理员
     */
    private User clubManager;

    public Club() {
    }

    public Club(String clubName,String clubImg, String desc,  Integer numbers) {
        this.clubImg = clubImg;
        this.desc = desc;
        this.clubName = clubName;
        this.numbers = numbers;
    }



    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubImg() {
        return clubImg;
    }

    public void setClubImg(String clubImg) {
        this.clubImg = clubImg;
    }

    public Integer getNumbers() {
        return numbers;
    }

    public void setNumbers(Integer numbers) {
        this.numbers = numbers;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public BmobRelation getMumbers() {
        return mumbers;
    }

    public void setMumbers(BmobRelation mumbers) {
        this.mumbers = mumbers;
    }

    public User getClubManager() {
        return clubManager;
    }

    public void setClubManager(User clubManager) {
        this.clubManager = clubManager;
    }
}
