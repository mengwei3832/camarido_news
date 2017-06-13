package com.yunqitechbj.clientandroid.weight;

import android.content.Context;
import android.view.View;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebSettings;

/**
 * webview初始化
 * Created by mengwei on 2016/11/2.
 */

public class InitWebView {
    private WebView wv;
    private Context context;
    public InitWebView(WebView wv, Context context) {
        super();
        this.wv = wv;
        this.context = context;
    }

    public void initWebView(){
        wv.getSettings().setUserAgentString("kameiliduo");
        // 设置Web视图
        // 设置可以访问文件
        wv.getSettings().setAllowFileAccess(true);
        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setAppCacheEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setDatabaseEnabled(true);
    }
}
