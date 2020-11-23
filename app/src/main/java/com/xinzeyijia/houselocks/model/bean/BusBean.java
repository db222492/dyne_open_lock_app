package com.xinzeyijia.houselocks.model.bean;

/**
 * Created by EDZ on 2018/12/27.
 * 此类用来传递不同类型的对象
 */

public class BusBean {
    private String type;
    private Object t;

    public BusBean(String type, Object t) {
        this.type = type;
        this.t = t;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getT() {
        return   t;
    }

    public void setT(Object t) {
        this.t = t;
    }

}
