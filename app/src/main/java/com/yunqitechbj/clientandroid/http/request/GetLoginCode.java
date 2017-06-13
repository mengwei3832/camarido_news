package com.yunqitechbj.clientandroid.http.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.yunqitechbj.clientandroid.http.HostUtil;
import com.yunqitechbj.clientandroid.http.response.NullObject;
import com.yunqitechbj.clientandroid.http.response.Response;

import java.lang.reflect.Type;

/**
 * 获取登录验证码请求
 * Created by mengwei on 2016/11/24.
 */

public class GetLoginCode extends IRequest {
    private String call;

    public GetLoginCode(Context context, String call) {
        super(context);
        this.call = call;
        mParams.put("UserName",call);
    }

    @Override
    public String getUrl() {
        return HostUtil.getWebHost() + "api/Auth/GetLogonByPhoneMsg";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<NullObject, NullObject>>() {
        }.getType();
    }
}
