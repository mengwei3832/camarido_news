package com.yunqitechbj.clientandroid.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;

import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.activity.LoginActivity;
import com.yunqitechbj.clientandroid.activity.MainActivity;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;
import com.yunqitechbj.clientandroid.entity.VehicleNoModel;
import com.yunqitechbj.clientandroid.utils.ImageUtil;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.NetworkAvailable;
import com.yunqitechbj.clientandroid.utils.PermissionUtil;
import com.yunqitechbj.clientandroid.utils.PreManager;
import com.yunqitechbj.clientandroid.utils.StringUtils;
import com.yunqitechbj.clientandroid.weight.Config;
import com.yunqitechbj.clientandroid.weight.InitWebView;
import com.yunqitechbj.clientandroid.weight.MyWebChomeClient;
import com.yunqitechbj.clientandroid.weight.MyWebViewDownLoadListener;
import com.yunqitechbj.clientandroid.weight.SyncCookie;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 车辆管理页面
 * Created by mengwei on 2017/5/19.
 */

public class VehicleFragment extends BaseFragment implements MyWebChomeClient.OpenFileChooserCallBack {
    //页面控件对象
    private View progress_vehicle;
    private View title_vehicle;
    private TextView tvTitle;
    private WebView wv_vehicle;

    private Intent mSourceIntent;
    private ValueCallback<Uri[]> mUploadMsgForAndroid5;
    private ValueCallback<Uri> mUploadMessage;

    private SyncCookie mSyncCookie;
    private InitWebView mInitWebView;

    private PreManager mPreManager;

    private String VEHICLE_URI = Config.API.WEB_VEHICLE_URL;

    // IWXAPI：第三方APP和微信通信的接口
    public static IWXAPI api;

    /* 存放图片文件夹的同一路径 */
    private String personalVehicleTempFileDir = Environment
            .getExternalStorageDirectory() + "/";

    /* 存放矿发图片文件夹的路径 */
    public String danJuImgName = "danju.jpg";
    public File danjuImageFile = null;
    public String imgDanjuBase64 = null;

