package com.yunqitechbj.clientandroid.weight;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.tencent.smtt.sdk.DownloadListener;

import com.yunqitechbj.clientandroid.utils.L;

/**
 * Created by mengwei on 2016/11/3.
 */

public class MyWebViewDownLoadListener implements DownloadListener {
    private Context mContext;

    public MyWebViewDownLoadListener(Context context) {
        mContext = context;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        L.i("tag", "url="+url);
        L.i("tag", "userAgent="+userAgent);
        L.i("tag", "contentDisposition="+contentDisposition);
        L.i("tag", "mimetype="+mimetype);
        L.i("tag", "contentLength="+contentLength);
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(intent);
    }
}
