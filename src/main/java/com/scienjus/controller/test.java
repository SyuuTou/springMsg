package com.scienjus.controller;

import com.alibaba.fastjson.JSONObject;

public class test {
    public static void main(String[] args) {
        String str= "{\"Message\":\"OK\",\"RequestId\":\"08038FDD-2609-4435-804A-E385F6CDB90B\",\"BizId\":\"284300932005454230^0\",\"Code\":\"OK\"}";
        JSONObject jsonObject = JSONObject.parseObject(str);
        System.out.println(jsonObject);
        System.out.println(jsonObject.getString("Message")+";"+jsonObject.getString("Code")+";"+jsonObject.getString("RequestId")+";"+jsonObject.getString("BizId")+";");

    }
}
