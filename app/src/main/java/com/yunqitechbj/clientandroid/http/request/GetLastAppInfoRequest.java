package com.yunqitechbj.clientandroid.http.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.yunqitechbj.clientandroid.entity.GetLastAppInfo;
import com.yunqitechbj.clientandroid.http.HostUtil;
import com.yunqitechbj.clientandroid.http.response.NullObject;
import com.yunqitechbj.clientandroid.http.response.Response;

import java.lang.reflect.Type;

/**
 * 获取服务器版本更新请求
 * Created by mengwei on 2016/11/4.
 */

public class GetLastAppInfoRequest extends IRequest {

    public GetLastAppInfoRequest(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return HostUtil.getWebHost() + "api/App/GetLastAppInfo";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<GetLastAppInfo, NullObject>>() {
        }.getType();
    }
}
