package com.yunqitechbj.clientandroid.http.request;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.RequestParams;
import com.yunqitechbj.clientandroid.http.HostUtil;

import java.lang.reflect.Type;

/**
 * 网络请求的基类
 * Created by mengwei on 2016/10/25.
 */

public abstract class IRequest {
    protected Context mContext;
    protected RequestParams mParams = new RequestParams();
    private int mRequestId = 0;
    private String mChooseIdentity;
    private String urlRequest;

    // 解决json字符串序列化问题，使用表单获取
    private boolean isUseFormRequest = false;

    public IRequest(Context context) {
        mContext = context;
    }

    /**
     * 接口请求参数
     *
     * @return
     * @throws
     * @Title:getParams
     * @return:RequestParams
     * @Create: 2015年11月19日 下午11:54:51
     * @Author : zhm
     */
    public RequestParams getParams() {
        return mParams;
    }

    /**
     * http直接post数据
     *
     * @return
     * @throws
     * @Description:
     * @Title:getPostData
     * @return:String
     * @Create: 2015年11月20日 上午12:02:54
     * @Author : zhm
     */
    public String getPostData() {
        return null;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 设置接口请求唯一标识
     *
     * @param requestId
     * @throws
     * @Description:
     * @Title:setRequestId
     * @return:void
     * @Create: 2015年11月19日 下午11:56:32
     * @Author : zhm
     */
    public void setRequestId(int requestId) {
        mRequestId = requestId;
    }

    /**
     * 返回请求接口唯一标识
     *
     * @return
     * @throws
     * @Description:
     * @Title:getRequestId
     * @return:int
     * @Create: 2015年11月19日 下午11:56:43
     * @Author : zhm
     */
    public int getRequestId() {
        return mRequestId;
    }

    /**
     * @return
     * @throws
     * @Description:当前接口的url地址
     * @Title:getUrl
     * @return:String
     * @Create: 2015年11月19日 下午11:53:21
     * @Author : zhm
     */
    public abstract String getUrl();

    /**
     * 获取解析类型
     *
     * @return
     * @throws
     * @Description:
     * @Title:getParserType
     * @return:Type
     * @Create: 2015年11月20日 上午12:02:38
     * @Author : zhm
     */
    public abstract Type getParserType();

    /**
     * @return
     * @throws
     * @Description:返回服务器接口地址
     * @Title:getHost
     * @return:String
     * @Create: 2015年11月19日 下午11:54:28
     * @Author : zhm
     */
    protected String getHost() {
        urlRequest = HostUtil.getApiHost();
        return urlRequest;
    }

    public boolean isUseFormRequest() {
        return isUseFormRequest;
    }

    public void setUseFormRequest(boolean isUseFormRequest) {
        this.isUseFormRequest = isUseFormRequest;
    }
}
