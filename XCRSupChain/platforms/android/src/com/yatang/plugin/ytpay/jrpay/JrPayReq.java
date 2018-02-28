package com.yatang.plugin.ytpay.jrpay;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by liuping on 2017/11/13.
 */

public class JrPayReq implements Serializable{


    public String jrPayReq ;
    public String async_url ;
    public String business_type;
    public String type ;
    public String order_amount ;
    public String datetime ;
    public String goods_name ;
    public String order_sn ;
    public String shop_user_name ;
    public String sync_url ;
    public String token ;
    public  String apiUrl ;
    public String user_name ;


    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    public String getJrPayReq() {
        return jrPayReq;
    }

    public void setJrPayReq(String jrPayReq) {
        this.jrPayReq = jrPayReq;
    }

    public String getAsync_url() {
        return async_url;
    }

    public void setAsync_url(String async_url) {
        this.async_url = async_url;
    }

    public String getBusiness_type() {
        return business_type;
    }

    public void setBusiness_type(String business_type) {
        this.business_type = business_type;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getShop_user_name() {
        return shop_user_name;
    }

    public void setShop_user_name(String shop_user_name) {
        this.shop_user_name = shop_user_name;
    }

    public String getSync_url() {
        return sync_url;
    }

    public void setSync_url(String sync_url) {
        this.sync_url = sync_url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String encode(String s){
        try {
          return   URLEncoder.encode(s, "utf-8")
                  .replaceAll("\\+","%20");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

    }

    public String parserRequsetParam(){
        StringBuffer stringBuffer = new StringBuffer();

        try {
            JSONObject jsonObject = new JSONObject(jrPayReq);
            Iterator iterator = jsonObject.keys();
            int fristIndex = 0;
            while(iterator.hasNext()){

                String  key = (String) iterator.next();
                String  value = jsonObject.getString(key);

                if(key.equals("apiUrl")){
                    continue;
                }

                if(fristIndex != 0){
                    stringBuffer.append("&");
                }
                stringBuffer.append(key).append("=").
                        append(encode(value));
                fristIndex ++ ;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return stringBuffer.toString();
        }
       return stringBuffer.toString();
    }

}
