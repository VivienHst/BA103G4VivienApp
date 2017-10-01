package com.beanlife.act;

import java.io.Serializable;

/**
 * Created by vivienhuang on 2017/9/30.
 */

public class Act_pairVO implements Serializable {
    private String act_no;
    private String mem_ac;
    private String apply_date;
    private String pay_state;
    private String chk_state;

    public Act_pairVO() {
        super();
    }

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

    public String getApply_date() {
        return apply_date;
    }

    public void setApply_date(String apply_date) {
        this.apply_date = apply_date;
    }

    public String getPay_state() {
        return pay_state;
    }

    public void setPay_state(String pay_state) {
        this.pay_state = pay_state;
    }

    public String getChk_state() {
        return chk_state;
    }

    public void setChk_state(String chk_state) {
        this.chk_state = chk_state;
    }
}
