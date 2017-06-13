package com.yunqitechbj.clientandroid.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.adapter.OfflineDisplayAdapter;
import com.yunqitechbj.clientandroid.entity.ExpiredModel;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;
import com.yunqitechbj.clientandroid.http.request.GetExpiredRequest;
import com.yunqitechbj.clientandroid.http.response.Response;
import com.yunqitechbj.clientandroid.utils.DialogUtils;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.Order;
import com.yunqitechbj.clientandroid.utils.StringUtils;
import com.yunqitechbj.clientandroid.utils.T;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 离线数据展示界面
 * Created by mengwei on 2016/12/29.
 */

public class OfflineDisplayActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener {
    /* 页面控件对象 */
    private RelativeLayout rlBack;
    private ListView lvDisplayView;
    private Button btNext;
    private ProgressWheel progressDisplay;
    private RelativeLayout rlAll;

    /* 适配器 */
    private List<OfflineStorage> displayList = new ArrayList<>();
    private List<OfflineStorage> chooseList = new ArrayList<>();
    private OfflineDisplayAdapter mOfflineDisplayAdapter;

    /* 无网弹窗 */
    AlertDialog builder;

    /** 请求类 */
    private GetExpiredRequest mGetExpiredRequest;

    /** 请求ID */
    private final int GET_DATA_EXPIRED = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_offline_display;
    }

    @Override
    protected void initView() {
        rlBack = obtainView(R.id.rl_display_back);
        lvDisplayView = obtainView(R.id.lv_display_view);
        btNext = obtainView(R.id.bt_display_next);
        rlAll = obtainView(R.id.rl_display_all);
        progressDisplay = obtainView(R.id.progress_display);
    }

    @Override
    protected void initData() {
        DataSupport.deleteAll(OfflineStorage.class,"expired = ?","1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isEnabled(true);
        DataSupport.deleteAll(OfflineStorage.class,"uploadSuccess = ?","1");

        displayList = DataSupport.findAll(OfflineStorage.class);
        L.e("-------------offlineList----------------"+displayList.toString());
        //倒序排列
        Collections.sort(displayList,new Order());

        mOfflineDisplayAdapter = new OfflineDisplayAdapter(mContext, displayList);
        lvDisplayView.setAdapter(mOfflineDisplayAdapter);

        if (displayList.size() != 0){
            String mTicketIds = "";
            for (int i = 0; i < displayList.size(); i++){
                if (!StringUtils.isEmpty(displayList.get(i).getTicketId())){
                    mTicketIds += displayList.get(i).getTicketId() + ",";
                }
            }

            L.e("----------mTicketIds-----------"+mTicketIds.toString());

            if (!StringUtils.isEmpty(mTicketIds)){
                showWaitDialog("请稍候...");
                getExpried(mTicketIds.toString().trim());
            }
        } else {
            mOfflineDisplayAdapter = new OfflineDisplayAdapter(mContext, displayList);
            lvDisplayView.setAdapter(mOfflineDisplayAdapter);

            mOfflineDisplayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取展示数据是否已过期
     * @param ticketIds 所有的运单ID
     */
    private void getExpried(String ticketIds){
        mGetExpiredRequest = new GetExpiredRequest(mContext,ticketIds);
        mGetExpiredRequest.setRequestId(GET_DATA_EXPIRED);
        httpGet(mGetExpiredRequest);
    }

    @Override
    protected void setListener() {
        btNext.setOnClickListener(this);
        rlBack.setOnClickListener(this);
        lvDisplayView.setOnItemClickListener(this);
    }

//    @Override
//    protected void onNetworkConnected(NetUtils.NetType type) {
//        builder.dismiss();
//        displayList.clear();
//        displayList = DataSupport.findAll(OfflineStorage.class);
//        L.e("-------------offlineList----------------"+displayList.toString());
//
//        mOfflineDisplayAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    protected void onNetworkDisConnected() {
//        builder.dismiss();
//        showUnNetworkDialog();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_display_back:
                finish();
                break;
            case R.id.bt_display_next://进入报价单列表页面
                chooseList.clear();

                isEnabled(false);

                HashMap<Integer,Boolean> map = OfflineDisplayAdapter.getIsSelected();

                for (int i = 0;i<map.size();i++){
                    if (map.get(i)){
                        OfflineStorage offlineStorage = new OfflineStorage();
                        offlineStorage.setId(displayList.get(i).getId());
                        offlineStorage.setInfoId(displayList.get(i).getInfoId());
//                        offlineStorage.setVehicleName(displayList.get(i).getVehicleName());
//                        offlineStorage.setVehiclePhone(displayList.get(i).getVehiclePhone());
//                        offlineStorage.setTicketId(displayList.get(i).getTicketId());
//                        offlineStorage.setMinehairDun(displayList.get(i).getMinehairDun());
//                        offlineStorage.setMinehairDate(displayList.get(i).getMinehairDate());
//                        offlineStorage.setMinehireImg(displayList.get(i).getMinehireImg());
//                        offlineStorage.setSignedDun(displayList.get(i).getSignedDun());
//                        offlineStorage.setSignedDate(displayList.get(i).getSignedDate());
//                        offlineStorage.setSignedImg(displayList.get(i).getSignedImg());
                        chooseList.add(offlineStorage);
                    }
                }
                OfflineDisplayAdapter.getIsSelected().get("");

                L.e("-------chooseList-----------"+chooseList.toString());
//                T.showShort(mContext,"-------chooseList-----------"+chooseList.toString());

                if (chooseList.size() == 0){
                    T.showShort(mContext,"请至少选择一个运单");
                    isEnabled(true);
                    return;
                }

                Bundle bundle = new Bundle();

                bundle.putSerializable("chooseList", (Serializable) chooseList);

                //跳转到报价单列表页
                Intent intent = new Intent(mContext,QuoteListActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

                break;
        }
    }

    /**
     * 判断按钮是否点击
     * @param isEnabled
     */
    private void isEnabled(boolean isEnabled){
        if (isEnabled){
            btNext.setEnabled(true);
            btNext.setBackgroundResource(R.mipmap.upload_shuju_save);
        } else {
            btNext.setEnabled(false);
            btNext.setBackgroundResource(R.mipmap.upload_shuju_zhihui);
        }
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
            case GET_DATA_EXPIRED:
                isSuccess = response.isSuccess;
                message = response.message;
                if (isSuccess){
                    ArrayList<ExpiredModel> expiredList = response.data;

                    if (expiredList != null){
                        //根据返回数据修改数据库中的判断
                        for (int i = 0; i < expiredList.size(); i++){
                            String mTicketId = expiredList.get(i).TicketId;
                            boolean mIsExitsLoadSign = expiredList.get(i).IsExitsLoadSign;
                            ContentValues values = new ContentValues();
                            if (mIsExitsLoadSign){
                                values.put("expired",1);
                            } else {
                                values.put("expired",0);
                            }
                            DataSupport.updateAll(OfflineStorage.class, values, "ticketId = ?", mTicketId);
                        }

                        displayList = DataSupport.findAll(OfflineStorage.class);

                        for (int i = 0; i < displayList.size(); i++){
                            if (displayList.get(i).getExpired() == 1){
                                hideWaitDialog();
                                DialogUtils.guoqiDialog(OfflineDisplayActivity.this);
                                break;
                            } else {
                                hideWaitDialog();
                            }
                        }

                        //倒序排列
                        Collections.sort(displayList,new Order());

                        L.e("----------------重新刷新列表展示----------------");

                        mOfflineDisplayAdapter = new OfflineDisplayAdapter(mContext, displayList);
                        lvDisplayView.setAdapter(mOfflineDisplayAdapter);

                        mOfflineDisplayAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestId, int httpCode, Throwable error) {
        super.onFailure(requestId, httpCode, error);
        switch (requestId){
            case GET_DATA_EXPIRED:
                T.showShort(mContext, this.getResources().getString(R.string.request_failure));
                break;
        }
    }

    /**
     * 离线数据展示页面跳转
     * @param context
     */
    public static void invoke(Context context){
        Intent intent = new Intent(context,OfflineDisplayActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
        OfflineDisplayAdapter.ViewHolder holder = (OfflineDisplayAdapter.ViewHolder) view.getTag();
        //获取保存的上传时间和更新时间
        long mUploadTime = displayList.get(position).getUploadTime();
        long mUpdateTime = displayList.get(position).getUpdateTime();
        if (mUploadTime <= mUpdateTime){
            // 改变CheckBox的状态
            holder.cbSelect.toggle();
        } else {
            T.showShort(mContext,"该运单没有做过修改，不能重复上传");
        }
        // 将CheckBox的选中状况记录下来
        OfflineDisplayAdapter.getIsSelected().put(position, holder.cbSelect.isChecked());
    }

    @Override
    protected void initStatus(boolean isNet) {

    }

    /**
     * 显示或隐藏进度条
     * @param isShow
     */
    private void isShow(boolean isShow){
        if (isShow){
            rlAll.setVisibility(View.VISIBLE);
            progressDisplay.setVisibility(View.GONE);
        } else {
            rlAll.setVisibility(View.GONE);
            progressDisplay.setVisibility(View.VISIBLE);
        }
    }
}
