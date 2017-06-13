package com.yunqitechbj.clientandroid.http.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.yunqitechbj.clientandroid.entity.QuoteModel;
import com.yunqitechbj.clientandroid.http.HostUtil;
import com.yunqitechbj.clientandroid.http.response.NullObject;
import com.yunqitechbj.clientandroid.http.response.Response;

import java.lang.reflect.Type;

/**
 * 获取报价单列表请求
 * Created by mengwei on 2016/12/29.
 */

public class QuoteListRequest extends IRequest {

    public QuoteListRequest(Context context,int pageIndex,int pageSize) {
        super(context);
        mParams.put("PageIndex",pageIndex);
        mParams.put("PageSize",pageSize);
    }

    @Override
    public String getUrl() {
        return HostUtil.getWebHost() + "api/Auth/GetVquotationList";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<NullObject, QuoteModel>>() {
        }.getType();
    }
}
