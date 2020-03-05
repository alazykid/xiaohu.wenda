package com.hlh.wenda.Util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

public class WendaUtil {
    private static final Logger logger = LoggerFactory.getLogger(WendaUtil.class);

    public static int ANONYMOUS_USERID = 521;

    public static String getJSONString(int code){
        JSONObject json = new JSONObject();
        json.put("code",code);
        return  json.toJSONString();
    }
    public static String getJSONString(int code,String msg){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return  json.toJSONString();
    }


    public static String MD5(String key){
        char hexDigits[] = {
                '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
        };

        try{
            byte[] btInput = key.getBytes();

            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            mdInst.update(btInput);

            byte[] md = mdInst.digest();

            int j = md.length;
            char str[] = new char[j*2];
            int k = 0;
            for(int i = 0;i < j;i++){
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }catch (Exception e){
            logger.error("生成MD5失败",e);
            return null;
        }
    }

}
