package com.lg.eventbus;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

public class TestSend {
    public static void main(String[] a) throws InterruptedException, IOException {
        JSONObject obj = new JSONObject();
        obj.put("mac", "00:01:7a:70:4d:64");
        obj.put("profit", 4);
        EventBus.getInstance().post(new Event("WECHAT", obj));
//        EventBus.getInstance().senderr();
//        EventBus.getInstance().post(new Event("type1", 111));
//        EventBus.getInstance().post(new Event("type1", 222));
//        EventBus.getInstance().post(new Event("type1", 333));
//        EventBus.getInstance().post(new Event("type1", 444));
//        EventBus.getInstance().post(new Event("INSTALL", "6038971"));
    }

}
