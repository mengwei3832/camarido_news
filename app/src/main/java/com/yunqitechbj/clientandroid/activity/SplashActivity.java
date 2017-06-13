package com.yunqitechbj.clientandroid.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.entity.GetLastAppInfo;
import com.yunqitechbj.clientandroid.http.request.GetLastAppInfoRequest;
import com.yunqitechbj.clientandroid.http.response.Response;
import com.yunqitechbj.clientandroid.utils.CommonUtil;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.NumberSeekBar;
import com.yunqitechbj.clientandroid.utils.PreManager;
import com.yunqitechbj.clientandroid.utils.StringUtils;
import com.yunqitechbj.clientandroid.utils.T;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity {
    private PreManager mPreManager;

    /* 请求类 */
    private GetLastAppInfoRequest mGetLastAppInfoRequest;

    /* 请求ID */
    private final int UPDATE_APP = 1;

    /* 获取的版本更新信息 */
    private int appVersion;
    private String appDownloadUrl;
    private String appDescription;

    /* 自动跟新弹窗对象 */
    private TextView tvDesc;
    private Button btnCancel,btnSure;
    private LinearLayout llBottom;
    private NumberSeekBar seekBar;
    private DownLoadListener listener;
    private AlertDialog alertDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        mPreManager = PreManager.instance(this);

        L.e("------------进入-----------------");

        //判断是否是第一次启动
        isFirstInput();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    /**
     * 判断是否是第一次启动
     */
    public void isFirstInput() {
        L.e("-----判断是否是第一次启动--------" + mPreManager.getFirstStart());
        if (mPreManager.getFirstStart()) {
            mPreManager.setFirstStart(false);
            //跳转到登录页面
            //            LoginActivity.invoke(SplashActivity.this);
            L.e("------------进入登陆了-----------------");
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            //关闭当前启动页
            SplashActivity.this.finish();
        } else {
            L.e("-----判断是否是自动登录--------" + mPreManager.getAutoLoginStatus());
            if (mPreManager.getAutoLoginStatus() && mPreManager.getIsLoginInput()) {
                String mToken = mPreManager.getToken();
                if (!StringUtils.isEmpty(mToken)){
                    // TODO 请求接口来判断有没有新版本
                    if (StringUtils.isNetworkConnected(mContext)){
                        updateVersion();
                    } else {
                        showOffline();
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    //关闭当前启动页
                    SplashActivity.this.finish();
                }
                //进入主页面
//                MainActivity.invoke(SplashActivity.this);
            } else {
                //进入登录页面
                LoginActivity.invoke(SplashActivity.this);
                //关闭当前启动页
                SplashActivity.this.finish();
            }
        }
    }

    /**
     * 访问服务器版本更新
     */
    private void updateVersion(){
        mGetLastAppInfoRequest = new GetLastAppInfoRequest(SplashActivity.this);
        mGetLastAppInfoRequest.setRequestId(UPDATE_APP);
        httpPost(mGetLastAppInfoRequest);
    }

    @Override
    public void onStart(int requestId) {
        super.onStart(requestId);
    }

    @Override
    public void onSuccess(int requestId, Response response) {
        super.onSuccess(requestId, response);
        boolean isSuccess;
        String message;
        switch (requestId){
            case UPDATE_APP:
                isSuccess = response.isSuccess;
                message = response.message;
                if (isSuccess){
                    // 获取版本号成功
                    GetLastAppInfo getLastAppInfo = (GetLastAppInfo) response.singleData;
                    appVersion = getLastAppInfo.appVersion;// 服务器返回的apk的版本号
                    appDownloadUrl = getLastAppInfo.appDownloadUrl;// 服务器返回的apk的url
                    appDescription = getLastAppInfo.appDescription;// 服务器返回的apk的更新描述

                    // 获取现有apk版本的版本号
                    int currentCode = CommonUtil.getVersionCode();

                    // 判断现有apk的版本号和服务器返回的版本号大小
                    if (currentCode < appVersion) {
                        // 现有版本号小于服务器返回的版本号--更新版本

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                SplashActivity.this);
                        // 设置对话框不能被取消
                        builder.setCancelable(false);

                        View view = View.inflate(SplashActivity.this,
                                R.layout.dialog_download, null);
                        tvDesc = (TextView) view.findViewById(R.id.desc);
                        llBottom = (LinearLayout) view.findViewById(R.id.bottom);
                        btnSure = (Button) view.findViewById(R.id.btn_ok);
                        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
                        seekBar = (NumberSeekBar) view.findViewById(R.id.bar0);

                        seekBar.setTextColor(Color.WHITE);
                        seekBar.setTextSize(30);
                        seekBar.setMax(100);

                        // 设置更新提示信息
                        if (!TextUtils.isEmpty(appDescription)
                                && appDescription != null) {
                            SplashActivity.this.setDesc(appDescription.replace(
                                    "\\n", "\n"));
                        }

                        // 监听下载进度
                        SplashActivity.this
                                .setDownLoadListener(new DownLoadListener() {
                                    @Override
                                    public void start(int max) {
                                        // TODO Auto-generated method stub
                                    }

                                    @Override
                                    public void finish() {
                                        // TODO Auto-generated method stub
                                    }

                                    @Override
                                    public void downLoad() {
                                        // 下载安装apk
                                        if (!TextUtils.isEmpty(appDownloadUrl)
                                                && appDownloadUrl != null) {
                                            downLoadAndInstall(appDownloadUrl);
                                        }
                                    }

                                    @Override
                                    public void cancel(int progress) {
                                        // TODO Auto-generated method stub
                                    }
                                });

                        // 点击取消按钮
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        btnSure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                llBottom.measure(0, 0);
                                int height = llBottom.getMeasuredHeight();

                                ValueAnimator animWidth = ValueAnimator.ofInt(
                                        height, 0);
                                animWidth
                                        .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                                            @Override
                                            public void onAnimationUpdate(
                                                    ValueAnimator valueAnimator) {
                                                int value = (Integer) valueAnimator
                                                        .getAnimatedValue();
                                                btnSure.getLayoutParams().height = value;
                                                btnCancel.getLayoutParams().height = value;
                                                btnSure.requestLayout();
                                                btnCancel.requestLayout();
                                            }
                                        });

                                animWidth.setDuration(300);
                                animWidth.start();

                                ValueAnimator animator = ValueAnimator.ofInt(0,
                                        height);

                                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                                    @Override
                                    public void onAnimationUpdate(
                                            ValueAnimator valueAnimator) {
                                        int value = (Integer) valueAnimator
                                                .getAnimatedValue();
                                        seekBar.getLayoutParams().height = value;
                                        seekBar.requestLayout();
                                    }
                                });

                                animator.setDuration(300);
                                animator.start();

                                if (listener != null) {
                                    listener.start(seekBar.getMax());
                                    listener.downLoad();
                                }

                            }
                        });

                        alertDialog = builder.create();
                        alertDialog.setView(view, 0, 0, 0, 0);
                        alertDialog.show();

                    } else {
                        // 现有版本号不小于服务器返回的版本号不更新版本--跳转到主界面
                        begin();
                        SplashActivity.this.finish();
                    }
                    //关闭当前启动页
