package com.yunqitechbj.clientandroid.http.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.yunqitechbj.clientandroid.entity.PushInfo;
import com.yunqitechbj.clientandroid.http.HostUtil;
import com.yunqitechbj.clientandroid.http.response.NullObject;
import com.yunqitechbj.clientandroid.http.response.Response;

import java.lang.reflect.Type;

/**
 * 推送相关请求
 * Created by mengwei on 2016/11/4.
 */

public class GetPushInfoRequest extends IRequest {

    public GetPushInfoRequest(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return HostUtil.getWebHost() + "api/Push/GetPushInfo";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<NullObject, PushInfo>>() {
        }.getType();
    }
}
