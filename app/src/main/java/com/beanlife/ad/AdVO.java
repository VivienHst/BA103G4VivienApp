package com.beanlife.ad;

import java.io.Serializable;

/**
 * Created by vivienhuang on 2017/10/7.
 */

public class AdVO implements Serializable {
    private String ad_no;
    private String prod_no;
    private String ad_title;
    private String ad_op_date;
    private String ad_ed_date;

    public String getAd_no() {
        return ad_no;
    }

    public void setAd_no(String ad_no) {
        this.ad_no = ad_no;
    }

    public String getProd_no() {
        return prod_no;
    }

    public void setProd_no(String prod_no) {
        this.prod_no = prod_no;
    }

    public String getAd_title() {
        return ad_title;
    }

    public void setAd_title(String ad_title) {
        this.ad_title = ad_title;
    }

    public String getAd_op_date() {
        return ad_op_date;
    }

    public void setAd_op_date(String ad_op_date) {
        this.ad_op_date = ad_op_date;
    }

    public String getAd_ed_date() {
        return ad_ed_date;
    }

    public void setAd_ed_date(String ad_ed_date) {
        this.ad_ed_date = ad_ed_date;
    }
}
