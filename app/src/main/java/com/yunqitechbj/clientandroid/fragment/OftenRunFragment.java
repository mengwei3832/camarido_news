package com.yunqitechbj.clientandroid.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;

import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.activity.MainActivity;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.NetworkAvailable;
import com.yunqitechbj.clientandroid.utils.PreManager;
import com.yunqitechbj.clientandroid.weight.Config;
import com.yunqitechbj.clientandroid.weight.InitWebView;
import com.yunqitechbj.clientandroid.weight.MyWebChomeClient;
import com.yunqitechbj.clientandroid.weight.MyWebViewDownLoadListener;
import com.yunqitechbj.clientandroid.weight.SyncCookie;

/**
 * 常跑路线页面
 * Created by mengwei on 2017/5/19.
 */

public class OftenRunFragment extends BaseFragment implements MyWebChomeClient.OpenFileChooserCallBack {
    //页面控件对象
    private View progress_run;
    private View title_run;
    private TextView tvTitle;
    private WebView wv_run;

    private SyncCookie mSyncCookie;
    private InitWebView mInitWebView;

    private PreManager mPreManager;

    private String RUN_URI = Config.API.WEB_RUN_URL;
    
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_often_run;
    }

    @Override
    protected void initView(View _rootView) {
        progress_run = obtainView(R.id.progress_run);
        title_run = obtainView(R.id.title_run);
        wv_run = obtainView(R.id.wv_run);
        tvTitle = (TextView) title_run.findViewById(R.id.tv_title);

    }

    @Override
    protected void initData() {
        mPreManager = PreManager.instance(getActivity());
        mSyncCookie = new SyncCookie(getActivity());
        mInitWebView = new InitWebView(wv_run, getActivity());
        //初始化webview
        initWebView();
    }

    public boolean onBackPressed() {
        if (wv_run.canGoBack()) {
            wv_run.goBack();
            return true;
        }
        return false;
    }

    private void initWebView() {
        //webview初始化
        mInitWebView.initWebView();
        wv_run.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //cookie过期，js调用Android方法,window.back.goBackLogin() 的方式调用
        //        wv_run.addJavascriptInterface(new MainActivity.JsInteration(), "back");

        // 设置Web视图
        wv_run.setWebViewClient(new webViewClient());
        wv_run.setWebChromeClient(new MyWebChomeClient(this, tvTitle));
        wv_run.setDownloadListener(new MyWebViewDownLoadListener(getActivity()));

        // 加载需要显示的网页
        wv_run.loadUrl(RUN_URI, mSyncCookie.syncCookie(RUN_URI));
    }

    /**
     * Web视图
     */
    private class webViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progress_run.setVisibility(View.VISIBLE);
        }

        //        @Override
        //        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //            if ()
        //            return super.shouldOverrideUrlLoading(view, url);
        //        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            L.d("==== WebView ajax 请求 ====", url);
            mSyncCookie.syncCookie(url);
            //将加好cookie的url传给父类继续执行
            return super.shouldInterceptRequest(view, url);
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            L.e("------errorCode---------" + errorCode);
            //            rlShowMain.setVisibility(View.GONE);
            //            if (!NetworkAvailable.isNetworkAvailable(MainActivity.this)) {
            //                isErrorCode(NETWORK_FAILED);
            //            } else {
            //                isErrorCode(errorCode);
            //            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progress_run.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            //            super.onReceivedSslError(webView, sslErrorHandler, sslError);
            sslErrorHandler.proceed();
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {

    }

    @Override
    public boolean openFileChooserCallBackAndroid5(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        return false;
    }
}
