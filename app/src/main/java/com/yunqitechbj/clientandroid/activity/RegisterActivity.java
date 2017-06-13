package com.yunqitechbj.clientandroid.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.entity.LoginInfo;
import com.yunqitechbj.clientandroid.entity.PushInfo;
import com.yunqitechbj.clientandroid.http.request.GetPushInfoRequest;
import com.yunqitechbj.clientandroid.http.request.LoginRequest;
import com.yunqitechbj.clientandroid.http.response.Response;
import com.yunqitechbj.clientandroid.utils.ImageUtil;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.MiPushUtil;
import com.yunqitechbj.clientandroid.utils.NetworkAvailable;
import com.yunqitechbj.clientandroid.utils.PermissionUtil;
import com.yunqitechbj.clientandroid.utils.PreManager;
import com.yunqitechbj.clientandroid.utils.StringUtils;
import com.yunqitechbj.clientandroid.utils.T;
import com.yunqitechbj.clientandroid.weight.Config;
import com.yunqitechbj.clientandroid.weight.InitWebView;
import com.yunqitechbj.clientandroid.weight.MyWebChomeClient;
import com.yunqitechbj.clientandroid.weight.SyncCookie;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 注册页面
 * Created by mengwei on 2016/10/25.
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener,MyWebChomeClient.OpenFileChooserCallBack {
    /* 控件对象 */
//    private WebView mWvRegister;
    private WebView mWvRegister;
    private ImageView ivBack;
    private ProgressWheel progressBar;
    private RelativeLayout llError;
    private TextView tvErrorMessage;
    private TextView tvErrorDetail;
    private RelativeLayout rlHideWv;
    private TextView tvTitle;
    /* 初始化webview */
    private InitWebView mInitWebView;
    private SyncCookie mSyncCookie;

    /* 要加载地址 */
    private String REGISTER_URI = Config.API.WEB_REGISTER_URL;//注册URL
    private String REGISTER_IMPROVE = Config.API.WEB_IMPROVE_INFORMATION_URL;//注册完善信息URL

    /* web页面加载失败code */
    private final int NETWORK_FAILED = 1001;
    private final int COOKIE_FAILED = 401;
    private final int SERVER_FAILED = 404;

    /* 请求类 */
    private LoginRequest mLoginRequest;
    private GetPushInfoRequest mGetPushInfoRequest;

    /* 请求Id */
    private final int GET_LOGIN = 1;
    private final int GET_PUSH = 2;

    // 存放SP的key
    private final String TOKENVALUE = "TokenValue";
    public static final String USER_NAME = "USER_NAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String USER_TYPE = "USER_TYPE";

    //传递的数据
    private int status;
    private String tel;

    private PreManager mPreManager;

    private ValueCallback<Uri[]> mUploadMsgForAndroid5;
    private ValueCallback<Uri> mUploadMessage;
    private Intent mSourceIntent;

    // permission Code
    private static final int P_CODE_PERMISSIONS = 101;
    private static final int REQUEST_CODE_PICK_IMAGE = 0;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    /**
     * 初始化WebView
     */
    @Override
    protected void initView(){
        mWvRegister = obtainView(R.id.wv_register);
        ivBack = obtainView(R.id.iv_register_back);
        progressBar = obtainView(R.id.progress_register);
        llError = obtainView(R.id.ll_register_error);
        tvErrorDetail = obtainView(R.id.tv_error_detail);
        tvErrorMessage = obtainView(R.id.tv_error_message);
        rlHideWv = obtainView(R.id.rl_register_wv);
        tvTitle = obtainView(R.id.tv_register_title);

        //获取传递的数据
        status = getIntent().getIntExtra("status",0);
        tel = getIntent().getStringExtra("tel");

        L.e("----------status----------"+status);
        L.e("----------tel----------"+tel);

        mInitWebView = new InitWebView(mWvRegister,this);
        mSyncCookie = new SyncCookie(this);
        mPreManager = PreManager.instance(mContext);
    }

    @Override
    protected void initData() {
        llError.setVisibility(View.GONE);

        mInitWebView.initWebView();
        mWvRegister.getSettings().setUseWideViewPort(true);
        mWvRegister.getSettings().setLoadWithOverviewMode(true);
        //注册成功，由js调用Android的方法window.register.registerSuccess()
//        mWvRegister.addJavascriptInterface(new jsToAndroid(),"register");
        //注册 js 接口
        mWvRegister.setWebChromeClient(new MyWebChomeClient(this,tvTitle));
        mWvRegister.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                L.e("-----------加载进度条-------------------");
                showOrHideWeb(true);
            }

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
                L.e("-----错误码----errcode------------"+errorCode);
                rlHideWv.setVisibility(View.GONE);
                if (!NetworkAvailable.isNetworkAvailable(RegisterActivity.this)){
                    isErrorCode(NETWORK_FAILED);
                } else {
                    isErrorCode(errorCode);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                L.e("-----------结束加载进度条-------------------");
                showOrHideWeb(false);
                CookieManager cookieManager = CookieManager.getInstance();
                String cookies = cookieManager.getCookie(url);
                L.e("----从网页获取-----cookies-----------"+cookies);
                if (!StringUtils.isEmpty(cookies)){
                    String[] str = cookies.split("=");
                    L.e("--------网页的token-------------"+str[1]);
                    String token = str[1];
                    mPreManager.setToken(token);
                    mSyncCookie.syncCookie(url);
                }
                super.onPageFinished(view, url);
            }
        });
        //        mWvRegister.loadUrl(URL, syncCookie(URL));
        if (status == 20){
            L.e("------------20--------------");
            mWvRegister.loadUrl((REGISTER_IMPROVE+tel),mSyncCookie.syncCookie(REGISTER_IMPROVE+tel));
        } else {
            mWvRegister.loadUrl(REGISTER_URI,mSyncCookie.syncCookie(REGISTER_URI));
        }
    }

    @Override
    protected void setListener() {
        /* 点击事件 */
        ivBack.setOnClickListener(this);
    }

    /**
     * 是否显示webview
     * @param isShowWeb
     */
    private void showOrHideWeb(boolean isShowWeb){
        if (isShowWeb){
            mWvRegister.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            mWvRegister.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_register_back:
                RegisterActivity.this.finish();
                break;
        }
    }

    @Override
    public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
        L.e("------------showOptions();----------------");
        mUploadMessage = uploadMsg;
        showOptions();
    }

    @Override
    public boolean openFileChooserCallBackAndroid5(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        L.e("-----5.0-------showOptions();----------------");
        mUploadMsgForAndroid5 = filePathCallback;
        showOptions();
        return true;
    }

    public class DialogOnCancelListener implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            restoreUploadMsg();
        }
    }

    private void restoreUploadMsg() {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;

        } else if (mUploadMsgForAndroid5 != null) {
            mUploadMsgForAndroid5.onReceiveValue(null);
            mUploadMsgForAndroid5 = null;
        }
    }

    private void showOptions() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setOnCancelListener(new DialogOnCancelListener());

        alertDialog.setTitle("请选择操作");
        // gallery, camera.
        String[] options = {"相册", "拍照"};

        alertDialog.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (PermissionUtil.isOverMarshmallow()) {
                        if (!PermissionUtil.isPermissionValid(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Toast.makeText(RegisterActivity.this,
                                    "请去\"设置\"中开启本应用的图片媒体访问权限",
                                    Toast.LENGTH_SHORT).show();

                            restoreUploadMsg();
                            requestPermissionsAndroidM();
                            return;
                        }
                    }
                    try {
                        mSourceIntent = ImageUtil.choosePicture();
                        startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this,
                                "请去\"设置\"中开启本应用的图片媒体访问权限",
                                Toast.LENGTH_SHORT).show();
                        restoreUploadMsg();
                    }
                } else {
                    if (PermissionUtil.isOverMarshmallow()) {
                        if (!PermissionUtil.isPermissionValid(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(RegisterActivity.this,
                                    "请去\"设置\"中开启本应用的图片媒体访问权限",
                                    Toast.LENGTH_SHORT).show();

                            restoreUploadMsg();
                            requestPermissionsAndroidM();
                            return;
                        }

                        if (!PermissionUtil.isPermissionValid(RegisterActivity.this, Manifest.permission.CAMERA)) {
                            Toast.makeText(RegisterActivity.this,
                                    "请去\"设置\"中开启本应用的相机权限",
                                    Toast.LENGTH_SHORT).show();

                            restoreUploadMsg();
                            requestPermissionsAndroidM();
                            return;
                        }
                    }

                    try {
                        mSourceIntent = ImageUtil.takeBigPicture();
                        startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this,
                                "请去\"设置\"中开启本应用的相机和图片媒体访问权限",
                                Toast.LENGTH_SHORT).show();

                        restoreUploadMsg();
                    }
                }
            }
        });
        alertDialog.show();

    }
    private void requestPermissionsAndroidM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> needPermissionList = new ArrayList<>();
            needPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            needPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            needPermissionList.add(Manifest.permission.CAMERA);
            PermissionUtil.requestPermissions(RegisterActivity.this, P_CODE_PERMISSIONS, needPermissionList);

        } else {
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode != Activity.RESULT_OK) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }

            if (mUploadMsgForAndroid5 != null) {         // for android 5.0+
                mUploadMsgForAndroid5.onReceiveValue(null);
            }
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_PICK_IMAGE:
            case REQUEST_CODE_IMAGE_CAPTURE: {
                try {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        if (mUploadMessage == null) {
                            return;
                        }

                        String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, intent);

                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {
                            Log.e("TAG", "sourcePath empty or not exists.");
                            break;
                        }
                        Uri uri = Uri.fromFile(new File(sourcePath));
                        mUploadMessage.onReceiveValue(uri);

                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (mUploadMsgForAndroid5 == null) {        // for android 5.0+
                            return;
                        }

                        String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, intent);

                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {
                            Log.e("TAG", "sourcePath empty or not exists.");
                            break;
                        }
                        Uri uri = Uri.fromFile(new File(sourcePath));
                        mUploadMsgForAndroid5.onReceiveValue(new Uri[]{uri});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    /**
     * 注册页面跳转
     * @param activity
     */
    public static void invoke(Activity activity, int userRegistStatus, String tel){
        Intent intent = new Intent(activity,RegisterActivity.class);
        intent.putExtra("status",userRegistStatus);
        intent.putExtra("tel",tel);
        activity.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWvRegister.canGoBack()) {
            // goBack()表示返回WebView的上一页面
            mWvRegister.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void isErrorCode(int errorCode){
        switch (errorCode){
            case NETWORK_FAILED://网络连接失败
                llError.setVisibility(View.VISIBLE);
                tvErrorDetail.setText("网络连接失败,请检查网络...");
                T.showShort(RegisterActivity.this,"网络连接失败,请检查网络...");
                break;
            case COOKIE_FAILED:
                LoginActivity.invoke(RegisterActivity.this);
                RegisterActivity.this.finish();
                break;
            case SERVER_FAILED:
                llError.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void initStatus(boolean isNet) {

    }

}
