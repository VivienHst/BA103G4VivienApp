package com.beanlife.act;

import java.io.Serializable;

/**
 * Created by vivienhuang on 2017/9/30.
 */

public class Fo_actVO implements Serializable {
    private String act_no;
    private String mem_ac;
    private String fo_act_date;

    public String getAct_no() {
        return act_no;
    }

    public void setAct_no(String act_no) {
        this.act_no = act_no;
    }

    public String getMem_ac() {
        return mem_ac;
    }

    public void setMem_ac(String mem_ac) {
        this.mem_ac = mem_ac;
    }

    public String getFo_act_date() {
        return fo_act_date;
    }

    public void setFo_act_date(String fo_act_date) {
        this.fo_act_date = fo_act_date;
    }
}
