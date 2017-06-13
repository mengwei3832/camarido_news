package com.yunqitechbj.clientandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;
import com.yunqitechbj.clientandroid.utils.DialogUtils;
import com.yunqitechbj.clientandroid.utils.ImageUtil;
import com.yunqitechbj.clientandroid.utils.StringUtils;

import org.litepal.crud.DataSupport;

public class UploadDataActivity extends BaseActivity implements View.OnClickListener {
    /* 页面控件对象 */
    private RelativeLayout rlBack;
    private TextView tvTitle;
    private TextView tvVehicle;
    private TextView tvLine;
    private LinearLayout llTicket;
    private TextView tvTicketCode;
    private ImageView ivKuang;
    private ImageView ivQian;
    private LinearLayout llAlreadyInit;
    private TextView tvWeightInit;
    private TextView tvInitTime;
    private ImageView ivDefaultInit;
    private ImageView ivInitImg;
    private LinearLayout llAlreadyReach;
    private TextView tvWeightReach;
    private TextView tvReachTime;
    private ImageView ivDefaultReach;
    private ImageView ivReachImg;
    private TextView tvInitHour;
    private TextView tvReachHour;
    private Button btCancel;
    private Button btBack;

    /* 传递过来的数据 */
    private String mVehicleName;
    private String mTicketCode;
    private long mId;
    private String mWeightInit;
    private String mInitTime;
    private String mInitImg;
    private String mWeightReach;
    private String mReachTime;
    private String mReachImg;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_upload_data;
    }

    @Override
    protected void initView() {
        //获取传递过来的数据
//        id = getIntent().getLongExtra("id",0);
//        mVehicleName = getIntent().getStringExtra("vehicleName");
//        mTicketCode = getIntent().getStringExtra("ticketCode");
//        mWeightInit = getIntent().getStringExtra("WeightInit");
//        mInitTime = getIntent().getStringExtra("InitTime");
//        mInitImg = getIntent().getStringExtra("InitImg");
//        mWeightReach = getIntent().getStringExtra("WeightReach");
//        mReachTime = getIntent().getStringExtra("ReachTime");
//        mReachImg = getIntent().getStringExtra("ReachImg");
        OfflineStorage offlineStorage = (OfflineStorage) getIntent().getSerializableExtra("OfflineModel");
        mId = offlineStorage.getId();

        rlBack = obtainView(R.id.rl_upload_back);
        tvTitle = obtainView(R.id.tv_upload_title);
        tvVehicle = obtainView(R.id.tv_upload_vehicle);
        tvLine = obtainView(R.id.tv_upload_line);
        llTicket = obtainView(R.id.ll_upload_ticket);
        tvTicketCode = obtainView(R.id.tv_upload_ticketcode);
        ivKuang = obtainView(R.id.iv_upload_data_img_init);
        ivQian = obtainView(R.id.iv_upload_data_img_reach);
        llAlreadyInit = obtainView(R.id.ll_upload_data_already_init);
        tvWeightInit = obtainView(R.id.tv_upload_data_weightinit);
        tvInitTime = obtainView(R.id.tv_upload_data_inittime);
        llAlreadyReach = obtainView(R.id.ll_upload_data_already_reach);
        tvWeightReach = obtainView(R.id.tv_upload_data_weightreach);
        tvReachTime = obtainView(R.id.tv_upload_data_reachtime);
        ivDefaultInit = obtainView(R.id.iv_upload_data_init_default);
        ivInitImg = obtainView(R.id.iv_upload_data_init);
        ivDefaultReach = obtainView(R.id.iv_upload_data_reach_default);
        ivReachImg = obtainView(R.id.iv_upload_data_reach);
        tvInitHour = obtainView(R.id.tv_upload_data_inittimehour);
        tvReachHour = obtainView(R.id.tv_upload_data_reachtimehour);
        btCancel = obtainView(R.id.bt_upload_data_cancel);
        btBack = obtainView(R.id.bt_upload_data_finish);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void setListener() {
        rlBack.setOnClickListener(this);
        ivKuang.setOnClickListener(this);
        ivQian.setOnClickListener(this);
        llAlreadyInit.setOnClickListener(this);
        llAlreadyReach.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        OfflineStorage offlineStorage = DataSupport.find(OfflineStorage.class,mId);
        mVehicleName = offlineStorage.getVehicleName();
        mTicketCode = offlineStorage.getTicketCode();
        mWeightInit = offlineStorage.getMinehairDun();
        mInitTime = offlineStorage.getMinehairDate();
        mInitImg = offlineStorage.getMinehireImg();
        mWeightReach = offlineStorage.getSignedDun();
        mReachTime = offlineStorage.getSignedDate();
        mReachImg = offlineStorage.getSignedImg();
        //判断是否显示车牌号、运单号、矿发、签收图片
        showUploadImg();
    }

    /**
     * 判断是否显示矿发、签收图片
     */
    private void showUploadImg(){
        //车牌号
        if (!StringUtils.isEmpty(mVehicleName)){
            tvVehicle.setText(mVehicleName);
        }

        //运单号
        if (StringUtils.isEmpty(mTicketCode)){
            tvLine.setVisibility(View.GONE);
            llTicket.setVisibility(View.GONE);
        } else {
            tvTicketCode.setText(mTicketCode);
        }
        //判断是否显示矿发默认的图片
        if(!StringUtils.isEmpty(mWeightInit) && !StringUtils.isEmpty(mInitTime)){
            ivKuang.setVisibility(View.GONE);
            llAlreadyInit.setVisibility(View.VISIBLE);
            tvWeightInit.setText("矿发吨数："+mWeightInit+"吨");
            tvInitTime.setText("矿发时间："+mInitTime.substring(0,10));
            tvInitHour.setText("  "+mInitTime.substring(11,16));
            if (!StringUtils.isEmpty(mInitImg)){
                ivDefaultInit.setVisibility(View.GONE);
                ivInitImg.setVisibility(View.VISIBLE);
                Bitmap mInitBitmap = ImageUtil.base64ToBitmap(mInitImg);
                ivInitImg.setImageBitmap(mInitBitmap);
            } else {
                ivDefaultInit.setVisibility(View.VISIBLE);
                ivInitImg.setVisibility(View.GONE);
            }
        } else {
            ivKuang.setVisibility(View.VISIBLE);
            llAlreadyInit.setVisibility(View.GONE);
        }
        //判断是否显示签收默认的图片
        if (!StringUtils.isEmpty(mWeightReach) && !StringUtils.isEmpty(mReachTime)){
            ivQian.setVisibility(View.GONE);
            llAlreadyReach.setVisibility(View.VISIBLE);
            tvWeightReach.setText("签收吨数："+mWeightReach+"吨");
            tvReachTime.setText("签收时间："+mReachTime.substring(0,10));
            tvReachHour.setText("  "+mReachTime.substring(11,16));
            if (!StringUtils.isEmpty(mReachImg)){
                ivDefaultReach.setVisibility(View.GONE);
                ivReachImg.setVisibility(View.VISIBLE);
                Bitmap mReachBitmap = ImageUtil.base64ToBitmap(mReachImg);
                ivReachImg.setImageBitmap(mReachBitmap);
            } else {
                ivDefaultReach.setVisibility(View.VISIBLE);
                ivReachImg.setVisibility(View.GONE);
            }
        } else {
            ivQian.setVisibility(View.VISIBLE);
            llAlreadyReach.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_upload_data_finish:
                finish();
                break;
            case R.id.bt_upload_data_cancel://取消本次执行
                DialogUtils.createDialog(UploadDataActivity.this,getString(R.string.activity_upload_data_cancel_dialog1),
                        getString(R.string.activity_upload_data_cancel_dialog2),mId);
                break;
            case R.id.rl_upload_back://返回上一页
                finish();
                break;
            case R.id.ll_upload_data_already_init:
            case R.id.iv_upload_data_img_init://上传矿发数据
//                UploadKuangActivity.invoke(mContext,id,mVehicleName,0);
                Intent intent = new Intent(UploadDataActivity.this,UploadKuangActivity.class);
                intent.putExtra("id",mId);
                intent.putExtra("distinguish",0);
                startActivity(intent);
                break;
            case R.id.ll_upload_data_already_reach:
            case R.id.iv_upload_data_img_reach://上传签收数据
//                UploadKuangActivity.invoke(mContext,id,mVehicleName,1);
                Intent intent1 = new Intent(UploadDataActivity.this,UploadKuangActivity.class);
                intent1.putExtra("id",mId);
                intent1.putExtra("distinguish",1);
                startActivity(intent1);
                break;
        }
    }

    /**
     * 上传数据页面跳转
     * @param context
     * @param vehicleName
     * @param ticketCode
     */
    public static void invoke(Context context,long id,String vehicleName, String ticketCode){
        Intent intent = new Intent(context,UploadDataActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("vehicleName",vehicleName);
        intent.putExtra("ticketCode",ticketCode);
        context.startActivity(intent);
    }

    @Override
    protected void initStatus(boolean isNet) {

    }


}
