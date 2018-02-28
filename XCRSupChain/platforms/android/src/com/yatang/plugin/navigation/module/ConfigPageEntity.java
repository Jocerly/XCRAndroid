package com.yatang.plugin.navigation.module;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuping on 2017/10/24.
 */

public class ConfigPageEntity {
    private Map configPageMap;

    public ConfigPageEntity (){
        configPageMap = new ConcurrentHashMap<String,JSONObject>() ;
    }

    public  static void setConfigPage(Map moduleConfigPageMap,String moduleId,JSONObject jsonObject){
      if(!TextUtils.isEmpty(moduleId) ){
          moduleConfigPageMap.put(moduleId,jsonObject);
      }
    }

    public  static JSONObject getConfigPage(Map moduleConfigPageMap,String moduleId){
        JSONObject jsonObject = null;
        if(!TextUtils.isEmpty(moduleId) ){
            jsonObject = (JSONObject) moduleConfigPageMap.get(moduleId);
        }
        return jsonObject;
    }

    public  Map getConfigPageMap() {
        return configPageMap;
    }
}
