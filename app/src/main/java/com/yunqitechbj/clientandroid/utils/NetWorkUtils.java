package com.yunqitechbj.clientandroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * 网络状态判断
 * Created by mengwei on 2016/11/25.
 */

public class NetWorkUtils {
    public Context context = null;

    public NetWorkUtils(Context context) {
        this.context = context;
    }

    public static boolean isConnectNET(final Context context){
        final ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()){
            return true;
        } else {
            Toast.makeText(context,"断网了，请检查网络",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
