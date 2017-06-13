package com.yunqitechbj.clientandroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.entity.LoginInfo;
import com.yunqitechbj.clientandroid.http.AsyncHttp;
import com.yunqitechbj.clientandroid.http.request.IRequest;
import com.yunqitechbj.clientandroid.http.response.Response;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.PreManager;
import com.yunqitechbj.clientandroid.utils.T;
import com.yunqitechbj.clientandroid.weight.PermissionsResultListener;
import com.yunqitechbj.clientandroid.weight.ReactiveNetwork;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by mengwei on 2016/10/28.
 */

public abstract class BaseActivity extends FragmentActivity implements AsyncHttp.IHttpListener {
    protected Context mContext;
    private Dialog mLoadingDialog;
    protected Handler mHandler = new Handler();
    public View view;
    public View mainView;
    protected AsyncHttp mHttp = new AsyncHttp();

    /* actionbar 控件对象 */
    private TextView tvTitle,tvRight;
    private ImageButton ibLeftImage,ibRightImage;

    private ReactiveNetwork mReactiveNetwork;
    private Subscription internetConnectivitySubscription;

    private android.support.v7.app.AlertDialog builder;
    public PreManager mPreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mContext = this;
        if (getLayoutId() != 0) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mainView = inflater.inflate(getLayoutId(), null, true);
            setContentView(mainView);
        }

        mReactiveNetwork = new ReactiveNetwork();

        initView();
        setListener();
        initData();

        mPreManager = PreManager.instance(mContext);

        //监听是否链接互联网的 （ 是 ， 否）
        internetConnectivitySubscription =
                mReactiveNetwork.observeInternetConnectivity()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                initStatus(aBoolean);
                            }
                        }) ;


    }

    /**
     * 判断网络连接
     * @param isNet
     */
    protected abstract void initStatus(boolean isNet);

    /**
     * 显示等待的弹窗
     * @param message
     */
    protected void showWaitDialog(String message){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View net_view = layoutInflater.inflate(R.layout.dialog_wait_tishi, null);

        TextView tv = (TextView) net_view.findViewById(R.id.tv_dialog_wait);
        tv.setText(message);

        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder = dialog.create();
        builder.setView(net_view);
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        builder.show();
    }

    /**
     * 隐藏等待的弹窗
     */
    protected void hideWaitDialog(){
        builder.dismiss();
    }

//    /**
//     * 网络状态监测
//     */
//    private void intentStatusChange(){
//        // 网络改变的一个回掉类
//        mNetChangeObserver = new NetChangeObserver() {
//            @Override
//            public void onNetConnected(NetUtils.NetType type) {
//                onNetworkConnected(type);
//            }
//
//            @Override
//            public void onNetDisConnect() {
//                onNetworkDisConnected();
//            }
//        };
//
//        //开启广播去监听 网络 改变事件
//        NetStateReceiver.registerObserver(mNetChangeObserver);
//    }

