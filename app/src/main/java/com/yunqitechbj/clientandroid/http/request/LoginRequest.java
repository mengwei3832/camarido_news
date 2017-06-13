package com.yunqitechbj.clientandroid.http.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.yunqitechbj.clientandroid.entity.LoginInfo;
import com.yunqitechbj.clientandroid.http.HostUtil;
import com.yunqitechbj.clientandroid.http.response.NullObject;
import com.yunqitechbj.clientandroid.http.response.Response;

import java.lang.reflect.Type;

/**
 * Created by mengwei on 2016/10/28.
 */

public class LoginRequest extends IRequest {

    public LoginRequest(Context context,String userName,String passWord) {
        super(context);
        mParams.put("UserName", userName);
        mParams.put("ShortMsg", passWord);
    }

    @Override
    public String getUrl() {
        return HostUtil.getWebHost() + "api/Auth/Logon";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<LoginInfo, NullObject>>() {
        }.getType();
    }
}
