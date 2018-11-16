package com.lg.eventbus;

import com.alibaba.fastjson.JSON;

public class Event {

    private String type;

    private Object payload;

    public Event(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return new StringBuilder(type).append(":").append(JSON.toJSONString(payload)).toString();
    }
}
