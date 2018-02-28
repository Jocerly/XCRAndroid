package com.yatang.plugin.navigation.module;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuping on 2017/10/19.
 */

/**
 * HashMap ==>  key : moduleId  ,value: Module
 */
public class ModuleEntity implements Serializable {

     Map<String,Module> moduleMap;

   public   ModuleEntity (){
       moduleMap = new ConcurrentHashMap<String,Module>() ;
    }

    public synchronized  static String getJsonStringByEntity(Context context, ModuleEntity object) {
        String strJson = "";
        Gson gson = new Gson();
        strJson = gson.toJson(object);
        return  strJson;
    }

    /**
     * 读取播放列表数据
     * @param context
     * @return
     */
    public synchronized static ModuleEntity getfromJson(Context context , String str){
        ModuleEntity entity = new ModuleEntity();
        if(!TextUtils.isEmpty(str)){
            Gson gson=new Gson();
            entity = gson.fromJson(str, new TypeToken<ModuleEntity>(){}.getType());
        }
        return entity;
    }

    public synchronized Map<String, Module> getModuleMap() {
        return moduleMap;
    }

    public synchronized void updateModuleMap(String moduelName,Module module){
        getModuleMap().put(moduelName,module);
    }

    public synchronized void removeModuleMap(String moduelName){
        getModuleMap().remove(moduelName);
    }

    public synchronized void setModuleMap(Map<String, Module> moduleMap) {
        this.moduleMap = moduleMap;
    }

}
