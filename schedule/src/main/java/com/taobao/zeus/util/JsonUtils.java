package com.taobao.zeus.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by yxl on 2017/9/16.
 */
public class JsonUtils {

    public static String objectToJson(Object o){
        Gson gson = new GsonBuilder() .disableHtmlEscaping().create();
        return gson.toJson(o) ;
    }

    public static <T> T stringToObject(String json,Class<T> cls){
        Gson gson = new Gson();
        return gson.fromJson(json,cls);
    }
}
