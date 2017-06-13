package com.yunqitechbj.clientandroid.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;
import com.yunqitechbj.clientandroid.entity.VehicleNoModel;
import com.yunqitechbj.clientandroid.fragment.OfflineSendCarFragment;
import com.yunqitechbj.clientandroid.fragment.OftenRunFragment;
import com.yunqitechbj.clientandroid.fragment.TransactionFragment;
import com.yunqitechbj.clientandroid.fragment.VehicleFragment;
import com.yunqitechbj.clientandroid.fragment.WalletFragment;
import com.yunqitechbj.clientandroid.utils.BackHandledFragment;
import com.yunqitechbj.clientandroid.utils.BackHandledInterface;
import com.yunqitechbj.clientandroid.utils.Constants;
import com.yunqitechbj.clientandroid.utils.DataCleanManager;
import com.yunqitechbj.clientandroid.utils.FileUtils;
import com.yunqitechbj.clientandroid.utils.ImageUtil;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.NetworkAvailable;
import com.yunqitechbj.clientandroid.utils.PermissionUtil;
import com.yunqitechbj.clientandroid.utils.PickImage;
import com.yunqitechbj.clientandroid.utils.PreManager;
import com.yunqitechbj.clientandroid.utils.StringUtils;
import com.yunqitechbj.clientandroid.utils.T;
import com.yunqitechbj.clientandroid.utils.Util;
import com.yunqitechbj.clientandroid.weight.Config;
import com.yunqitechbj.clientandroid.weight.InitWebView;
import com.yunqitechbj.clientandroid.weight.MyWebChomeClient;
import com.yunqitechbj.clientandroid.weight.MyWebViewDownLoadListener;
import com.yunqitechbj.clientandroid.weight.PermissionsResultListener;
import com.yunqitechbj.clientandroid.weight.SyncCookie;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;

import org.litepal.crud.DataSupport;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 主界面
 * Created by mengwei on 2016/10/26.
 */

