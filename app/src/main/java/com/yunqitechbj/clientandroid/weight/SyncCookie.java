package com.yunqitechbj.clientandroid.weight;

import android.content.Context;
import android.text.TextUtils;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;

import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.PreManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 把Cookie同步到webview
 * Created by mengwei on 2016/11/2.
 */

public class SyncCookie {
    private Context context;
    private PreManager preManager;

    public SyncCookie(Context context) {
        super();
        this.context = context;
        preManager = PreManager.instance(context);
    }

    public Map<String, String> syncCookie(String url){
        //获取cookies
        String cookies = preManager.getToken();
        try {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();
            L.e("---------保存mCookies------------"+cookies);
            String cookieString = null;
            if (cookies != null && !TextUtils.isEmpty(cookies)){
                cookieString = String.format("YQ_API_TOKEN=%s", cookies);
            }

            cookieManager.setCookie(url, cookieString);
            CookieSyncManager.getInstance().sync();
            Map<String, String> abc = new HashMap<String, String>();
            L.e("---------应用cookieString------------"+cookieString);
            abc.put("Cookie", cookieString);
            return abc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
