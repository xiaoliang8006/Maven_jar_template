package com.demo;


import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class HelloGson {
    public static void main(String[] args) {

        Gson gson = new Gson();
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        String json = gson.toJson(map);
        System.out.println(json);
    }
}
