package com.example.handformula;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.security.acl.Group;
import java.util.List;

public class KatexDecode {
    private String rawJson;
    private JSONObject json;
    private String KatexText;
    public KatexDecode(String rawJson) {
        this.rawJson = rawJson;
        this.json = JSONObject.parseObject(this.rawJson);
        String words_result = this.json.getString("words_result");
        List<JSONObject> words = JSON.parseArray(words_result, JSONObject.class);
        this.KatexText = "";
        for(JSONObject word : words){
            KatexText = KatexText + word.getString("words");
            System.out.println(word.getString("words"));
        }
        System.out.println(words_result);
    }
    public String getKatexText(){
        return this.KatexText;
    }

}
