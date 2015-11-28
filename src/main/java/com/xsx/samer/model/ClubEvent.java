package com.xsx.samer.model;

import cn.bmob.v3.BmobObject;

/**
 * 社团活动
 * Created by XSX on 2015/10/22.
 */
public class ClubEvent extends BmobObject{

    /**
     */
    private String eventName;

    /**
     */
    private String eventTime;

    /**
     */
    private String eventOrganizer;

    /**
     */
    private String eventPlace;

    /**
     */
    private String eventPerson;

    /**
     */
    private String eventDesc;


    /**
     * 活动海报
     */
    private String eventPoster;

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventOrganizer() {
        return eventOrganizer;
    }

    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    public String getEventPerson() {
        return eventPerson;
    }

    public void setEventPerson(String eventPerson) {
        this.eventPerson = eventPerson;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }


    public String getEventPoster() {
        return eventPoster;
    }

    public void setEventPoster(String eventPoster) {
        this.eventPoster = eventPoster;
    }
}