public class MainActivity extends BaseActivity implements
        View.OnClickListener, RadioGroup.OnCheckedChangeListener, BackHandledInterface {
    /* 控件对象 */
    //    private WebView wvMain;
    //    private boolean isExit = false;// 是否退出
    //    private String mCookies;
    //    private PreManager mPreManager;
    //    private ProgressWheel mWheel;
    //    private ActionBar actionBar;
    //    private RelativeLayout llAll;
    //    private TextView tvTitle;
    //    private ImageView ivOn;
    //    private RelativeLayout rlOn;
    //    private RelativeLayout rlError;
    //    private TextView tvErrorMessage;
    //    private TextView tvErrorDetail;
    //    private RelativeLayout rlShowMain;
    //    private RelativeLayout rlBack;
    private RadioGroup rgMain;
    //
    //    private Intent mSourceIntent;
    //
    //    private ValueCallback<Uri[]> mUploadMsgForAndroid5;
    //    private ValueCallback<Uri> mUploadMessage;
    //    // permission Code
    //    private static final int P_CODE_PERMISSIONS = 101;
    //    private static final int REQUEST_CODE_PICK_IMAGE = 0;
    //    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;
    //
    //    private PopupWindow mStatusChooseWindow;
    //
    //    /* 要加载的网址 */
    //    private int checkId;
    private String URI;
    //    private String TRANS_URI = Config.API.WEB_TRANS_URL;
    //    private String RUN_URI = Config.API.WEB_RUN_URL;
    //    private String WALLET_URI = Config.API.WEB_WALLET_URL;
    //    private String VEHICLE_URI = Config.API.WEB_VEHICLE_URL;
    //    private String HELP_URI = Config.API.WEB_HELP_URL;
    //
    //    private SyncCookie mSyncCookie;
    //    private InitWebView mInitWebView;
    //
    //    /* web页面加载失败code */
    //    private final int NETWORK_FAILED = 1001;
    //    private final int COOKIE_FAILED = 401;
    //    private final int SERVER_FAILED = 404;
    //
    //加载的Fragment
    private TransactionFragment mTransactionFragment;
    private OfflineSendCarFragment mOfflineSendCarFragment;
    private VehicleFragment mVehicleFragment;
    private OftenRunFragment mOftenRunFragment;
    private WalletFragment mWalletFragment;

    private final int TRANS_TAB = 1;
    private final int OFFLINE_TAB = 2;
    private final int VEHICLE_TAB = 3;
    private final int RUN_TAB = 4;
    private final int WALLET_TAB = 5;
    private boolean isExit = false;// 是否退出
    private int isFinishBack = 3;

    public static FragmentManager manager;

    private BackHandledFragment mBackHandedFragment;
    private boolean hadIntercept;

    /* 无网弹窗 */
    AlertDialog builder;
    private long lastTime;
    //
    //    // IWXAPI：第三方APP和微信通信的接口
    //    public static IWXAPI api;
    //
    //    /* 存放图片文件夹的同一路径 */
    //    private String personalVehicleTempFileDir = Environment
    //            .getExternalStorageDirectory() + "/";
    //
    //    /* 存放矿发图片文件夹的路径 */
    //    public String danJuImgName = "danju.jpg";
    //    public File danjuImageFile = null;
    //    public String imgDanjuBase64 = null;
    //
    //上传页面传递过来的标记
    private int mark = 0;

    @Override
    protected void initStatus(boolean isNet) {
        if (!isNet) {
            showUnNetworkDialog();
        } else {
            if (builder != null) {
                T.showShort(mContext, "网络恢复连接");
                builder.dismiss();
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public String getURI() {
        return URI;
    }

    @Override
    protected void initView() {
        //        // 初始化
        //        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        //        // 向微信终端注册你的id
        //        api.registerApp(Constants.APP_ID);

        mPreManager = PreManager.instance(this);
        //        //获取传递的数据
        //        checkId = getIntent().getIntExtra("checkId", 0);
        //
        //        wvMain = obtainView(R.id.wv_main);
        //                llAll = obtainView(R.id.rl_all);
        //        mWheel = obtainView(R.id.progress_wheel);
        //        tvTitle = obtainView(R.id.tv_main_title);
        //        ivOn = obtainView(R.id.iv_main_on);
        //        rlOn = obtainView(R.id.rl_show_on);
        //        rlError = obtainView(R.id.ll_main_error);
        //        tvErrorMessage = obtainView(R.id.tv_error_message);
        //        tvErrorDetail = obtainView(R.id.tv_error_detail);
        //        rlShowMain = obtainView(R.id.rl_show_main);
        //        rlBack = obtainView(R.id.rl_main_back);
        //
        //        createChooseStatusWindow();
        rgMain = obtainView(R.id.rg_employer_main);
        manager = getSupportFragmentManager();


    }

    @Override
    protected void initData() {
        rgMain.check(R.id.rb_main_transaction);
        //        mSyncCookie = new SyncCookie(mContext);
        //        mInitWebView = new InitWebView(wvMain, mContext);
        //        //判断要加载的网页
        //        switch (checkId) {
        //            case 0://交易广场
        //                URI = TRANS_URI;
        //                break;
        //            case 1://长跑路线
        //                URI = RUN_URI;
        //                break;
        //            case 2://企业钱包
        //                URI = WALLET_URI;
        //                break;
        //            case 3://车辆管理
        //                URI = VEHICLE_URI;
        //                break;
        //            case 4://系统帮助
        //                URI = HELP_URI;
        //                break;
        //        }
        //        //初始化webview
        //        initWebView();
    }

    @Override
    protected void setListener() {
        //        rlOn.setOnClickListener(this);
        //        rlBack.setOnClickListener(this);
        rgMain.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //        mark = getIntent().getIntExtra("mark", 0);
        mark = mPreManager.getLoadUrl();
        if (mark == 1) {
            if (!StringUtils.isEmpty(getIntent().getStringExtra("Url"))) {
                URI = getIntent().getStringExtra("Url");
            }
            L.e("-------传递网址-------" + URI);
            mPreManager.setLoadUrl(0);
        }
    }

    //    private void initWebView() {
    //        //        mCookies = CacheUtils.getString(mContext, "TokenValue", "");
    ////        mCookies = mPreManager.getToken();
    ////        L.e("---------获取mCookies------------" + mCookies);
    ////
    ////        //webview初始化
    ////        mInitWebView.initWebView();
    ////        wvMain.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    ////        //cookie过期，js调用Android方法,window.back.goBackLogin() 的方式调用
    ////        wvMain.addJavascriptInterface(new JsInteration(), "back");
    ////
    ////        // 设置Web视图
    ////        wvMain.setWebViewClient(new webViewClient());
    ////        wvMain.setWebChromeClient(new MyWebChomeClient(this, tvTitle));
    ////        wvMain.setDownloadListener(new MyWebViewDownLoadListener(this));
    ////
    ////        // 加载需要显示的网页
    ////        wvMain.loadUrl(URI, mSyncCookie.syncCookie(URI));
    //    }

    //    @Override
    //    public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
    //        L.e("------------showOptions();----------------");
    //        mUploadMessage = uploadMsg;
    //        showOptions();
    //    }
    //
    //    @Override
    //    public boolean openFileChooserCallBackAndroid5(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
    //        L.e("-----5.0-------showOptions();----------------");
    //        mUploadMsgForAndroid5 = filePathCallback;
    //        showOptions();
    //        return true;
    //    }

    //    private class DialogOnCancelListener implements DialogInterface.OnCancelListener {
    //        @Override
    //        public void onCancel(DialogInterface dialogInterface) {
    //            restoreUploadMsg();
    //        }
    //    }
    //
    //    private void restoreUploadMsg() {
    //        if (mUploadMessage != null) {
    //            mUploadMessage.onReceiveValue(null);
    //            mUploadMessage = null;
    //        } else if (mUploadMsgForAndroid5 != null) {
    //            mUploadMsgForAndroid5.onReceiveValue(null);
    //            mUploadMsgForAndroid5 = null;
    //        }
    //    }
    //
    //    private void showOptions() {
    //        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
    //        alertDialog.setOnCancelListener(new DialogOnCancelListener());
    //
    //        alertDialog.setTitle("请选择操作");
    //        // gallery, camera.
    //        String[] options = {"相册", "拍照"};
    //
    //        alertDialog.setItems(options, new DialogInterface.OnClickListener() {
    //
    //            @Override
    //            public void onClick(DialogInterface dialog, int which) {
    //                if (which == 0) {
    //                    if (PermissionUtil.isOverMarshmallow()) {
    //                        if (!PermissionUtil.isPermissionValid(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
    //                            Toast.makeText(MainActivity.this,
    //                                    "请去\"设置\"中开启本应用的图片媒体访问权限",
    //                                    Toast.LENGTH_SHORT).show();
    //
    //                            restoreUploadMsg();
    //                            requestPermissionsAndroidM();
    //                            return;
    //                        }
    //                    }
    //                    try {
    //                        mSourceIntent = ImageUtil.choosePicture();
    //                        startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
    //                    } catch (Exception e) {
    //                        e.printStackTrace();
    //                        Toast.makeText(MainActivity.this,
    //                                "请去\"设置\"中开启本应用的图片媒体访问权限",
    //                                Toast.LENGTH_SHORT).show();
    //                        restoreUploadMsg();
    //                    }
    //                } else {
    //                    if (PermissionUtil.isOverMarshmallow()) {
    //                        if (!PermissionUtil.isPermissionValid(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
    //                            Toast.makeText(MainActivity.this,
    //                                    "请去\"设置\"中开启本应用的图片媒体访问权限",
    //                                    Toast.LENGTH_SHORT).show();
    //
    //                            restoreUploadMsg();
    //                            requestPermissionsAndroidM();
    //                            return;
    //                        }
    //
    //                        if (!PermissionUtil.isPermissionValid(MainActivity.this, Manifest.permission.CAMERA)) {
    //                            Toast.makeText(MainActivity.this,
    //                                    "请去\"设置\"中开启本应用的相机权限",
    //                                    Toast.LENGTH_SHORT).show();
    //
    //                            restoreUploadMsg();
    //                            requestPermissionsAndroidM();
    //                            return;
    //                        }
    //                    }
    //                    try {
    //                        mSourceIntent = ImageUtil.takeBigPicture();
    //                        startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);
    //                    } catch (Exception e) {
    //                        Toast.makeText(MainActivity.this,
    //                                "请去\"设置\"中开启本应用的相机和图片媒体访问权限",
    //                                Toast.LENGTH_SHORT).show();
    //                        e.printStackTrace();
    //                        restoreUploadMsg();
    //                    }
    //                }
    //            }
    //        });
    //        alertDialog.show();
    //
    //    }
    //
    //    private void requestPermissionsAndroidM() {
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    //            List<String> needPermissionList = new ArrayList<>();
    //            needPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    //            needPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
    //            needPermissionList.add(Manifest.permission.CAMERA);
    //            PermissionUtil.requestPermissions(MainActivity.this, P_CODE_PERMISSIONS, needPermissionList);
    //
    //        } else {
    //            return;
    //        }
    //    }
    //
    //
    //    @Override
    //    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //
    //        //                switch (requestCode){
    //        //                    case 1://图库返回
    //        //                        if (resultCode == RESULT_OK){
    //        //                            Uri uri = data.getData();
    //        //                            String realPathFromURI = FileUtils.getRealPathFromURI(uri,this);
    //        //                            if (TextUtils.isEmpty(realPathFromURI)) {
    //        //                                danjuImageFile = null;
    //        //                            } else {
    //        //                                danjuImageFile = new File(personalVehicleTempFileDir
    //        //                                        + "danju.jpg");
    //        //                                File temFile = new File(realPathFromURI);
    //        //
    //        //                                FileUtils.copyFile(temFile, danjuImageFile);
    //        //                            }
    //        //                        }
    //        //                        break;
    //        //                    case 2://矿发数据拍照返回
    //        //                        if (resultCode == RESULT_OK){
    //        //                            danjuImageFile = new File(personalVehicleTempFileDir + "danju.jpg");
    //        //                        }
    //        //                        break;
    //        //                }
    //        //
    //        //                if (requestCode == 1){
    //        //                    // 判断本地文件是否存在
    //        //                    if (danjuImageFile == null) {
    //        //                        return;
    //        //                    }
    //        //
    //        //                    // TODO--
    //        //                    byte[] bytes = handleImage(danjuImageFile, 800, 800);
    //        //
    //        //                    if (bytes == null || bytes.length == 0) {
    //        //                        return;
    //        //                    }
    //        //                    int degree = ImageUtil.readPictureDegree(danjuImageFile
    //        //                            .getAbsolutePath());
    //        //
    //        //                    imgDanjuBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
    //        //
    //        //                    // base64解码
    //        //                    byte[] decode = Base64.decode(imgDanjuBase64, Base64.NO_WRAP);
    //        //
    //        //                    // TODO--将图片保存在SD卡
    //        //                    BufferedOutputStream bos = null;
    //        //                    FileOutputStream fos = null;
    //        //                    File file = new File(personalVehicleTempFileDir
    //        //                            + "danju.jpg");
    //        //                    try {
    //        //                        if (file.exists()) {
    //        //                            file.createNewFile();
    //        //                        }
    //        //                        fos = new FileOutputStream(file);
    //        //                        bos = new BufferedOutputStream(fos);
    //        //                        bos.write(decode);
    //        //                        fos.flush();
    //        //                    } catch (Exception e) {
    //        //                        e.printStackTrace();
    //        //                    } finally {
    //        //                        if (bos != null) {
    //        //                            try {
    //        //                                bos.close();
    //        //                            } catch (IOException e1) {
    //        //                                e1.printStackTrace();
    //        //                            }
    //        //                        }
    //        //                        if (fos != null) {
    //        //                            try {
    //        //                                fos.close();
    //        //                            } catch (IOException e1) {
    //        //                                e1.printStackTrace();
    //        //                            }
    //        //                        }
    //        //                    }
    //        //                }
    //        //
    //        //                if (requestCode == 2){
    //        //                    // 判断本地文件是否存在
    //        //                    if (danjuImageFile == null) {
    //        //                        return;
    //        //                    }
    //        //
    //        //                    // TODO--
    //        //                    byte[] bytes = handleImage(danjuImageFile, 800, 800);
    //        //
    //        //                    if (bytes == null || bytes.length == 0) {
    //        //                        return;
    //        //                    }
    //        //                    int degree = ImageUtil.readPictureDegree(danjuImageFile
    //        //                            .getAbsolutePath());
    //        //
    //        //                    imgDanjuBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
    //        //
    //        //                    // base64解码
    //        //                    byte[] decode = Base64.decode(imgDanjuBase64, Base64.NO_WRAP);
    //        //
    //        //                    // TODO--将图片保存在SD卡
    //        //                    BufferedOutputStream bos = null;
    //        //                    FileOutputStream fos = null;
    //        //                    File file = new File(personalVehicleTempFileDir
    //        //                            + "danju.jpg");
    //        //                    try {
    //        //                        if (file.exists()) {
    //        //                            file.createNewFile();
    //        //                        }
    //        //                        fos = new FileOutputStream(file);
    //        //                        bos = new BufferedOutputStream(fos);
    //        //                        bos.write(decode);
    //        //                        fos.flush();
    //        //                    } catch (Exception e) {
    //        //                        e.printStackTrace();
    //        //                    } finally {
    //        //                        if (bos != null) {
    //        //                            try {
    //        //                                bos.close();
    //        //                            } catch (IOException e1) {
    //        //                                e1.printStackTrace();
    //        //                            }
    //        //                        }
    //        //                        if (fos != null) {
    //        //                            try {
    //        //                                fos.close();
    //        //                            } catch (IOException e1) {
    //        //                                e1.printStackTrace();
    //        //                            }
    //        //                        }
    //        //                    }
    //        //                }
    //
    //        if (resultCode != Activity.RESULT_OK) {
    //            if (mUploadMessage != null) {
    //                mUploadMessage.onReceiveValue(null);
    //            }
    //
    //            if (mUploadMsgForAndroid5 != null) {         // for android 5.0+
    //                mUploadMsgForAndroid5.onReceiveValue(null);
    //            }
    //            return;
    //        }
    //
    //        switch (requestCode) {
    //            case REQUEST_CODE_PICK_IMAGE:
    //            case REQUEST_CODE_IMAGE_CAPTURE: {
    //                try {
    //                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
    //                        if (mUploadMessage == null) {
    //                            return;
    //                        }
    //
    //                        String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);
    //
    //                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {
    //                            Log.e("TAG", "sourcePath empty or not exists.");
    //                            break;
    //                        }
    //
    //                        L.e("=====sourcePath======" + sourcePath.toString());
    //
    //                        Uri uri = Uri.fromFile(new File(sourcePath));
    //                        mUploadMessage.onReceiveValue(uri);
    //
    //                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //                        if (mUploadMsgForAndroid5 == null) {        // for android 5.0+
    //                            return;
    //                        }
    //
    //                        String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);
    //
    //                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {
    //                            Log.e("TAG", "sourcePath empty or not exists.");
    //                            break;
    //                        }
    //
    //                        L.e("------------sourcePath--------------" + sourcePath.toString());
    //
    //                        Uri uri = Uri.fromFile(new File(sourcePath));
    //                        mUploadMsgForAndroid5.onReceiveValue(new Uri[]{uri});
    //                    }
    //                } catch (Exception e) {
    //                    e.printStackTrace();
    //                }
    //                break;
    //            }
    //        }
    //    }
    //
    //
    //    /**
    //     * Web视图
    //     */
    //    private class webViewClient extends WebViewClient {
    //        @Override
    //        public void onPageStarted(WebView view, String url, Bitmap favicon) {
    //            super.onPageStarted(view, url, favicon);
    //            showOrHideWeb(true);
    //        }
    //
    //        //        @Override
    //        //        public boolean shouldOverrideUrlLoading(WebView view, String url) {
    //        //            if ()
    //        //            return super.shouldOverrideUrlLoading(view, url);
    //        //        }
    //
    //        @Override
    //        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    //            L.d("==== WebView ajax 请求 ====", url);
    //            mSyncCookie.syncCookie(url);
    //            //将加好cookie的url传给父类继续执行
    //            return super.shouldInterceptRequest(view, url);
    //        }
    //
    //
    //        @Override
    //        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    //            super.onReceivedError(view, errorCode, description, failingUrl);
    //            L.e("------errorCode---------" + errorCode);
    //            rlShowMain.setVisibility(View.GONE);
    //            if (!NetworkAvailable.isNetworkAvailable(MainActivity.this)) {
    //                isErrorCode(NETWORK_FAILED);
    //            } else {
    //                isErrorCode(errorCode);
    //            }
    //        }
    //
    //        @Override
    //        public void onPageFinished(WebView view, String url) {
    //            super.onPageFinished(view, url);
    //            showOrHideWeb(false);
    //        }
    //
    //        @Override
    //        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
    ////            super.onReceivedSslError(webView, sslErrorHandler, sslError);
    //            sslErrorHandler.proceed();
    //        }
    //    }
    //

    /**
     * 主界面跳转方法
     *
     * @param activity
     */
    public static void invoke(Context activity, int checkId) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("checkId", checkId);
        activity.startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        switch (isFinishBack) {
            case 1:
                mfinish();
                break;
            case 2:
                if (mVehicleFragment != null) {
                    if (mVehicleFragment.onBackPressed()) {
                        return;
                    } else {
                        mfinish();
                    }
                }
                break;
            case 3:
                if (mTransactionFragment != null) {
                    if (mTransactionFragment.onBackPressed()) {
                        return;
                    } else {
                        mfinish();
                    }
                }
                break;
            case 4:
                if (mOftenRunFragment != null) {
                    if (mOftenRunFragment.onBackPressed()) {
                        return;
                    } else {
                        mfinish();
                    }
                }
                break;
            case 5:
                if (mWalletFragment != null) {
                    if (mWalletFragment.onBackPressed()) {
                        return;
                    } else {
                        mfinish();
                    }
                }
                break;
        }
    }

    private void mfinish() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime < 2 * 1000) {
            DataCleanManager.cleanInternalCache(mContext);
            DataCleanManager.cleanExternalCache(mContext);
            String path = Environment.getExternalStorageDirectory().getPath() + "/yunqizhengtong";
            if (ImageUtil.isPathExist(path)) {
                DataCleanManager.cleanCustomCache(path);
            }
            MainActivity.this.finish();
            super.onBackPressed();
        } else {
            T.showShort(mContext, "再按一次，退出程序");
            lastTime = currentTime;
        }
    }

    //
    //    @Override
    //    public boolean onKeyDown(int keyCode, KeyEvent event) {
    ////        if ((keyCode == KeyEvent.KEYCODE_BACK) && wvMain.canGoBack()) {
    ////            // goBack()表示返回WebView的上一页面
    //////            wvMain.goBack();
    ////            return true;
    ////        } else {
    ////            long currentTime = System.currentTimeMillis();
    ////            if (currentTime - lastTime < 2 * 1000) {
    ////                DataCleanManager.cleanInternalCache(mContext);
    ////                DataCleanManager.cleanExternalCache(mContext);
    ////                String path = Environment.getExternalStorageDirectory().getPath() + "/yunqizhengtong";
    ////                if (ImageUtil.isPathExist(path)) {
    ////                    DataCleanManager.cleanCustomCache(path);
    ////                }
    ////                MainActivity.this.finish();
    ////            } else {
    ////                T.showShort(mContext, "再按一次，退出程序");
    ////                lastTime = currentTime;
    ////            }
    ////        }
    //        if (keyCode == KeyEvent.KEYCODE_BACK) {
    //            // 再按一次退出程序
    ////            exit();
    //        }
    //        return false;
    //    }
    // 再按一次退出程序的方法
    private void exit() {
        if (!isExit) {
            isExit = true;
            T.showShort(mContext, "再按一次退出程序");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            MainActivity.this.finish();
        }
    }
    //
    //    private void dismissPupWindows() {
    //        if (null != mStatusChooseWindow) {
    //            mStatusChooseWindow.dismiss();
    //        }
    //    }
    //
    //    private void showPupWindow() {
    //        int width = llAll.getMeasuredWidth();
    //        mStatusChooseWindow.showAsDropDown(llAll, width, 0);
    //    }
    //
    //    private void createChooseStatusWindow() {
    //        View popupView = getLayoutInflater().inflate(
    //                R.layout.employer_current_ticket_popuwindow, null);
    //        mStatusChooseWindow = new PopupWindow(popupView,
    //                ViewGroup.LayoutParams.WRAP_CONTENT,
    //                ViewGroup.LayoutParams.WRAP_CONTENT);
    //        mStatusChooseWindow.setFocusable(true);
    //        mStatusChooseWindow.setOutsideTouchable(true);
    //        mStatusChooseWindow.setBackgroundDrawable(new BitmapDrawable());
    //
    //        popupView.findViewById(R.id.tv_web_trans)
    //                .setOnClickListener(this);
    //        popupView.findViewById(R.id.tv_web_run)
    //                .setOnClickListener(this);
    //        popupView.findViewById(R.id.tv_web_wallet)
    //                .setOnClickListener(this);
    //        popupView.findViewById(R.id.tv_web_help)
    //                .setOnClickListener(this);
    //        popupView.findViewById(R.id.tv_web_back)
    //                .setOnClickListener(this);
    //        popupView.findViewById(R.id.tv_web_vehicle)
    //                .setOnClickListener(this);
    //        popupView.findViewById(R.id.tv_web_offline)
    //                .setOnClickListener(this);
    //
    //    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //            case R.id.rl_main_back:
            //                if (StringUtils.isNetworkConnected(mContext)) {
            //                    Intent intent = new Intent(MainActivity.this, OfflineDisplayActivity.class);
            //                    startActivity(intent);
            //                } else {
            //                    T.showShort(mContext, "当前无网络，不能进行同步操作");
            //                }
            //                break;
            //            case R.id.rl_show_on:
            //                if (mStatusChooseWindow.isShowing()) {
            //                    dismissPupWindows();
            //                } else {
            //                    showPupWindow();
            //                }
            //                break;
            //            case R.id.tv_web_trans:
            //                dismissPupWindows();
            //                if (StringUtils.isNetworkConnected(mContext)) {
            //                    tvTitle.setText(getResources().getString(R.string.tv_web_trans));
            //                    wvMain.loadUrl(TRANS_URI, mSyncCookie.syncCookie(TRANS_URI));
            //                } else {
            //                    T.showShort(mContext, "当前无网络，请检查网络");
            //                }
            //                break;
            //            case R.id.tv_web_run:
            //                dismissPupWindows();
            //                if (StringUtils.isNetworkConnected(mContext)) {
            //                    tvTitle.setText(getResources().getString(R.string.tv_web_run));
            //                    wvMain.loadUrl(RUN_URI, mSyncCookie.syncCookie(RUN_URI));
            //                } else {
            //                    T.showShort(mContext, "当前无网络，请检查网络");
            //                }
            //                break;
            //            case R.id.tv_web_wallet:
            //                dismissPupWindows();
            //                if (StringUtils.isNetworkConnected(mContext)) {
            //                    tvTitle.setText(getResources().getString(R.string.tv_web_wallet));
            //                    wvMain.loadUrl(WALLET_URI, mSyncCookie.syncCookie(WALLET_URI));
            //                } else {
            //                    T.showShort(mContext, "当前无网络，请检查网络");
            //                }
            //                break;
            //            case R.id.tv_web_vehicle:
            //                dismissPupWindows();
            //                if (StringUtils.isNetworkConnected(mContext)) {
            //                    tvTitle.setText(getResources().getString(R.string.tv_web_vehicle));
            //                    wvMain.loadUrl(VEHICLE_URI, mSyncCookie.syncCookie(VEHICLE_URI));
            //                } else {
            //                    T.showShort(mContext, "当前无网络，请检查网络");
            //                }
            //                break;
            //            case R.id.tv_web_help:
            //                dismissPupWindows();
            //                if (StringUtils.isNetworkConnected(mContext)) {
            //                    tvTitle.setText(getResources().getString(R.string.tv_web_help));
            //                    wvMain.loadUrl(HELP_URI, mSyncCookie.syncCookie(HELP_URI));
            //                } else {
            //                    T.showShort(mContext, "当前无网络，请检查网络");
            //                }
            //                break;
            //            case R.id.tv_web_offline:
            //                dismissPupWindows();
            //                Intent intent = new Intent(MainActivity.this, OfflineSendCarActivity.class);
            //                startActivity(intent);
            //                //                MainActivity.this.finish();
            //                break;
            //            case R.id.tv_web_back:
            //                dismissPupWindows();
            //                Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
            //                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //                startActivity(intent1);
            //                mPreManager.setToken(null);
            //                DataCleanManager.cleanInternalCache(mContext);
            //                DataCleanManager.cleanExternalCache(mContext);
            //                String path = Environment.getExternalStorageDirectory().getPath() + "/yunqizhengtong";
            //                if (ImageUtil.isPathExist(path)) {
            //                    DataCleanManager.cleanCustomCache(path);
            //                }
            //                MainActivity.this.finish();
            //                break;
        }
    }

    //    private void showOrHideWeb(boolean isShow) {
    //        if (isShow) {
    //            mWheel.setVisibility(View.VISIBLE);
    //            wvMain.setVisibility(View.GONE);
    //        } else {
    //            mWheel.setVisibility(View.GONE);
    //            wvMain.setVisibility(View.VISIBLE);
    //        }
    //    }

    //    /**
    //     * 返回登录界面（token过期）
    //     * 供js调用的方法
    //     */
    //    public class JsInteration {
    //        @JavascriptInterface
    //        public void goBackLogin() {
    //            L.e("----------cookie过期，回到登陆界面------------");
    //            //cookie过期，回到登陆界面
    //            LoginActivity.invoke(mContext);
    //            MainActivity.this.finish();
    //        }
    //
    //        /**
    //         * 获取派车数
    //         *
    //         * @param strJson
    //         */
    //        @JavascriptInterface
    //        public void getVehicleList(String strJson) {
    //            Type listType = new TypeToken<ArrayList<VehicleNoModel>>() {
    //            }.getType();
    //            ArrayList<VehicleNoModel> vehicleList = new Gson().fromJson(strJson, listType);
    //
    //            L.e("--------vehicleList---------" + vehicleList.toString());
    //            if (vehicleList.size() != 0) {
    //                for (int i = 0; i < vehicleList.size(); i++) {
    //                    OfflineStorage offlineStorage = new OfflineStorage();
    //                    offlineStorage.setVehicleName(vehicleList.get(i).getVehicleNo());
    //                    offlineStorage.setVehiclePhone(vehicleList.get(i).getTel());
    //                    offlineStorage.setTicketId(vehicleList.get(i).getTicketId());
    //                    offlineStorage.setTicketCode(vehicleList.get(i).getTicketCode());
    //                    offlineStorage.setInfoId(vehicleList.get(i).getInfoId());
    //                    offlineStorage.save();
    //                }
    //            }
    //
    //
    //        }
    //
    //        /**
    //         * 获取矿发数据
    //         *
    //         * @param weightInit 矿发吨数
    //         * @param initTime   矿发时间
    //         * @param initImg    矿发单据
    //         */
    //        @JavascriptInterface
    //        public void getInitData(String ticketId, String weightInit, String initTime, String initImg) {
    //            //获取当前时间
    //            String mCurrentTime = StringUtils.getCurrentDate();
    //            long mCurrentLong = StringUtils.getStringToDate(mCurrentTime);//当前时间
    //            L.e("---------mCurrentTime-----------" + mCurrentTime);
    //            L.e("----------mCurrentLong-------------" + mCurrentLong);
    //            L.e("-------ticketId--------" + ticketId);
    //            L.e("-------weightInit--------" + weightInit);
    //            L.e("-------initTime--------" + initTime);
    //            L.e("-------initImg--------" + initImg);
    //            //判断数据库中该数据是否完善
    //            List<OfflineStorage> offlist = DataSupport.where("ticketId = ?", ticketId).find(OfflineStorage.class);
    //            L.e("-------offlist--------" + offlist.toString());
    //
    //            if (offlist.size() != 0) {
    //                String mWeightReach = offlist.get(0).getSignedDun();
    //                String mReachTime = offlist.get(0).getSignedDate();
    //                if (StringUtils.isEmpty(mWeightReach) && StringUtils.isEmpty(mReachTime)) {
    //                    ContentValues values = new ContentValues();
    //                    values.put("minehairDun", weightInit);
    //                    values.put("minehairDate", initTime);
    //                    if (!StringUtils.isEmpty(initImg)) {
    //                        values.put("minehireImg", initImg);
    //                    }
    //                    values.put("updateTime", mCurrentLong);
    //                    DataSupport.updateAll(OfflineStorage.class, values, "ticketId = ?", ticketId);
    //                } else {
    //                    DataSupport.deleteAll(OfflineStorage.class, "ticketId = ?", ticketId);
    //                }
    //            }
    //        }
    //
    //        /**
    //         * 获取签收数据
    //         *
    //         * @param weightReach 签收吨数
    //         * @param reachTime   签收时间
    //         * @param reachImg    签收单据
    //         */
    //        @JavascriptInterface
    //        public void getReachData(String ticketId, String weightReach, String reachTime, String reachImg) {
    //            //获取当前时间
    //            String mCurrentTime = StringUtils.getCurrentDate();
    //            long mCurrentLong = StringUtils.getStringToDate(mCurrentTime);//当前时间
    //            L.e("---------mCurrentTime-----------" + mCurrentTime);
    //            L.e("----------mCurrentLong-------------" + mCurrentLong);
    //            L.e("-------ticketId--------" + ticketId);
    //            L.e("-------weightReach--------" + weightReach);
    //            L.e("-------reachTime--------" + reachTime);
    //            L.e("-------reachImg--------" + reachImg);
    //            //判断数据库中该数据是否完善
    //            List<OfflineStorage> offlist = DataSupport.where("ticketId = ?", ticketId).find(OfflineStorage.class);
    //            L.e("-------offlist--------" + offlist.toString());
    //
    //            if (offlist.size() != 0) {
    //                String mWeightInit = offlist.get(0).getMinehairDun();
    //                String mInitTime = offlist.get(0).getMinehairDate();
    //                if (StringUtils.isEmpty(mWeightInit) && StringUtils.isEmpty(mInitTime)) {
    //                    ContentValues values = new ContentValues();
    //                    values.put("signedDun", weightReach);
    //                    values.put("signedDate", reachTime);
    //                    if (!StringUtils.isEmpty(reachImg)) {
    //                        values.put("signedImg", reachImg);
    //                    }
    //                    values.put("updateTime", mCurrentLong);
    //                    DataSupport.updateAll(OfflineStorage.class, values, "ticketId = ?", ticketId);
    //                } else {
    //                    DataSupport.deleteAll(OfflineStorage.class, "ticketId = ?", ticketId);
    //                }
    //            }
    //        }
    //
    //        @JavascriptInterface
    //        public String getToken() {
    //            //获取cookies
    //            String cookies = mPreManager.getToken();
    //            return cookies;
    //        }
    //
    //        @JavascriptInterface
    //        public void setWXToken(String token) {
    //            L.e("------传过来的Token------" + token);
    //            mPreManager.setToken(token);
    //        }
    //
    //        /**
    //         * 获取微信分享的内容
    //         *
    //         * @param url
    //         */
    //        @JavascriptInterface
    //        public void setWXShare(String title, String url, String detail) {
    //            L.e("------title---------" + title);
    //            L.e("------url---------" + url);
    //            L.e("------detail---------" + detail);
    //            shareText(title, url, detail);
    //        }
    //
    //        @JavascriptInterface
    //        public void setLoadurl(){
    //            L.e("-----------------------------");
    //            showDialog(1);
    //        }

    //        /**
    //         * 调用照相机或相册
    //         *
    //         * @return
    //         */
    //        @JavascriptInterface
    //        public String setCameraPhone() {
    //            L.e("===============jinru============");
    //            performRequestPermissions("为了应用可以正常使用，请您点击确认申请权限。", new String[]{
    //                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
    //                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1, new PermissionsResultListener() {
    //                @Override
    //                public void onPermissionGranted() {
    //                    //显示弹窗
    //                    showUploadCamera();
    //                }
    //
    //                @Override
    //                public void onPermissionDenied() {
    //                }
    //            });
    //            L.e("------imgDanjuBase64-------" + imgDanjuBase64);
    //            return imgDanjuBase64;
    //        }

    //    }

    //    /**
    //     * 对加载错误页面进行处理
    //     *
    //     * @param errorCode
    //     */
    //    private void isErrorCode(int errorCode) {
    //        switch (errorCode) {
    //            case NETWORK_FAILED://网络连接失败
    //                rlError.setVisibility(View.VISIBLE);
    //                tvErrorDetail.setText("网络连接失败,请检查网络...");
    //                T.showShort(MainActivity.this, "网络连接失败,请检查网络...");
    //                break;
    //            case COOKIE_FAILED:
    //                LoginActivity.invoke(MainActivity.this);
    //                MainActivity.this.finish();
    //                break;
    //            case SERVER_FAILED:
    //                rlError.setVisibility(View.VISIBLE);
    //                break;
    //        }
    //    }
    //
    //        @Override
    //        protected void onNetworkConnected(NetUtils.NetType type) {
    //            //恢复网络，重新加载网页
    //            dismissDialog(1);
    //            wvMain.loadUrl(TRANS_URI, mSyncCookie.syncCookie(TRANS_URI));
    //        }
    //
    //        @Override
    //        protected void onNetworkDisConnected() {
    //            //没有网络，弹窗提示进入离线派车页面
    //            showDialog(1);
    //        }
    //
    //    @Override
    //    protected void initStatus(boolean isNet) {
    //        if (!isNet) {
    //            showDialog(1);
    //        } else {
    //            if (builder != null) {
    //                T.showShort(mContext, "网络恢复连接");
    //                dismissDialog(1);
    //                rlError.setVisibility(View.GONE);
    //                rlShowMain.setVisibility(View.VISIBLE);
    //                //                wvMain.loadUrl(TRANS_URI, mSyncCookie.syncCookie(TRANS_URI));
    //            }
    //        }
    //    }

    /**
     * 没有网络提示弹窗
     */
    private void showUnNetworkDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View net_view = layoutInflater.inflate(R.layout.dialog_unnetwork, null);

        TextView tvCancel = (TextView) net_view.findViewById(R.id.tv_dialog_net_cancel);
        TextView tvSure = (TextView) net_view.findViewById(R.id.tv_dialog_net_sure);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        builder = dialog.create();
        builder.setView(net_view);
        builder.setCancelable(true);
        builder.show();

        //取消
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "你好！";
                //                dismissDialog(1);
                //                wvMain.loadUrl("javascript:andriod('"+str+"')");
                builder.dismiss();
            }
        });
        //确定
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入离线派车界面
                //                dismissDialog(1);
                builder.dismiss();
                showFragment(1);
                isFinishBack = 1;
                rgMain.check(R.id.rb_main_offline);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_main_transaction://交易广场
                isFinishBack = 3;
                showFragment(3);
                break;
            case R.id.rb_main_offline://离线派车
                isFinishBack = 1;
                showFragment(1);
                break;
            case R.id.rb_main_run://常跑路线
                isFinishBack = 4;
                showFragment(4);
                break;
            case R.id.rb_main_vehicle://车辆管理
                isFinishBack = 2;
                showFragment(2);
                break;
            case R.id.rb_main_wallet://企业钱包
                isFinishBack = 5;
                showFragment(5);
                break;
        }
    }

    private void showFragment(int checkId) {
        switch (checkId) {
            case 1:
                FragmentTransaction transaction1 = manager.beginTransaction();
                hideAllFragment(transaction1);
                if (mOfflineSendCarFragment == null) {
                    mOfflineSendCarFragment = new OfflineSendCarFragment();
                    transaction1.add(R.id.fl_main_container, mOfflineSendCarFragment);
                } else {
                    transaction1.show(mOfflineSendCarFragment);
                }
                transaction1.commit();
                break;
            case 2:
                FragmentTransaction transaction2 = manager.beginTransaction();
                hideAllFragment(transaction2);
                if (mVehicleFragment == null) {
                    mVehicleFragment = new VehicleFragment();
                    transaction2.add(R.id.fl_main_container, mVehicleFragment);
                } else {
                    transaction2.show(mVehicleFragment);
                }
                transaction2.commit();
                break;
            case 3:
                L.e("-----交易广场--------");
                FragmentTransaction transaction3 = manager.beginTransaction();
                hideAllFragment(transaction3);
                if (mTransactionFragment == null) {
                    mTransactionFragment = new TransactionFragment();
                    transaction3.add(R.id.fl_main_container, mTransactionFragment);
                } else {
                    transaction3.show(mTransactionFragment);
                }
                transaction3.commit();
                break;
            case 4:
                FragmentTransaction transaction4 = manager.beginTransaction();
                hideAllFragment(transaction4);
                if (mOftenRunFragment == null) {
                    mOftenRunFragment = new OftenRunFragment();
                    transaction4.add(R.id.fl_main_container, mOftenRunFragment);
                } else {
                    transaction4.show(mOftenRunFragment);
                }
                transaction4.commit();
                break;
            case 5:
                FragmentTransaction transaction5 = manager.beginTransaction();
                hideAllFragment(transaction5);
                if (mWalletFragment == null) {
                    mWalletFragment = new WalletFragment();
                    transaction5.add(R.id.fl_main_container, mWalletFragment);
                } else {
                    transaction5.show(mWalletFragment);
                }
                transaction5.commit();
                break;
        }
    }

    /**
     * 隐藏全部的Fragment
     *
     * @param transaction
     */
    private void hideAllFragment(FragmentTransaction transaction) {
        if (mTransactionFragment != null) {
            transaction.hide(mTransactionFragment);
        }
        if (mOfflineSendCarFragment != null) {
            transaction.hide(mOfflineSendCarFragment);
        }
        if (mVehicleFragment != null) {
            transaction.hide(mVehicleFragment);
        }
        if (mOftenRunFragment != null) {
            transaction.hide(mOftenRunFragment);
        }
        if (mWalletFragment != null) {
            transaction.hide(mWalletFragment);
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    //    @Override
    //    protected Dialog onCreateDialog(int id) {
    //        if (id == 1) {
    //            if (builder == null) {
    //                showUnNetworkDialog();
    //            }
    //            return builder;
    //        }
    //        return super.onCreateDialog(id);
    //    }
    //
    //    // 文本分享
    //    private void shareText(String title, String url, String detail) {
    //        try {
    //            //初始化一个WXWebpageObject对象，填写url
    //            WXWebpageObject webpage = new WXWebpageObject();
    //            webpage.webpageUrl = url;
    //            //用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题、描述
    //            WXMediaMessage msg = new WXMediaMessage(webpage);
    //            msg.title = title;
    //            msg.description = detail;
    //            Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);//压缩Bitmap
    //            Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, 150, 150, true);
    //            thumb.recycle();
    //            msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
    //            //构造一个Req
    //            SendMessageToWX.Req req = new SendMessageToWX.Req();
    //            req.transaction = buildTransaction("webpage");//transaction字段用于唯一标识一个请求
    //            req.message = msg;
    //            req.scene = SendMessageToWX.Req.WXSceneSession;
    //            //调用api接口发送数据到微信
    //            api.sendReq(req);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    private String buildTransaction(final String type) {
    //        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    //    }
    //
    //    private void showUploadCamera() {
    //        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
    //
    //        alertDialog.setTitle("请选择操作");
    //        // gallery, camera.
    //        String[] options = {"相册", "拍照"};
    //
    //        alertDialog.setItems(options, new DialogInterface.OnClickListener() {
    //
    //            @Override
    //            public void onClick(DialogInterface dialog, int which) {
    //                if (which == 0) {
    //                    try {
    //                        PickImage.pickImageFromPhoto(MainActivity.this, 1);//相册
    //
    //                    } catch (Exception e) {
    //                        e.printStackTrace();
    //                        Toast.makeText(mContext,
    //                                "请去\"设置\"中开启本应用的图片媒体访问权限",
    //                                Toast.LENGTH_SHORT).show();
    //                    }
    //                } else {
    //                    try {
    //                        PickImage.pickImageFromCamera(MainActivity.this,
    //                                personalVehicleTempFileDir + "danju.jpg", 2);//拍照
    //                    } catch (Exception e) {
    //                        e.printStackTrace();
    //                        Toast.makeText(mContext,
    //                                "请去\"设置\"中开启本应用的相机和图片媒体访问权限",
    //                                Toast.LENGTH_SHORT).show();
    //
    //                    }
    //                }
    //            }
    //        });
    //        alertDialog.show();
    //    }
    //
    //    /**
    //     * 处理图片
    //     *
    //     * @param avatarFile
    //     * @return
    //     */
    //    private byte[] handleImage(File avatarFile, int out_Width, int out_Height) {
    //        if (avatarFile.exists()) { // 本地文件存在
    //            BitmapFactory.Options options = new BitmapFactory.Options();
    //            options.inJustDecodeBounds = true;
    //            // 获取这个图片原始的宽和高 在outHeight 及 outWidth
    //            Bitmap bm = BitmapFactory.decodeFile(avatarFile.getPath(), options);
    //
    //            // 此时返回bm为空
    //            // 我们要得到高及宽都不超过W H的缩略图
    //            int zW = options.outWidth / out_Width;
    //            int zH = options.outHeight / out_Height;
    //            int be = zH;
    //            if (zW > be)
    //                be = zW;
    //            if (be == 0)
    //                be = 1;
    //            options.inSampleSize = be;
    //            options.inJustDecodeBounds = false;
    //            bm = BitmapFactory.decodeFile(avatarFile.getPath(), options);
    //
    //            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    //
    //            if (bm == null) {
    //                return null;
    //            }
    //            bm.copy(Bitmap.Config.ARGB_8888, false);
    //
    //            // TODO--
    //            bm.compress(
    //                    avatarFile.getAbsolutePath().endsWith("jpg") ? Bitmap.CompressFormat.JPEG
    //                            : Bitmap.CompressFormat.PNG, 50, outputStream);
    //
    //            return outputStream.toByteArray();
    //
    //        }
    //        return null;
    //    }
}
