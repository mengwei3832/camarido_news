package com.yunqitechbj.clientandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.entity.LoginInfo;
import com.yunqitechbj.clientandroid.entity.Phone;
import com.yunqitechbj.clientandroid.entity.PushInfo;
import com.yunqitechbj.clientandroid.http.HostUtil;
import com.yunqitechbj.clientandroid.http.request.GetLoginCode;
import com.yunqitechbj.clientandroid.http.request.GetPushInfoRequest;
import com.yunqitechbj.clientandroid.http.request.LoginRequest;
import com.yunqitechbj.clientandroid.http.response.Response;
import com.yunqitechbj.clientandroid.utils.CacheUtils;
import com.yunqitechbj.clientandroid.utils.DialogUtils;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.MiPushUtil;
import com.yunqitechbj.clientandroid.utils.PreManager;
import com.yunqitechbj.clientandroid.utils.StringUtils;
import com.yunqitechbj.clientandroid.utils.T;
import com.yunqitechbj.clientandroid.weight.TimeCountUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 登录页面
 * Created by mengwei on 2016/10/25.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    /* 控件对象 */
    private TextView mTvRegister;
    private EditText mEtName;
    private EditText mEtPass;
    private CheckBox mCbCheck;
    private Button mBtLogin;
    private Button btLoginCode;
    private TextView tvSurroundings;

    /* 保存的类 */
    private PreManager mPreManager;

    /* 请求类 */
    private LoginRequest mLoginRequest;
    private GetPushInfoRequest mGetPushInfoRequest;
    private GetLoginCode mGetLoginCode;

    /* 请求Id */
    private final int GET_LOGIN = 1;
    private final int GET_PUSH = 2;
    private final int GET_CODE = 3;

    /* 输入框的值 */
    private String mName;
    private String mPass;

    // 存放SP的key
    private final String TOKENVALUE = "TokenValue";
    public static final String USER_NAME = "USER_NAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String USER_TYPE = "USER_TYPE";

    /* 等待提示弹窗 */
    private AlertDialog builder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        mPreManager = PreManager.instance(this);

        //初始化控件对象
        initFindView();
    }

    @Override
    protected void initData() {
        //判断当前的环境
        getCurrentSurroundings();
        //设置自动登录状态
        setAutoCheckStatus();
        //判断用户名是否存在，并赋值
        setUserNameEdit();
    }

    /**
     * 判断当前的环境
     */
    private void getCurrentSurroundings(){
        String mSurroundings = HostUtil.getWebHost();
        if (mSurroundings.equals("http://dev.yqtms.com/")){
            tvSurroundings.setText("dev");
        } else if (mSurroundings.equals("http://qa.yqtms.com/")){
            tvSurroundings.setText("测试");
        } else if (mSurroundings.equals("http://demo.yqtms.com/")){
            tvSurroundings.setText("demo");
        } else if (mSurroundings.equals("http://yqtms.com/")){
            tvSurroundings.setText("");
        }
    }

    @Override
    protected void setListener() {
        //初始化监听事件
        initOnclick();
    }

    /**
     * 设置自动登录状态
     */
    private void setAutoCheckStatus() {
        //获取自动登录状态
        boolean mAutoStatus = mPreManager.getAutoLoginStatus();

        L.e("---------mAutoStatus-----------" + mAutoStatus);

        //设置是否选中
        if (mAutoStatus) {
            mCbCheck.setChecked(true);
            mPreManager.setAutoLoginStatus(true);
        } else {
            mCbCheck.setChecked(false);
            mPreManager.setAutoLoginStatus(false);
        }
    }

    /**
     * 判断用户名是否存在，并赋值
     */
    private void setUserNameEdit(){
        //获取保存的用户名
        String mUserName = mPreManager.getUserName();

        if (!StringUtils.isEmpty(mUserName)){
            mEtName.setText(mUserName);

        }
    }

    /**
     * 初始化监听事件
     */
    private void initOnclick() {
        mTvRegister.setOnClickListener(this);
        mBtLogin.setOnClickListener(this);
        mCbCheck.setOnCheckedChangeListener(this);
        btLoginCode.setOnClickListener(this);
    }

    /**
     * 初始化控件对象
     */
    private void initFindView() {
        mTvRegister = obtainView(R.id.tv_login_register);
        mEtName = obtainView(R.id.et_login_name);
        mEtPass = obtainView(R.id.et_login_pass);
        mBtLogin = obtainView(R.id.bt_login);
        mCbCheck = obtainView(R.id.cb_login_check);
        btLoginCode = obtainView(R.id.bt_login_code);
        tvSurroundings = obtainView(R.id.tv_login_surroundings);
    }

    @Override
    public void onStart(int requestId) {
        super.onStart(requestId);
    }

    @Override
    public void onSuccess(int requestId, Response response) {
        super.onSuccess(requestId, response);
        boolean isSuccess;
        String messgae;
        switch (requestId) {
            case GET_LOGIN:
                isSuccess = response.isSuccess;
                messgae = response.message;
                if (isSuccess) {
                    LoginInfo mLoginInfo = (LoginInfo) response.singleData;
                    String tokenValue = mLoginInfo.tokenValue;
                    String userId = mLoginInfo.userId;
                    String tokenExpires = mLoginInfo.tokenExpires;
                    String userType = mLoginInfo.userType;
                    boolean isTempUser = mLoginInfo.isTempUser;
                    int userRegistStatus = mLoginInfo.userRegistStatus;

                    String mCookies = "DA46C837-94AF-46C4-A88A-7D37FDCB8D7C";

                    L.e("-----------tokenValue------------" + tokenValue);
                    String str1 = tokenExpires.substring(0,10);
                    String str2 = tokenExpires.substring(11,16);
                    L.e(str1+" "+str2);

                    tokenExpires = str1 + " " + str2;

                    // 登录成功后保存账号密码到SP
                    CacheUtils.putString(getApplicationContext(), USER_NAME,
                            mName);
                    // 保存登录返回的token到SP
                    //                    CacheUtils.putString(getApplicationContext(), TOKENVALUE,
                    //                            mCookies);
                    mPreManager.setToken(tokenValue);
                    mPreManager.setUserName(mName);
                    mPreManager.setTokenExpires(tokenExpires);

                    //获取推送相关请求
                    getPushRequest();

                    if (userRegistStatus == 40){
                        //登录成功跳转到主页面
                        MainActivity.invoke(mContext,0);
                        //记录登陆成功
                        mPreManager.setIsLoginInput(true);
                        //关闭当前页面
                        finish();
                    } else if (userRegistStatus == 30){
                        T.showShort(mContext,messgae);
                        setLoginButtonEnabled(true);
                        mPreManager.setIsLoginInput(false);
                    } else if (userRegistStatus == 20){
                        RegisterActivity.invoke(LoginActivity.this, userRegistStatus, mName);
                        setLoginButtonEnabled(true);
                        mPreManager.setIsLoginInput(false);
                    }
                } else {
                    T.showShort(mContext, messgae);
                    setLoginButtonEnabled(true);
                    mPreManager.setIsLoginInput(false);
                }
                break;

            case GET_PUSH:
                isSuccess = response.isSuccess;
                messgae = response.message;
                if (isSuccess) {
                    List<PushInfo> pushInfos = response.data;
                    for (PushInfo pushInfo : pushInfos) {
                        if (pushInfo.pushDataType == 1) {
                            MiPushUtil.setMiPushTopic(LoginActivity.this,
                                    pushInfo.receiverMark);
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(messgae) && messgae != null) {
                        T.showShort(mContext, messgae);
                    }
                }
                break;
            case GET_CODE://获取验证码
                isSuccess = response.isSuccess;
                messgae = response.message;
                if (isSuccess){
                    hideWaitDialog();
                    // 验证码倒计时
                    TimeCountUtil countTimer = new TimeCountUtil(btLoginCode);
                    countTimer.start();

                    T.showShort(mContext,messgae);
                } else {
                    hideWaitDialog();
                    T.showShort(mContext,messgae);
                }
                setLoginCodeEnabled(true);
                break;
        }
    }

    @Override
    public void onFailure(int requestId, int httpCode, Throwable error) {
        super.onFailure(requestId, httpCode, error);
        T.showShort(mContext, this.getResources().getString(R.string.request_failure));
        switch (requestId) {
            case GET_LOGIN://登录失败处理
                setLoginButtonEnabled(true);
                break;
            case GET_CODE://获取验证码失败处理
                setLoginCodeEnabled(true);
                hideWaitDialog();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login_register://进入注册页面
                RegisterActivity.invoke(LoginActivity.this, 0, "");
                break;
            case R.id.bt_login://点击登录
                String mCookies = "03BCB905-76C2-42AB-863D-38B3D3672BD6";
                mPreManager.setToken(mCookies);
                setLoginButtonEnabled(false);
                if (isComplate()) {
                    //请求登录的接口
                    getLoginRequest();
                    //进入主页面
//                  MainActivity.invoke(LoginActivity.this);
                } else {
                    setLoginButtonEnabled(true);
                    mPreManager.setIsLoginInput(false);
                }
                break;

            case R.id.bt_login_code://获取验证码
                //获取用户名输入框的值
                String call = mEtName.getText().toString().trim();

//                ArrayList<Phone> list = StringUtils.getAllContacts(mContext);
//
//                L.e("=========list==========="+list.toString());
//
//                T.showShort(mContext,"======list======"+list.toString());

                if (StringUtils.isEmpty(call)){
                    T.showShort(mContext,"请输入用户名");
                    return;
                } else {
                    L.e("----------用户名输入框的值----------" + call);
                    if (!StringUtils.isPhone(call)){
                        T.showShort(mContext,"请输入正确手机号");
                        return;
                    } else {
                        showWaitDialog("正在获取，请稍候...");
                        getLoginCodeRequest(call);
                        setLoginCodeEnabled(false);
                    }
                }
                break;
        }
    }

    /**
     * 等待提示窗
     * @param
     */
    public void waitDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View net_view = layoutInflater.inflate(R.layout.dialog_wait_tishi, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        builder = dialog.create();
        builder.setView(net_view);
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        builder.show();
    }

    /**
     * 请求登录接口
     */
    private void getLoginRequest() {
        L.e("--------mName---------" + mName);
        L.e("-----------mPass-------------" + mPass);

        mLoginRequest = new LoginRequest(LoginActivity.this, mName, mPass);
        mLoginRequest.setRequestId(GET_LOGIN);
        httpPost(mLoginRequest);
    }

    /**
     * q请求推送相关接口
     */
    private void getPushRequest() {
        mGetPushInfoRequest = new GetPushInfoRequest(mContext);
        mGetPushInfoRequest.setRequestId(GET_PUSH);
        httpPost(mGetPushInfoRequest);
    }

    /**
     * 请求登录验证码接口
     *
     * @param call
     */
    private void getLoginCodeRequest(String call) {
        mGetLoginCode = new GetLoginCode(mContext, call);
        mGetLoginCode.setRequestId(GET_CODE);
        httpPost(mGetLoginCode);
    }

    /**
     * 判断输入框的值
     *
     * @return
     */
    private boolean isComplate() {
        //获取输入框的值
        mName = mEtName.getText().toString().trim();
        mPass = mEtPass.getText().toString().trim();

        //判断用户名
        if (StringUtils.isEmpty(mName)) {
            T.showShort(LoginActivity.this, "请输入用户名");
            return false;
        }
        //判断用户名手机号
        if (!StringUtils.isPhone(mName)) {
            T.showShort(LoginActivity.this, "请输入正确手机号");
            return false;
        }
        //判断密码
        if (StringUtils.isEmpty(mPass)) {
            T.showShort(LoginActivity.this, "请输入验证码");
            return false;
        }
        //判断密码是否符合要求
        /*if (!StringUtils.isPass(mPass)) {
            T.showShort(LoginActivity.this, "密码必须为6~14位数字或字母,请重新输入");
            return false;
        }*/
        return true;
    }

    /**
     * 设置登录按钮置灰
     *
     * @param isEnabled
     */
    private void setLoginButtonEnabled(boolean isEnabled) {
        if (isEnabled) {
            mBtLogin.setEnabled(true);
            mBtLogin.setText("登录");
            mBtLogin.setBackgroundResource(R.color.color_00bbff);
        } else {
            mBtLogin.setEnabled(false);
            mBtLogin.setText("登录中...");
            mBtLogin.setBackgroundResource(R.color.color_bebebe);
        }
    }

    /**
     * 设置获取验证码按钮置灰
     * @param isEnabled
     */
    private void setLoginCodeEnabled(boolean isEnabled) {
        if (isEnabled){
            btLoginCode.setEnabled(isEnabled);
            btLoginCode.setBackgroundColor(mContext.getResources().getColor(R.color.color_00bbff));
        } else {
            btLoginCode.setEnabled(isEnabled);
            btLoginCode.setBackgroundColor(mContext.getResources().getColor(R.color.color_bebebe));
        }
    }

    /**
     * 登录界面跳转方法
     *
     * @param activity
     */
    public static void invoke(Context activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            //自动登录选中
            mPreManager.setAutoLoginStatus(true);
        } else {
            //取消自动登录
            mPreManager.setAutoLoginStatus(false);
        }
    }

    @Override
    protected void initStatus(boolean isNet) {

    }

//    private void getDateTime(){
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String time = "2016-12-23 10:22:22";
//        try {
//            Date date = format.parse(time);
//            String str = String.valueOf(date.getTime());
//            String m = str.substring(0,10);
//            L.e("--------时间戳---------"+m);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
}