//
                } else {
                    // 获取版本号失败--跳转到主界面
                    begin();
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestId, int httpCode, Throwable error) {
        super.onFailure(requestId, httpCode, error);
        // 请求失败处理--跳转到登录界面
        LoginActivity.invoke(SplashActivity.this);
        // 关闭启动页
        SplashActivity.this.finish();
    }

    private void begin(){
        MainActivity.invoke(SplashActivity.this,0);
    }


    public void setDesc(String desc) {
        this.tvDesc.setText(desc);
    }

    public void setMax(int max) {
        seekBar.setMax(max);
    }

    public void setProgres(int progress) {
        seekBar.setProgress(progress);
    }

    public int getProgres() {
        return seekBar.getProgress();
    }

    public void setDownLoadListener(DownLoadListener listener) {
        this.listener = listener;
    }

    /**
     * 下载并安装新版本
     *
     * @param downUrl
     */
    private void downLoadAndInstall(final String downUrl) {

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            T.showShort(SplashActivity.this,"没有检测到存储设备");
            return;
        }

        // 获取下载路径
        File sdCardFile = Environment.getExternalStorageDirectory();
        File file = new File(sdCardFile, "yunqitech");
        if (!file.exists()) {
            file.mkdirs();
        }

        // 开始下载
        if (TextUtils.isEmpty(downUrl)) {
            return;
        }
        new Thread() {
            public void run() {
                // 下载apk的url
                download(downUrl);
            };
        }.start();
    }

    /**
     * 下载最新版本
     */
    protected void download(final String downUrl) {
        File rootFile = Environment.getExternalStorageDirectory();
        File file = new File(rootFile, "yunqitech/download");

        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            URL url = new URL(downUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);

            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(new File(file,
                        "camarido.apk"));

                byte[] buf = new byte[1024];

                int len = -1;
                int total = conn.getContentLength();

                // 设置加载进度数
                SplashActivity.this.setMax(total / 10);

                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    fos.flush();
                    // 设置加载进度
                    SplashActivity.this.setProgres(SplashActivity.this
                            .getProgres() + len / 10);
                }

                fos.close();
                is.close();
                conn.disconnect();
                // 取消对话框
                alertDialog.dismiss();
                // 安装apk
                installApk();
                // 关闭当前页面
                SplashActivity.this.finish();

            } else {
                // 下载失败
                alertDialog.dismiss();
                // 显示一个重试的dialog
                showTryAgainDialog(downUrl);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            // 显示一个重试的dialog
            showTryAgainDialog(downUrl);
        } catch (IOException e) {
            e.printStackTrace();
            // 显示一个重试的dialog
            showTryAgainDialog(downUrl);
        }

    }

    /**
     * 显示一个重试的dialog
     *
     * @param downUrl
     */
    private void showTryAgainDialog(final String downUrl) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setCancelable(false);
                builder.setTitle("提示");

                builder.setMessage("下载失败，请重试");

                builder.setPositiveButton("重试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 下载安装apk
                        downLoadAndInstall(downUrl);
                    }
                });

                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                // 进入主界面
                                begin();
                                //
                                SplashActivity.this.finish();
                            }
                        });

                builder.create().show();

            }
        });

    }

    /**
     * 安装apk
     */
    protected void installApk() {
        /**
         * <intent-filter> <action android:name="android.intent.action.VIEW" />
         * <category android:name="android.intent.category.DEFAULT" /> <data
         * android:scheme="content" /> <data android:scheme="file" /> <data
         * android:mimeType="application/vnd.android.package-archive" />
         * </intent-filter>
         */
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");

        File rootFile = Environment.getExternalStorageDirectory();
        File file = new File(rootFile, "yunqitech/download/camarido.apk");

        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        // 当当前的activity退出的时候，会调用以前的activity的OnActivityResult方法
        startActivityForResult(intent, 0);
    }

    // 接收安装完apk后的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    // 更新监听回调的接口
    public interface DownLoadListener {
        // 开始下载
        void start(int max);

        // 取消
        void cancel(int progress);

        // 完成
        void finish();

        // 下载中
        void downLoad();
    }

    private void showOffline(){
        String mTokenExpires = mPreManager.getTokenExpires();
        String mCurrentTime = StringUtils.getCurrentDate();

        long t1 = StringUtils.getStringToDate(mTokenExpires);//token过期时间
        long t2 = StringUtils.getStringToDate(mCurrentTime);//当前时间

        L.e("--------token过期时间-----------"+t1);
        L.e("----------当前时间--------------"+t2);

        if (t1 < t2){
            //进入登录页面
            T.showShort(mContext,"登录信息失效，请重新登陆");
            LoginActivity.invoke(mContext);
            finish();
        } else {
            //进入离线派车界面
            T.showShort(mContext,"当前没有网络，进入离线派车页面");
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void initStatus(boolean isNet) {

    }
}
