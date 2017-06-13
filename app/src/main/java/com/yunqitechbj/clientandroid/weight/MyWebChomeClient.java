package com.yunqitechbj.clientandroid.weight;

import android.app.Activity;
import android.net.Uri;
import android.widget.TextView;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.ValueCallback;
import com.yunqitechbj.clientandroid.utils.StringUtils;

/**
 * Created by mengwei on 2016/11/2.
 */

public class MyWebChomeClient extends WebChromeClient {

    private OpenFileChooserCallBack mOpenFileChooserCallBack;
    private TextView mActivity;

    public MyWebChomeClient(OpenFileChooserCallBack openFileChooserCallBack,TextView activity) {
        mOpenFileChooserCallBack = openFileChooserCallBack;
        mActivity = activity;
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        mOpenFileChooserCallBack.openFileChooserCallBack(uploadMsg, acceptType);
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
    }

    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        return mOpenFileChooserCallBack.openFileChooserCallBackAndroid5(webView, filePathCallback, fileChooserParams);
    }

    @Override
    public void onReceivedTitle(WebView webView, String s) {
        if (StringUtils.isContainChinese(s)){
            mActivity.setText(s);
        } else {
            mActivity.setText("");
        }
        super.onReceivedTitle(webView, s);
    }

    public interface OpenFileChooserCallBack {
        // for API - Version below 5.0.
        void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType);

        // for API - Version above 5.0 (contais 5.0).
        boolean openFileChooserCallBackAndroid5(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                                FileChooserParams fileChooserParams);
    }
}