    private static final int P_CODE_PERMISSIONS = 101;
    private static final int REQUEST_CODE_PICK_IMAGE = 0;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_vehicle;
    }

    @Override
    protected void initView(View _rootView) {
        progress_vehicle = obtainView(R.id.progress_vehicle);
        title_vehicle = obtainView(R.id.title_vehicle);
        wv_vehicle = obtainView(R.id.wv_vehicle);
        tvTitle = (TextView) title_vehicle.findViewById(R.id.tv_title);
    }

    @Override
    protected void initData() {
        mPreManager = PreManager.instance(getActivity());
        mSyncCookie = new SyncCookie(getActivity());
        mInitWebView = new InitWebView(wv_vehicle, getActivity());
        //初始化webview
        initWebView();
    }

    private void initWebView() {
        //webview初始化
        mInitWebView.initWebView();
        wv_vehicle.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //cookie过期，js调用Android方法,window.back.goBackLogin() 的方式调用
                wv_vehicle.addJavascriptInterface(new JsInteration(), "back");

        // 设置Web视图
        wv_vehicle.setWebViewClient(new webViewClient());
        wv_vehicle.setWebChromeClient(new MyWebChomeClient(this, tvTitle));
        wv_vehicle.setDownloadListener(new MyWebViewDownLoadListener(getActivity()));

        // 加载需要显示的网页
        wv_vehicle.loadUrl(VEHICLE_URI, mSyncCookie.syncCookie(VEHICLE_URI));
    }

    /**
     * Web视图
     */
    private class webViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progress_vehicle.setVisibility(View.VISIBLE);
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
            progress_vehicle.setVisibility(View.GONE);
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

    public boolean onBackPressed() {
        if (wv_vehicle.canGoBack()) {
            wv_vehicle.goBack();
            return true;
        }
        return false;
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

    private class DialogOnCancelListener implements DialogInterface.OnCancelListener {
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
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
        alertDialog.setOnCancelListener(new DialogOnCancelListener());

        alertDialog.setTitle("请选择操作");
        // gallery, camera.
        String[] options = {"相册", "拍照"};

        alertDialog.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (PermissionUtil.isOverMarshmallow()) {
                        if (!PermissionUtil.isPermissionValid(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Toast.makeText(getActivity(),
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
                        Toast.makeText(getActivity(),
                                "请去\"设置\"中开启本应用的图片媒体访问权限",
                                Toast.LENGTH_SHORT).show();
                        restoreUploadMsg();
                    }
                } else {
                    if (PermissionUtil.isOverMarshmallow()) {
                        if (!PermissionUtil.isPermissionValid(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(getActivity(),
                                    "请去\"设置\"中开启本应用的图片媒体访问权限",
                                    Toast.LENGTH_SHORT).show();

                            restoreUploadMsg();
                            requestPermissionsAndroidM();
                            return;
                        }

                        if (!PermissionUtil.isPermissionValid(getActivity(), Manifest.permission.CAMERA)) {
                            Toast.makeText(getActivity(),
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
                        Toast.makeText(getActivity(),
                                "请去\"设置\"中开启本应用的相机和图片媒体访问权限",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        restoreUploadMsg();
                    }
                }
            }
        });
        alertDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //                switch (requestCode){
        //                    case 1://图库返回
        //                        if (resultCode == RESULT_OK){
        //                            Uri uri = data.getData();
        //                            String realPathFromURI = FileUtils.getRealPathFromURI(uri,this);
        //                            if (TextUtils.isEmpty(realPathFromURI)) {
        //                                danjuImageFile = null;
        //                            } else {
        //                                danjuImageFile = new File(personalVehicleTempFileDir
        //                                        + "danju.jpg");
        //                                File temFile = new File(realPathFromURI);
        //
        //                                FileUtils.copyFile(temFile, danjuImageFile);
        //                            }
        //                        }
        //                        break;
        //                    case 2://矿发数据拍照返回
        //                        if (resultCode == RESULT_OK){
        //                            danjuImageFile = new File(personalVehicleTempFileDir + "danju.jpg");
        //                        }
        //                        break;
        //                }
        //
        //                if (requestCode == 1){
        //                    // 判断本地文件是否存在
        //                    if (danjuImageFile == null) {
        //                        return;
        //                    }
        //
        //                    // TODO--
        //                    byte[] bytes = handleImage(danjuImageFile, 800, 800);
        //
        //                    if (bytes == null || bytes.length == 0) {
        //                        return;
        //                    }
        //                    int degree = ImageUtil.readPictureDegree(danjuImageFile
        //                            .getAbsolutePath());
        //
        //                    imgDanjuBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
        //
        //                    // base64解码
        //                    byte[] decode = Base64.decode(imgDanjuBase64, Base64.NO_WRAP);
        //
        //                    // TODO--将图片保存在SD卡
        //                    BufferedOutputStream bos = null;
        //                    FileOutputStream fos = null;
        //                    File file = new File(personalVehicleTempFileDir
        //                            + "danju.jpg");
        //                    try {
        //                        if (file.exists()) {
        //                            file.createNewFile();
        //                        }
        //                        fos = new FileOutputStream(file);
        //                        bos = new BufferedOutputStream(fos);
        //                        bos.write(decode);
        //                        fos.flush();
        //                    } catch (Exception e) {
        //                        e.printStackTrace();
        //                    } finally {
        //                        if (bos != null) {
        //                            try {
        //                                bos.close();
        //                            } catch (IOException e1) {
        //                                e1.printStackTrace();
        //                            }
        //                        }
        //                        if (fos != null) {
        //                            try {
        //                                fos.close();
        //                            } catch (IOException e1) {
        //                                e1.printStackTrace();
        //                            }
        //                        }
        //                    }
        //                }
        //
        //                if (requestCode == 2){
        //                    // 判断本地文件是否存在
        //                    if (danjuImageFile == null) {
        //                        return;
        //                    }
        //
        //                    // TODO--
        //                    byte[] bytes = handleImage(danjuImageFile, 800, 800);
        //
        //                    if (bytes == null || bytes.length == 0) {
        //                        return;
        //                    }
        //                    int degree = ImageUtil.readPictureDegree(danjuImageFile
        //                            .getAbsolutePath());
        //
        //                    imgDanjuBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
        //
        //                    // base64解码
        //                    byte[] decode = Base64.decode(imgDanjuBase64, Base64.NO_WRAP);
        //
        //                    // TODO--将图片保存在SD卡
        //                    BufferedOutputStream bos = null;
        //                    FileOutputStream fos = null;
        //                    File file = new File(personalVehicleTempFileDir
        //                            + "danju.jpg");
        //                    try {
        //                        if (file.exists()) {
        //                            file.createNewFile();
        //                        }
        //                        fos = new FileOutputStream(file);
        //                        bos = new BufferedOutputStream(fos);
        //                        bos.write(decode);
        //                        fos.flush();
        //                    } catch (Exception e) {
        //                        e.printStackTrace();
        //                    } finally {
        //                        if (bos != null) {
        //                            try {
        //                                bos.close();
        //                            } catch (IOException e1) {
        //                                e1.printStackTrace();
        //                            }
        //                        }
        //                        if (fos != null) {
        //                            try {
        //                                fos.close();
        //                            } catch (IOException e1) {
        //                                e1.printStackTrace();
        //                            }
        //                        }
        //                    }
        //                }

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

                        String sourcePath = ImageUtil.retrievePath(getActivity(), mSourceIntent, data);

                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {
                            Log.e("TAG", "sourcePath empty or not exists.");
                            break;
                        }

                        L.e("=====sourcePath======" + sourcePath.toString());

                        Uri uri = Uri.fromFile(new File(sourcePath));
                        mUploadMessage.onReceiveValue(uri);

                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (mUploadMsgForAndroid5 == null) {        // for android 5.0+
                            return;
                        }

                        String sourcePath = ImageUtil.retrievePath(getActivity(), mSourceIntent, data);

                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {
                            Log.e("TAG", "sourcePath empty or not exists.");
                            break;
                        }

                        L.e("------------sourcePath--------------" + sourcePath.toString());

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

    private void requestPermissionsAndroidM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> needPermissionList = new ArrayList<>();
            needPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            needPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            needPermissionList.add(Manifest.permission.CAMERA);
            PermissionUtil.requestPermissions(getActivity(), P_CODE_PERMISSIONS, needPermissionList);

        } else {
            return;
        }
    }

    /**
     * 返回登录界面（token过期）
     * 供js调用的方法
     */
    public class JsInteration {
        @JavascriptInterface
        public void goBackLogin() {
            L.e("----------cookie过期，回到登陆界面------------");
            //cookie过期，回到登陆界面
            LoginActivity.invoke(getActivity());
            getActivity().finish();
        }

        /**
         * 获取派车数
         *
         * @param strJson
         */
        @JavascriptInterface
        public void getVehicleList(String strJson) {
            Type listType = new TypeToken<ArrayList<VehicleNoModel>>() {
            }.getType();
            ArrayList<VehicleNoModel> vehicleList = new Gson().fromJson(strJson, listType);

            L.e("--------vehicleList---------" + vehicleList.toString());
            if (vehicleList.size() != 0) {
                for (int i = 0; i < vehicleList.size(); i++) {
                    OfflineStorage offlineStorage = new OfflineStorage();
                    offlineStorage.setVehicleName(vehicleList.get(i).getVehicleNo());
                    offlineStorage.setVehiclePhone(vehicleList.get(i).getTel());
                    offlineStorage.setTicketId(vehicleList.get(i).getTicketId());
                    offlineStorage.setTicketCode(vehicleList.get(i).getTicketCode());
                    offlineStorage.setInfoId(vehicleList.get(i).getInfoId());
                    offlineStorage.save();
                }
            }


        }

        /**
         * 获取矿发数据
         *
         * @param weightInit 矿发吨数
         * @param initTime   矿发时间
         * @param initImg    矿发单据
         */
        @JavascriptInterface
        public void getInitData(String ticketId, String weightInit, String initTime, String initImg) {
            //获取当前时间
            String mCurrentTime = StringUtils.getCurrentDate();
            long mCurrentLong = StringUtils.getStringToDate(mCurrentTime);//当前时间
            L.e("---------mCurrentTime-----------" + mCurrentTime);
            L.e("----------mCurrentLong-------------" + mCurrentLong);
            L.e("-------ticketId--------" + ticketId);
            L.e("-------weightInit--------" + weightInit);
            L.e("-------initTime--------" + initTime);
            L.e("-------initImg--------" + initImg);
            //判断数据库中该数据是否完善
            List<OfflineStorage> offlist = DataSupport.where("ticketId = ?", ticketId).find(OfflineStorage.class);
            L.e("-------offlist--------" + offlist.toString());

            if (offlist.size() != 0) {
                String mWeightReach = offlist.get(0).getSignedDun();
                String mReachTime = offlist.get(0).getSignedDate();
                if (StringUtils.isEmpty(mWeightReach) && StringUtils.isEmpty(mReachTime)) {
                    ContentValues values = new ContentValues();
                    values.put("minehairDun", weightInit);
                    values.put("minehairDate", initTime);
                    if (!StringUtils.isEmpty(initImg)) {
                        values.put("minehireImg", initImg);
                    }
                    values.put("updateTime", mCurrentLong);
                    DataSupport.updateAll(OfflineStorage.class, values, "ticketId = ?", ticketId);
                } else {
                    DataSupport.deleteAll(OfflineStorage.class, "ticketId = ?", ticketId);
                }
            }
        }

        /**
         * 获取签收数据
         *
         * @param weightReach 签收吨数
         * @param reachTime   签收时间
         * @param reachImg    签收单据
         */
        @JavascriptInterface
        public void getReachData(String ticketId, String weightReach, String reachTime, String reachImg) {
            //获取当前时间
            String mCurrentTime = StringUtils.getCurrentDate();
            long mCurrentLong = StringUtils.getStringToDate(mCurrentTime);//当前时间
            L.e("---------mCurrentTime-----------" + mCurrentTime);
            L.e("----------mCurrentLong-------------" + mCurrentLong);
            L.e("-------ticketId--------" + ticketId);
            L.e("-------weightReach--------" + weightReach);
            L.e("-------reachTime--------" + reachTime);
            L.e("-------reachImg--------" + reachImg);
            //判断数据库中该数据是否完善
            List<OfflineStorage> offlist = DataSupport.where("ticketId = ?", ticketId).find(OfflineStorage.class);
            L.e("-------offlist--------" + offlist.toString());

            if (offlist.size() != 0) {
                String mWeightInit = offlist.get(0).getMinehairDun();
                String mInitTime = offlist.get(0).getMinehairDate();
                if (StringUtils.isEmpty(mWeightInit) && StringUtils.isEmpty(mInitTime)) {
                    ContentValues values = new ContentValues();
                    values.put("signedDun", weightReach);
                    values.put("signedDate", reachTime);
                    if (!StringUtils.isEmpty(reachImg)) {
                        values.put("signedImg", reachImg);
                    }
                    values.put("updateTime", mCurrentLong);
                    DataSupport.updateAll(OfflineStorage.class, values, "ticketId = ?", ticketId);
                } else {
                    DataSupport.deleteAll(OfflineStorage.class, "ticketId = ?", ticketId);
                }
            }
        }

        @JavascriptInterface
        public String getToken() {
            //获取cookies
            String cookies = mPreManager.getToken();
            return cookies;
        }

        @JavascriptInterface
        public void setWXToken(String token) {
            L.e("------传过来的Token------" + token);
            mPreManager.setToken(token);
        }

        /**
         * 获取微信分享的内容
         *
         * @param url
         */
//        @JavascriptInterface
//        public void setWXShare(String title, String url, String detail) {
//            L.e("------title---------" + title);
//            L.e("------url---------" + url);
//            L.e("------detail---------" + detail);
//            shareText(title, url, detail);
//        }

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

    }
}