//    /**
//     * 网络连接状态
//     *
//     * @param type 网络状态
//     */
//    protected abstract void onNetworkConnected(NetUtils.NetType type);
//
//    /**
//     * 网络断开的时候调用
//     */
//    protected abstract void onNetworkDisConnected();

    /**
     * 返回当前界面布局文件
     *
     * @return
     * @throws
     * @Title:getLayoutId
     * @Description:
     * @return:int
     * @Create: 2014年12月8日 下午2:40:17
     * @Author : zhm
     */
    protected abstract int getLayoutId();

    /**
     * 此方法描述的是： 初始化所有view
     *
     * @author: zhm
     * @version: 2014-3-12 下午3:17:28
     */
    protected abstract void initView();

    /**
     * 此方法描述的是： 初始化所有数据的方法
     *
     * @author: zhm
     * @version: 2014-3-12 下午3:17:46
     */
    protected abstract void initData();

    /**
     * 此方法描述的是： 设置所有事件监听
     *
     * @author: zhangwb
     * @version: 2015-11-20 上午0:10:21
     */
    protected abstract void setListener();

    protected void httpPost(IRequest request) {
        mHttp.post(request, this);
    }

    protected void httpGet(IRequest request) {
        mHttp.get(request, this);
    }

    /**
     * 直接把JSON数据提交到服务器
     *
     * @Description:
     * @Title:httpPostJson
     * @param request
     * @return:void
     * @throws
     * @Create: 2015年11月22日 上午12:15:38
     * @Author : zhm
     */
    protected void httpPostJson(IRequest request) {
        mHttp.postJson(request, this);
    }

    @Override
    public void onStart(int requestId) {
        // TODO Auto-generated method stub

    }

    // ////////////////////Activity生命周期///////////////////////
    private String getClassName() {
        String canonicalName = this.getClass().getCanonicalName();
        String[] split = canonicalName.split("\\.");
        return split[split.length - 1];
    }

    @Override
    public void onSuccess(int requestId, Response response) {
        if (response.isSuccess) {
            if (response.singleData instanceof LoginInfo) {
                LoginInfo loginInfo = (LoginInfo) response.singleData;
                PreManager.instance(this).setToken(loginInfo.tokenValue);
//                UserUtil.setUserId(this, loginInfo.userId);
//                PreManager.instance(this).setTokenExpires(
//                        loginInfo.tokenExpires);
            }
        } else {
            Log.d("TAG", "--------response.ErrCode---------" + response.ErrCode);
            if (response.ErrCode == 10001) {
                // TODO 退出登录
                // 删除token过期时间
                PreManager.instance(mContext).removeTokenExpires();
                // 清空userId
//                UserUtil.unSetUserId(mContext);
//                // 跳转到登录界面
//                LoginActicity.invoke((Activity) mContext);
//                // 用户退出统计
//                MobclickAgent.onProfileSignOff();
//                // finish主界面
//                ((Activity) mContext).finish();
//                CamaridoApp.destoryActivity("EmployerMainActivity");
            }
        }
    }

    @Override
    public void onFailure(int requestId, int httpCode, Throwable error) {
        // TODO Auto-generated method stub

    }

    public <T extends View> T obtainView(int resId) {
        return (T) findViewById(resId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        safelyUnsubscribe(internetConnectivitySubscription);
    }

    private void safelyUnsubscribe(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }

    private PermissionsResultListener mListener;

    private int mRequestCode;

    /**
     * 其他 Activity 继承 BaseActivity 调用 performRequestPermissions 方法
     *
     * @param desc        首次申请权限被拒绝后再次申请给用户的描述提示
     * @param permissions 要申请的权限数组
     * @param requestCode 申请标记值
     * @param listener    实现的接口
     */
    protected void performRequestPermissions(String desc, String[] permissions, int requestCode, PermissionsResultListener listener) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        mRequestCode = requestCode;
        mListener = listener;
        L.e("---------Build.VERSION.SDK_INT----------"+Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkEachSelfPermission(permissions)) {// 检查是否声明了权限
                requestEachPermissions(desc, permissions, requestCode);
            } else {// 已经申请权限
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            }
        } else {
            if (mListener != null) {
                mListener.onPermissionGranted();
            }

            L.e("-------checkEachSelfPermission(permissions)--------"+checkEachSelfPermission(permissions));

            if (checkEachSelfPermission(permissions)) {// 检查是否声明了权限
                T.showShort(mContext,"开通权限");
            }
        }
    }

    /**
     * 申请权限前判断是否需要声明
     *
     * @param desc
     * @param permissions
     * @param requestCode
     */
    private void requestEachPermissions(String desc, String[] permissions, int requestCode) {
        if (shouldShowRequestPermissionRationale(permissions)) {// 需要再次声明
            showRationaleDialog(desc, permissions, requestCode);
        } else {
            ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
        }
    }

    /**
     * 弹出声明的 Dialog
     *
     * @param desc
     * @param permissions
     * @param requestCode
     */
    private void showRationaleDialog(String desc, final String[] permissions, final int requestCode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage(desc)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }


    /**
     * 再次申请权限时，是否需要声明
     *
     * @param permissions
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检察每个权限是否申请
     *
     * @param permissions
     * @return true 需要申请权限,false 已申请权限
     */
    private boolean checkEachSelfPermission(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 申请权限结果的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode) {
            if (checkEachPermissionsGranted(grantResults)) {
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            } else {// 用户拒绝申请权限
                if (mListener != null) {
                    mListener.onPermissionDenied();
                }
            }
        }
    }

    /**
     * 检查回调结果
     *
     * @param grantResults
     * @return
     */
    private boolean checkEachPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
