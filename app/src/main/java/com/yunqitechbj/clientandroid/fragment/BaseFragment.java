package com.yunqitechbj.clientandroid.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tencent.smtt.sdk.WebView;
import android.widget.TextView;

import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.http.AsyncHttp;
import com.yunqitechbj.clientandroid.http.request.IRequest;
import com.yunqitechbj.clientandroid.http.response.Response;
import com.yunqitechbj.clientandroid.utils.BackHandledFragment;

/**
 * Created by mengwei on 2017/5/19.
 */

public abstract class BaseFragment extends Fragment implements AsyncHttp.IHttpListener {
    protected AsyncHttp mHttp = new AsyncHttp();

    protected Handler mHandler = new Handler();

    protected View mRootView;
    protected WebView wv;

    private AlertDialog builder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getLayoutId() != 0) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
        }

        initView(mRootView);
        setListener();
        initData();
        return mRootView;
    }

    /**
     * 此方法描述的是： 获取布局
     *
     * @author: zhangwb
     * @version: 2015-11-20 上午0:10:30
     */
    protected abstract int getLayoutId();

    /**
     * 此方法描述的是： 初始化界面
     *
     * @author: zhangwb
     * @version: 2015-11-20 上午0:10:30
     */
    protected abstract void initView(View _rootView);

    /**
     * 此方法描述的是： 初始化所有数据的方法
     *
     * @author: zhm
     * @version: 2014-3-12 下午3:17:46
     */
    protected abstract void initData();

    /**
     * 此方法描述的是： 初始化界面
     *
     * @author: zhangwb
     * @version: 2015-11-20 下午13:10:30
     */
    protected abstract void setListener();

    /**
     *
     * @Description:获取控件对象
     * @Title:obtainView
     * @param id
     * @return
     * @return:T
     * @throws
     * @Create: 2016-5-10 下午6:25:33
     * @Author : zhm
     */
    protected <T extends View> T obtainView(int id) {
        return (T) mRootView.findViewById(id);
    }

    protected void httpPost(IRequest request) {
        mHttp.post(request, this);
    }

    protected void httpGet(IRequest request) {
        mHttp.get(request, this);
    }

    /**
     * 显示等待弹窗
     * @param message 弹窗显示的文本
     */
    protected void showProgressDialog(String message){
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View net_view = layoutInflater.inflate(R.layout.dialog_wait_tishi, null);

        TextView tv = (TextView) net_view.findViewById(R.id.tv_dialog_wait);

        tv.setText(message);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        builder = dialog.create();
        builder.setView(net_view);
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        builder.show();
    }

    /**
     * 隐藏等待弹窗
     */
    protected void hideProgressDialog(){
        builder.dismiss();
    }

    @Override
    public void onStart(int requestId) {

    }

    @Override
    public void onSuccess(int requestId, Response response) {

    }

    @Override
    public void onFailure(int requestId, int httpStatus, Throwable error) {

    }
}
