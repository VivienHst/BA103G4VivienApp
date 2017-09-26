package com.beanlife.recyclecan;
/**
 * Created by Java on 2017/9/3.
 */

public class CommentCardItem {
    private int memImg;
    private String memId;
    private String memLv;

    public CommentCardItem(int memImg, String memId, String memLv) {
        this.memImg = memImg;
        this.memId = memId;
        this.memLv = memLv;
    }

    public int getMemImg() {
        return memImg;
    }

    public void setMemImg(int memImg) {
        this.memImg = memImg;
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId;
    }

    public String getMemLv() {
        return memLv;
    }

    public void setMemLv(String memLv) {
        this.memLv = memLv;
    }
}