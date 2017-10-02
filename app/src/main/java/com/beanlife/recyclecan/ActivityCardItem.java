package com.beanlife.recyclecan;

/**
 * Created by Java on 2017/9/4.
 */

public class ActivityCardItem {
    private int actImg;
    private String actName, actDate;

    public ActivityCardItem(int actImg, String actName, String actDate) {
        this.actImg = actImg;
        this.actName = actName;
        this.actDate = actDate;
    }

    public int getActImg() {
        return actImg;
    }

    public void setActImg(int actImg) {
        this.actImg = actImg;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getActDate() {
        return actDate;
    }

    public void setActDate(String actDate) {
        this.actDate = actDate;
    }
}
