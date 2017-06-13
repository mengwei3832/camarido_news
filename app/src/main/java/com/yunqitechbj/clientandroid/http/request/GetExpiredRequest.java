package com.yunqitechbj.clientandroid.http.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.yunqitechbj.clientandroid.entity.ExpiredModel;
import com.yunqitechbj.clientandroid.http.HostUtil;
import com.yunqitechbj.clientandroid.http.response.NullObject;
import com.yunqitechbj.clientandroid.http.response.Response;

import java.lang.reflect.Type;

/**
 * 获取展示数据是否已过期请求
 * Created by mengwei on 2017/3/27.
 */

public class GetExpiredRequest extends IRequest {
    private String ticketIds;

    public GetExpiredRequest(Context context,String ticketIds) {
        super(context);
        this.ticketIds = ticketIds;
        mParams.put("ticketIds",ticketIds);
    }

    @Override
    public String getUrl() {
        return HostUtil.getWebHost() + "api/Auth/GetTicketOperationHis?ticketIds="+ticketIds;
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<NullObject, ExpiredModel>>() {
        }.getType();
    }
}
