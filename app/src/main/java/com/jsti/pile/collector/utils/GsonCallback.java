package com.jsti.pile.collector.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lzy.okgo.callback.AbsCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract  class GsonCallback<T> extends AbsCallback<T> {

    public GsonCallback() {
    }

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {
        String s = response.body().string();
        Type genericType = this.getClass().getGenericSuperclass();
        ParameterizedType type = (ParameterizedType) genericType;
        Type[] genericTypes = type.getActualTypeArguments();

        Class<T> tClass = (Class<T>) genericTypes[0];
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls(); //重点
        Gson gson = gsonBuilder.create();
        T tClassa = gson.fromJson(s, tClass);
        return tClassa;
    }

}
