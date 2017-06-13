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
 * 企业钱包页面
 * Created by mengwei on 2017/5/19.
 */

public class WalletFragment extends BaseFragment implements MyWebChomeClient.OpenFileChooserCallBack {
    //页面控件对象
    private View progress_wallet;
    private View title_wallet;
    private TextView tvTitle;
    private WebView wv_wallet;

    private SyncCookie mSyncCookie;
    private InitWebView mInitWebView;
    
    private PreManager mPreManager;

    private String WALLET_URI = Config.API.WEB_WALLET_URL;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wallet;
    }

    @Override
    protected void initView(View _rootView) {
        progress_wallet = obtainView(R.id.progress_wallet);
        title_wallet = obtainView(R.id.title_wallet);
        wv_wallet = obtainView(R.id.wv_wallet);
        tvTitle = (TextView) title_wallet.findViewById(R.id.tv_title);

    }

    @Override
    protected void initData() {
        mPreManager = PreManager.instance(getActivity());
        mSyncCookie = new SyncCookie(getActivity());
        mInitWebView = new InitWebView(wv_wallet, getActivity());
        //初始化webview
        initWebView();
    }

    private void initWebView() {
        //webview初始化
        mInitWebView.initWebView();
        wv_wallet.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //cookie过期，js调用Android方法,window.back.goBackLogin() 的方式调用
//        wv_wallet.addJavascriptInterface(new MainActivity.JsInteration(), "back");

        // 设置Web视图
        wv_wallet.setWebViewClient(new webViewClient());
        wv_wallet.setWebChromeClient(new MyWebChomeClient(this, tvTitle));
        wv_wallet.setDownloadListener(new MyWebViewDownLoadListener(getActivity()));

        // 加载需要显示的网页
        wv_wallet.loadUrl(WALLET_URI, mSyncCookie.syncCookie(WALLET_URI));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // 加载需要显示的网页
        wv_wallet.loadUrl(WALLET_URI, mSyncCookie.syncCookie(WALLET_URI));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public boolean onBackPressed() {
        if (wv_wallet.canGoBack()) {
            wv_wallet.goBack();
            return true;
        }
        return false;
    }

    /**
     * Web视图
     */
    private class webViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progress_wallet.setVisibility(View.VISIBLE);
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
            progress_wallet.setVisibility(View.GONE);
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
