package com.yunqitechbj.clientandroid;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.MiPushUtil;

import org.litepal.LitePal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by mengwei on 2016/10/25.
 */

public class CamaridoApp extends Application {
    // 云启新增销毁队列
    private static Map<String, Activity> destoryMap = new HashMap<String, Activity>();

    private static Context context;// 全局Context变量
    private static Handler mainHandler;// 主线程的handler
    public static CamaridoApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        // 全局异常处理函数
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

//        /* 开启网络广播监听 */
//        NetStateReceiver.registerNetworkStateReceiver(this);

        //初始化先X5浏览器内核
        initX5Webview();

        context = this;
        mainHandler = new Handler();

        //小米推送初始化
        MiPushUtil.initPush(this);

        //Litepal数据库初始化
        LitePal.initialize(this);

        //LogCat初始化
        L.isDebug = true;

        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
    }

    /**
     * 初始化先X5浏览器内核
     */
    private void initX5Webview(){
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                L.e("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub

            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                L.d("app","onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
                L.d("app","onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
                L.d("app","onDownloadProgress:"+i);
            }
        });

        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    /**
     * 添加到销毁队列
     *
     * @param activity
     *            要销毁的activity
     */

    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    /**
     * 销毁指定Activity
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        for (String key : keySet) {
            destoryMap.get(key).finish();
        }
    }

    /**
     * 获取指定Activity
     */
    public static Activity getActivity(String activityName) {
        return destoryMap.get(activityName);
    }

    // 获取上下文对象
    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return mainHandler;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


}
