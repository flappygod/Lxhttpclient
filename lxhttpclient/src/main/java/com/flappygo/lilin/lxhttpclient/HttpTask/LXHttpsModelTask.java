package com.flappygo.lilin.lxhttpclient.HttpTask;

import com.flappygo.lilin.lxhttpclient.HttpTask.Base.LXHttpsBaseTask;
import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

public abstract class LXHttpsModelTask<T> extends LXHttpsBaseTask<T> {

    private Gson gson = new Gson();

    /**************
     * 构造器
     *  @param url
     *            地址
     * @param hashMap
     */
    public LXHttpsModelTask(String url, HashMap<String, Object> hashMap) {
        super(url, hashMap);
    }

    private Type getTClass() {
        Type[] types=  ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        return types[0];
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public T preParseData(String data) throws Exception {
        try {
            Type mType=getTClass();
            //使用gson解析
            T t= gson.fromJson(data, mType);
            //解析成功
            parseSuccess(t);
            //返回
            return t;
        }catch (Exception ex){
            //失败
            throw ex;
        }
    }


    public abstract void parseSuccess(T t);

}
