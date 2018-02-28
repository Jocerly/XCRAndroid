package com.yatang.xc.xcr.uitls;


import android.content.Context;

import com.yatang.xc.xcr.config.Config;
import com.yatang.xc.xcr.config.Constants;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 解析所有从数据库返回的json数据
 *
 * @author Jocerly
 */
public class DataAnalyze {
    /**
     * 具体的解析
     *
     * @param result
     * @return
     */
    public static ResultParam doAnalyze(String result) {
        JCLoger.debug(result);
        ResultParam resultParam = ResultParam.getInstance();
        try {
            if (!StringUtils.isEmpty(result)) {
                JSONObject json = new JSONObject(result);
                resultParam.resultId = json.getJSONObject("Status").getString("StateValue");
                resultParam.message = json.getJSONObject("Status").getString("StateDesc");

                if (!json.isNull("mapdata")) {
                    resultParam.mapData = doAnalyzeJsonArray(json.getJSONObject("mapdata"));
                }
                if (!json.isNull("listdata")) {
                    JSONObject jsonListData = json.getJSONObject("listdata");
                    if (jsonListData.has("rows") && jsonListData.get("rows") != null) {
                        JSONArray array = new JSONArray(jsonListData.getString("rows"));
                        int length = array.length();
                        for (int i = 0; i < length; i++) {
                            resultParam.listData.add(doAnalyzeJsonArray(array.getJSONObject(i)));
                        }
                    }

                    if(jsonListData.has("totalpage") && jsonListData.get("totalpage") != null) {
                        resultParam.totalpage = Integer.parseInt(String.valueOf(jsonListData.get("totalpage")));
                    }
                }
            } else {
                resultParam.resultId = Constants.M98;
                resultParam.message = "服务器繁忙，请稍后再试！";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultParam;
    }

    /**
     * 具体的解析
     * @param context
     * @param method
     * @param result
     * @return
     */
    public static ResultParam doAnalyze(Context context, String method, String result) {
        JCLoger.debug(result);
        ResultParam resultParam = ResultParam.getInstance();
        try {
            if (!StringUtils.isEmpty(result)) {
                JSONObject json = new JSONObject(result);
                resultParam.resultId = json.getJSONObject("Status").getString("StateValue");
                resultParam.message = json.getJSONObject("Status").getString("StateDesc");

                if (!json.isNull("mapdata")) {
                    JSONObject jsonMap = null;
                    if ("System/Login".equals(method)) {
                        if (StringUtils.isEmpty(Config.customerKey)) {
                            Config.customerKey = SerializUtil.deSerializ(Common.getAppInfo(context, "b", "b")).toString();
                        }
                        String mapStr = DESEncrype.decryptDES(json.getString("mapdata"), Config.customerKey);
                        jsonMap = new JSONObject(mapStr);
                    } else {
                        jsonMap = json.getJSONObject("mapdata");
                    }
                    resultParam.mapData = doAnalyzeJsonArray(jsonMap);
                }
                if (!json.isNull("listdata")) {
                    JSONObject jsonListData = json.getJSONObject("listdata");
                    if (jsonListData.has("rows") && jsonListData.get("rows") != null) {
                        JSONArray array = new JSONArray(jsonListData.getString("rows"));
                        int length = array.length();
                        for (int i = 0; i < length; i++) {
                            resultParam.listData.add(doAnalyzeJsonArray(array.getJSONObject(i)));
                        }
                    }
                    
                    if(jsonListData.has("totalpage") && jsonListData.get("totalpage") != null) {
                        resultParam.totalpage = Integer.parseInt(String.valueOf(jsonListData.get("totalpage")));
                    }
                }
            } else {
                resultParam.resultId = Constants.M98;
                resultParam.message = "服务器繁忙，请稍后再试！";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultParam;
    }

    /**
     * 解析具体的json
     *
     * @param jsonMap
     * @return
     */
    public static ConcurrentHashMap<String, String> doAnalyzeJsonArray(JSONObject jsonMap) {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        try {
            JSONArray nameArray = jsonMap.names();
            int objLength = nameArray.length();
            for (int j = 0; j < objLength; j++) {
                String temp = StringUtils.replaceNULLToStr(jsonMap.getString(nameArray.get(j).toString()));
                map.put(nameArray.get(j).toString(), StringUtils.isEmpty(temp) ? "" : temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * str转列表数据
     *
     * @param str
     * @return
     */
    public static ArrayList<ConcurrentHashMap<String, String>> strToArrayList(String str) {
        ArrayList<ConcurrentHashMap<String, String>> listData = new ArrayList<ConcurrentHashMap<String, String>>();
        try {
            JSONArray array = new JSONArray(str);
            int length = array.length();
            for (int i = 0; i < length; i++) {
                listData.add(doAnalyzeJsonArray(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listData;
    }
}
