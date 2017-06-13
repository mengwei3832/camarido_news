package com.yunqitechbj.clientandroid.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.airsaid.pickerviewlibrary.TimePickerView;
import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;
import com.yunqitechbj.clientandroid.utils.FileUtils;
import com.yunqitechbj.clientandroid.utils.ImageUtil;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.PermissionUtil;
import com.yunqitechbj.clientandroid.utils.PickImage;
import com.yunqitechbj.clientandroid.utils.StringUtils;
import com.yunqitechbj.clientandroid.utils.T;
import com.yunqitechbj.clientandroid.weight.PermissionsResultListener;

import org.litepal.crud.DataSupport;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 上传矿发数据页面
 * Created by mengwei on 2016/12/27.
 */

public class UploadKuangActivity extends BaseActivity implements View.OnClickListener {
    /* 页面控件对象 */
    private RelativeLayout rlBack;
    private TextView tvTitle;
    private TextView tvVehicle;
    private EditText etDun;
    private TextView tvdate;
    private ImageView ivImg;
    private Button btupload;
    private TextView tvKQdun;
    private TextView tvKQdate;
    private Button btimgButton;
    private RelativeLayout rlimg;
    private LinearLayout llFocus;
    private EditText etYear,etMonth,etDay,etHour,etMinute;
    private TextView tvText;

    /* 传递过来的数据 */
    private long id;
    private String vehicleName;
    private int mDistinguish;
    private String mWeightInit;
    private String mInitTime;
    private String mInitImg;
    private String mWeightReach;
    private String mReachTime;
    private String mReachImg;

    /* 时间选择控件 */
//    private TimePickerView mTimePickerView;
    private String mKuangDate;

    /* 存放图片文件夹的同一路径 */
    private String personalVehicleTempFileDir = Environment
            .getExternalStorageDirectory() + "/";

    /* 存放矿发图片文件夹的路径 */
    private String kuangImgName = "Kuangfa.jpg";
    private File kuangImageFile = null;
    private String imgKuangBase64;

    /* 存放矿发图片文件夹的路径 */
    private String qianImgName = "Qianshou.jpg";
    private File qianImageFile = null;
    private String imgQianBase64;

    private static final int P_CODE_PERMISSIONS = 101;

    private InputMethodManager manager;
    private boolean isFocus = false;

    /** 获取输入框的值 */
    private int mYearText,mMonthText,mDayText,mHourText,mMinuteText;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_upload_kuang;
    }

    @Override
    protected void initView() {
        //获取传递过来的数据
        id = getIntent().getLongExtra("id",0);
        mDistinguish = getIntent().getIntExtra("distinguish",0);
        OfflineStorage offlineStorage = DataSupport.find(OfflineStorage.class,id);
        vehicleName = offlineStorage.getVehicleName();
        mWeightInit = offlineStorage.getMinehairDun();
        mInitTime = offlineStorage.getMinehairDate();
        mInitImg = offlineStorage.getMinehireImg();
        mWeightReach = offlineStorage.getSignedDun();
        mReachTime = offlineStorage.getSignedDate();
        mReachImg = offlineStorage.getSignedImg();

        L.e("------id-------"+id);
        L.e("------vehicleName-------"+vehicleName);
        L.e("------mDistinguish-------"+mDistinguish);

        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        rlBack = obtainView(R.id.rl_upload_kuang_back);
        tvTitle = obtainView(R.id.tv_upload_kuang_title);
        tvVehicle = obtainView(R.id.tv_upload_kuang_vehicle);
        etDun = obtainView(R.id.et_upload_kuang_dun);
        tvdate = obtainView(R.id.tv_upload_kuang_date);
        ivImg = obtainView(R.id.iv_upload_kuang_img);
        btupload = obtainView(R.id.bt_upload_kuang_button);
        tvKQdun = obtainView(R.id.tv_upload_kuang_qian_dun);
        tvKQdate = obtainView(R.id.tv_upload_kuang_qian_date);
        btimgButton = obtainView(R.id.bt_upload_img_button);
        rlimg = obtainView(R.id.rl_upload_kuang_img);
        llFocus = obtainView(R.id.ll_upload_focus);
        etYear = obtainView(R.id.et_upload_kuang_year);
        etMonth = obtainView(R.id.et_upload_kuang_month);
        etDay = obtainView(R.id.et_upload_kuang_day);
        etHour = obtainView(R.id.et_upload_kuang_hour);
        etMinute = obtainView(R.id.et_upload_kuang_minute);
        tvText = obtainView(R.id.tv_upload_kuang_text);
    }

    @Override
    protected void initData() {
        /** 获取当前年、月 */
        Calendar c = Calendar.getInstance();
        mYearText = c.get(Calendar.YEAR);
        mMonthText = c.get(Calendar.MONTH) + 1;
        mDayText = c.get(Calendar.DATE);
        mHourText = c.get(Calendar.HOUR_OF_DAY);
        mMinuteText = c.get(Calendar.MINUTE);

        L.e("---"+mYearText+"年"+mMonthText+"月"+mDayText+"日"+mHourText+"时"+mMinuteText+"分");

        if (!StringUtils.isEmpty(vehicleName)){
            tvVehicle.setText(vehicleName);
        }

        if (mDistinguish == 0){
            tvTitle.setText(getString(R.string.activity_upload_kuang_title));
            tvKQdun.setText(getString(R.string.activity_upload_kuang_dun));
            tvKQdate.setText(getString(R.string.activity_upload_kuang_date));
            tvText.setText(getString(R.string.activity_upload_kuang_date_input));

            L.e("--------mInitTime-----------"+mInitTime);

            if (!StringUtils.isEmpty(mWeightInit) && !StringUtils.isEmpty(mInitTime)){
                String mYear = mInitTime.substring(0,4);
                String mMonth = mInitTime.substring(5,7);
                String mDay = mInitTime.substring(8,10);
                String mHour = mInitTime.substring(11,13);
                String mMouth = mInitTime.substring(14,16);
                etDun.setText(mWeightInit);
                tvdate.setText(mInitTime);
                etYear.setText(mYear);
                etMonth.setText(mMonth);
                etDay.setText(mDay);
                etHour.setText(mHour);
                etMinute.setText(mMouth);
            } else {
                etDun.setHint(getString(R.string.activity_upload_kuang_dun_input));
                tvdate.setHint(getString(R.string.activity_upload_kuang_date_input));
                etYear.setText(mYearText+"");
                if (mMonthText < 10){
                    etMonth.setText("0"+mMonthText);
                } else {
                    etMonth.setText(mMonthText+"");
                }
                if (mDayText < 10){
                    etDay.setText("0" + mDayText);
                } else {
                    etDay.setText(mDayText+"");
                }
                if (mHourText < 10){
                    etHour.setText("0" + mHourText);
                } else {
                    etHour.setText(mHourText+"");
                }
                if (mMinuteText < 10){
                    etMinute.setText("0" + mMinuteText);
                } else {
                    etMinute.setText(mMinuteText+"");
                }
            }
            if (!StringUtils.isEmpty(mInitImg)){
                btimgButton.setVisibility(View.GONE);
                Bitmap mInitBitmap = ImageUtil.base64ToBitmap(mInitImg);
                ivImg.setImageBitmap(mInitBitmap);
            } else {
                btimgButton.setVisibility(View.VISIBLE);
            }
        } else if (mDistinguish == 1){
            tvTitle.setText(getString(R.string.activity_upload_qian_title));
            tvKQdun.setText(getString(R.string.activity_upload_qian_dun));
            etDun.setHint(getString(R.string.activity_upload_qian_dun_input));
            tvKQdate.setText(getString(R.string.activity_upload_qian_date));
            tvText.setText(getString(R.string.activity_upload_qian_date_input));
            tvdate.setHint(getString(R.string.activity_upload_qian_date_input));

            L.e("--------mReachTime-----------"+mReachTime);

            if (!StringUtils.isEmpty(mWeightReach) && !StringUtils.isEmpty(mReachTime)){
                String mYear = mReachTime.substring(0,4);
                String mMonth = mReachTime.substring(5,7);
                String mDay = mReachTime.substring(8,10);
                String mHour = mReachTime.substring(11,13);
                String mMouth = mReachTime.substring(14,16);
                etDun.setText(mWeightReach);
                tvdate.setText(mReachTime);
                etYear.setText(mYear);
                etMonth.setText(mMonth);
                etDay.setText(mDay);
                etHour.setText(mHour);
                etMinute.setText(mMouth);
            } else {
                etDun.setHint(getString(R.string.activity_upload_qian_dun_input));
                tvdate.setHint(getString(R.string.activity_upload_qian_date_input));
                etYear.setText(mYearText+"");
                if (mMonthText < 10){
                    etMonth.setText("0"+mMonthText);
                } else {
                    etMonth.setText(mMonthText+"");
                }
                if (mDayText < 10){
                    etDay.setText("0" + mDayText);
                } else {
                    etDay.setText(mDayText+"");
                }
                if (mHourText < 10){
                    etHour.setText("0" + mHourText);
                } else {
                    etHour.setText(mHourText+"");
                }
                if (mMinuteText < 10){
                    etMinute.setText("0" + mMinuteText);
                } else {
                    etMinute.setText(mMinuteText+"");
                }
            }
            if (!StringUtils.isEmpty(mReachImg)){
                btimgButton.setVisibility(View.GONE);
                Bitmap mReachBitmap = ImageUtil.base64ToBitmap(mReachImg);
                ivImg.setImageBitmap(mReachBitmap);
            } else {
                btimgButton.setVisibility(View.VISIBLE);
            }
        }

//        mTimePickerView = new TimePickerView(mContext,TimePickerView.Type.ALL);
    }

    @Override
    protected void setListener() {
        rlBack.setOnClickListener(this);
        tvdate.setOnClickListener(this);
        btimgButton.setOnClickListener(this);
        ivImg.setOnClickListener(this);
        btupload.setOnClickListener(this);
        etYear.addTextChangedListener(tw);
        etMonth.addTextChangedListener(tw);
        etDay.addTextChangedListener(tw);
        etHour.addTextChangedListener(tw);
        etMinute.addTextChangedListener(tw);
        tvdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                tvdate.setFocusable(true);
                tvdate.setFocusableInTouchMode(true);
                tvdate.requestFocus();
                return false;
            }
        });
        etDun.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        etDun.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2)
                {
                    s.delete(posDot + 3, posDot + 4);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_upload_kuang_back:
                UploadKuangActivity.this.finish();
                break;
            case R.id.tv_upload_kuang_date://选择时间
//                mTimePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
//                    @Override
//                    public void onTimeSelect(Date date) {
//                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//                        mKuangDate = format.format(date);
//                        tvdate.setText(mKuangDate);
//                        T.showShort(mContext,mKuangDate);
//                    }
//                });
//                mTimePickerView.show();
                break;
            case R.id.iv_upload_kuang_img:
                showUploadCamera();
                break;
            case R.id.bt_upload_img_button://上传照片
                performRequestPermissions("为了应用可以正常使用，请您点击确认申请权限。", new String[]{
                        Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1, new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        showUploadCamera();
                    }

                    @Override
                    public void onPermissionDenied() {
                    }
                });
                break;
            case R.id.bt_upload_kuang_button://保存数据到数据库
                //获取数据
                String kuangDun = etDun.getText().toString().trim();

//                String kuangDate = tvdate.getText().toString().trim();
                //获取输入框的值
                String mYear = etYear.getText().toString().trim();
                String mMonth = etMonth.getText().toString().trim();
                String mDay = etDay.getText().toString().trim();
                String mHour = etHour.getText().toString().trim();
                String mMouth = etMinute.getText().toString().trim();

                String kuangDate = mYear + "-" +mMonth + "-" + mDay + " " + mHour + ":" +mMouth;


                L.e("----------id------------"+id);
                L.e("----------mDistinguish------------"+mDistinguish);
                L.e("----------吨数----------"+kuangDun);
                L.e("-----------时间-----------"+kuangDate);

                if(!isComplete(kuangDun,mYear,mMonth,mDay,mHour,mMouth,mDistinguish)){
                    return;
                }

                //将吨数保留两位小数
                double kuangDun1 = Double.valueOf(kuangDun);
                String kuangDun2 = String.format("%.2f", kuangDun1);

                //获取当前时间
//                String mCurrentTime = StringUtils.getCurrentDate();
                long mCurrentLong = StringUtils.getCurrentTime();//当前时间

//                L.e("---------保存矿发签收mCurrentTime-----------"+mCurrentTime);
                L.e("----------保存矿发签收mCurrentLong-------------"+mCurrentLong);

                OfflineStorage offlineStorage = new OfflineStorage();
                if (mDistinguish == 0){
                    offlineStorage.setMinehairDun(kuangDun2);
                    offlineStorage.setMinehairDate(kuangDate);
                    if (!StringUtils.isEmpty(imgKuangBase64)){
                        offlineStorage.setMinehireImg(imgKuangBase64);
                    }
                    offlineStorage.setUpdateTime(mCurrentLong);
                    offlineStorage.update(id);
                    kuangImageFile = new File(personalVehicleTempFileDir + "Kuangfa.jpg");
                    if (kuangImageFile.exists() && kuangImageFile.isFile()){
                        kuangImageFile.delete();
                    }
                } else if (mDistinguish == 1){
                    offlineStorage.setSignedDun(kuangDun2);
                    offlineStorage.setSignedDate(kuangDate);
                    if (!StringUtils.isEmpty(imgQianBase64)){
                        offlineStorage.setSignedImg(imgQianBase64);
                    }
                    offlineStorage.setUpdateTime(mCurrentLong);
                    offlineStorage.update(id);
                    qianImageFile = new File(personalVehicleTempFileDir + "Qianshou.jpg");
                    if (qianImageFile.exists() && qianImageFile.isFile()){
                        qianImageFile.delete();
                    }
                }

                T.showShort(mContext,"保存成功");
//                Intent intent = new Intent(UploadKuangActivity.this,UploadDataActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                UploadKuangActivity.this.finish();

                break;
        }
    }

    /**
     * 上传矿发数据跳转页面
     * @param context
     * @param id
     * @param vehicleName
     */
    public static void invoke(Context context,long id,String vehicleName,int distinguish){
        Intent intent = new Intent(context,UploadKuangActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("vehicleName",vehicleName);
        intent.putExtra("distinguish",distinguish);
        context.startActivity(intent);
    }

    /**
     * 判断是否符合条件
     * @param dun
     * @param mYear
     * @param mMonth
     * @param mDay
     * @param mHour
     * @param mMouth
     * @param panduan
     * @return
     */
    private boolean isComplete(String dun,String mYear, String mMonth, String mDay, String mHour, String mMouth,int panduan){
        if (panduan == 0){//判断矿发数据
            if (StringUtils.isEmpty(dun)){
                T.showShort(mContext,"请填写矿发吨数");
                return false;
            }
            double kuangDouble = Double.valueOf(dun);
            if (kuangDouble == 0){
                T.showShort(mContext,"矿发吨数不可为0");
                return false;
            }
            if (StringUtils.isEmpty(mYear) || StringUtils.isEmpty(mMonth) || StringUtils.isEmpty(mDay)
                    || StringUtils.isEmpty(mHour) || StringUtils.isEmpty(mMouth)){
                T.showShort(mContext,"请填写矿发时间");
                return false;
            }
            if (mYear.length() != 4 || mMonth.length() != 2 || mDay.length() != 2
                    || mHour.length() != 2 || mMouth.length() != 2){
                T.showShort(mContext,"请按示例格式填写时间");
                return false;
            }
            int yearInt = Integer.valueOf(mYear);
            int monthInt = Integer.valueOf(mMonth);
            int dayInt = Integer.valueOf(mDay);
            int hourInt = Integer.valueOf(mHour);
            int mouthInt = Integer.valueOf(mMouth);
            if (yearInt == 0){
                T.showShort(mContext,"请输入正确的年份");
                return false;
            }
            if (monthInt == 0){
                T.showShort(mContext,"请输入正确的月份");
                return false;
            }
            if (monthInt > 12){
                T.showShort(mContext,"请输入正确的月份");
                return false;
            }
            if (dayInt == 0){
                T.showShort(mContext,"请输入正确的日期");
                return false;
            }
            if (StringUtils.isLeapYear(yearInt)){
                if (monthInt == 2){
                    if (dayInt > 29){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                } else if (monthInt == 1 || monthInt == 3|| monthInt == 5
                        || monthInt == 7|| monthInt == 8|| monthInt == 10|| monthInt == 12){
                    if (dayInt > 31){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                } else if (monthInt == 4 || monthInt == 6|| monthInt == 9
                        || monthInt == 11){
                    if (dayInt > 30){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                }
            } else {
                if (monthInt == 2){
                    if (dayInt > 28){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                } else if (monthInt == 1 || monthInt == 3|| monthInt == 5
                        || monthInt == 7|| monthInt == 8|| monthInt == 10|| monthInt == 12){
                    if (dayInt > 31){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                } else if (monthInt == 4 || monthInt == 6|| monthInt == 9
                        || monthInt == 11){
                    if (dayInt > 30){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                }
            }
            if (hourInt > 23){
                T.showShort(mContext,"请输入正确的时间");
                return false;
            }
            if (mouthInt > 59){
                T.showShort(mContext,"请输入正确的时间");
                return false;
            }
            String mDate = mYear + "-" +mMonth + "-" + mDay + " " + mHour + ":" +mMouth;
            if (!StringUtils.isEmpty(mReachTime)){
                long mReach = StringUtils.getStringToDate(mReachTime);
                long mInit = StringUtils.getStringToDate(mDate);
                if (mReach < mInit){
                    T.showShort(mContext,"矿发时间不能大于签收时间");
                    return false;
                }
            }
        } else if (panduan == 1){//判断签收数据
            if (StringUtils.isEmpty(dun)){
                T.showShort(mContext,"请填写签收吨数");
                return false;
            }
            double qianDouble = Double.valueOf(dun);
            if (qianDouble == 0){
                T.showShort(mContext,"签收吨数不可为0");
                return false;
            }
            if (StringUtils.isEmpty(mYear) || StringUtils.isEmpty(mMonth) || StringUtils.isEmpty(mDay)
                    || StringUtils.isEmpty(mHour) || StringUtils.isEmpty(mMouth)){
                T.showShort(mContext,"请填写签收时间");
                return false;
            }
            if (mYear.length() != 4 || mMonth.length() != 2 || mDay.length() != 2
                    || mHour.length() != 2 || mMouth.length() != 2){
                T.showShort(mContext,"请按示例格式填写时间");
                return false;
            }
            int yearInt = Integer.valueOf(mYear);
            int monthInt = Integer.valueOf(mMonth);
            int dayInt = Integer.valueOf(mDay);
            int hourInt = Integer.valueOf(mHour);
            int mouthInt = Integer.valueOf(mMouth);
            if (yearInt == 0){
                T.showShort(mContext,"请输入正确的年份");
                return false;
            }
            if (monthInt == 0){
                T.showShort(mContext,"请输入正确的月份");
                return false;
            }
            if (dayInt == 0){
                T.showShort(mContext,"请输入正确的日期");
                return false;
            }
            if (monthInt > 12){
                T.showShort(mContext,"请输入正确的月份");
                return false;
            }
            if (StringUtils.isLeapYear(yearInt)){
                if (monthInt == 2){
                    if (dayInt > 29){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                } else if (monthInt == 1 || monthInt == 3|| monthInt == 5
                        || monthInt == 7|| monthInt == 8|| monthInt == 10|| monthInt == 12){
                    if (dayInt > 31){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                } else if (monthInt == 4 || monthInt == 6|| monthInt == 9
                        || monthInt == 11){
                    if (dayInt > 30){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                }
            } else {
                if (monthInt == 2){
                    if (dayInt > 28){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                } else if (monthInt == 1 || monthInt == 3|| monthInt == 5
                        || monthInt == 7|| monthInt == 8|| monthInt == 10|| monthInt == 12){
                    if (dayInt > 31){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                } else if (monthInt == 4 || monthInt == 6|| monthInt == 9
                        || monthInt == 11){
                    if (dayInt > 30){
                        T.showShort(mContext,"请输入正确的日期");
                        return false;
                    }
                }
            }
            if (hourInt > 23){
                T.showShort(mContext,"请输入正确的时间");
                return false;
            }
            if (mouthInt > 59){
                T.showShort(mContext,"请输入正确的时间");
                return false;
            }

            String mDate = mYear + "-" +mMonth + "-" + mDay + " " + mHour + ":" +mMouth;

            if (!StringUtils.isEmpty(mInitTime)){
                long mInit = StringUtils.getStringToDate(mInitTime);
                long mReach = StringUtils.getStringToDate(mDate);
                if (mReach < mInit){
                    T.showShort(mContext,"矿发时间不能大于签收时间");
                    return false;
                }
            }
        }

        return true;
    }

    private void requestPermissionsAndroidM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> needPermissionList = new ArrayList<>();
            needPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            needPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            needPermissionList.add(Manifest.permission.CAMERA);
            PermissionUtil.requestPermissions(UploadKuangActivity.this, P_CODE_PERMISSIONS, needPermissionList);

        } else {
            return;
        }
    }

    private void showUploadCamera(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        alertDialog.setTitle("请选择操作");
        // gallery, camera.
        String[] options = {"相册", "拍照"};

        alertDialog.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (PermissionUtil.isOverMarshmallow()) {
                        if (!PermissionUtil.isPermissionValid(UploadKuangActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Toast.makeText(mContext,
                                    "请去\"设置\"中开启本应用的图片媒体访问权限",
                                    Toast.LENGTH_SHORT).show();

                            requestPermissionsAndroidM();
                            return;
                        }
                    }
                    try {
                        if (mDistinguish == 0){
                            PickImage.pickImageFromPhoto(UploadKuangActivity.this,1);//矿发数据图库选择
                        } else {
                            PickImage.pickImageFromPhoto(UploadKuangActivity.this,2);//签收数据图库选择
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext,
                                "请去\"设置\"中开启本应用的图片媒体访问权限",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (PermissionUtil.isOverMarshmallow()) {
                        if (!PermissionUtil.isPermissionValid(UploadKuangActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(mContext,
                                    "请去\"设置\"中开启本应用的图片媒体访问权限",
                                    Toast.LENGTH_SHORT).show();

                            requestPermissionsAndroidM();
                            return;
                        }

                        if (!PermissionUtil.isPermissionValid(UploadKuangActivity.this, Manifest.permission.CAMERA)) {
                            Toast.makeText(mContext,
                                    "请去\"设置\"中开启本应用的相机权限",
                                    Toast.LENGTH_SHORT).show();

                            requestPermissionsAndroidM();
                            return;
                        }
                    }

                    try {
                        if (mDistinguish == 0){
                            PickImage.pickImageFromCamera(UploadKuangActivity.this,
                                    personalVehicleTempFileDir + "Kuangfa.jpg",3);//矿发数据拍照
                        } else {
                            PickImage.pickImageFromCamera(UploadKuangActivity.this,
                                    personalVehicleTempFileDir + "Qianshou.jpg",4);//签收数据拍照
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext,
                                "请去\"设置\"中开启本应用的相机和图片媒体访问权限",
                                Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        alertDialog.show();
    }

    /**
     * 接收拍照或相册返回结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1://矿发数据图库返回
                if (resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    String realPathFromURI = FileUtils.getRealPathFromURI(uri,this);
                    if (TextUtils.isEmpty(realPathFromURI)) {
                        kuangImageFile = null;
                    } else {
                        kuangImageFile = new File(personalVehicleTempFileDir
                                + "Kuangfa.jpg");
                        File temFile = new File(realPathFromURI);

                        FileUtils.copyFile(temFile, kuangImageFile);
                    }
                }
                break;
            case 2://签收数据图库返回
                if (resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    String realPathFromURI = FileUtils.getRealPathFromURI(uri,this);
                    if (TextUtils.isEmpty(realPathFromURI)) {
                        qianImageFile = null;
                    } else {
                        qianImageFile = new File(personalVehicleTempFileDir
                                + "Qianshou.jpg");
                        File temFile = new File(realPathFromURI);

                        FileUtils.copyFile(temFile, qianImageFile);
                    }
                }
                break;
            case 3://矿发数据拍照返回
                if (resultCode == RESULT_OK){
                    kuangImageFile = new File(personalVehicleTempFileDir + "Kuangfa.jpg");
                }
                break;
            case 4://签收数据拍照返回
                if (resultCode == RESULT_OK){
                    qianImageFile = new File(personalVehicleTempFileDir + "Qianshou.jpg");
                }
                break;
        }

        if (requestCode == 1 || requestCode == 3){
            // 判断本地文件是否存在
            if (kuangImageFile == null) {
                return;
            }

            // TODO--
            byte[] bytes = handleImage(kuangImageFile, 800, 800);

            if (bytes == null || bytes.length == 0) {
                return;
            }
            int degree = ImageUtil.readPictureDegree(kuangImageFile
                    .getAbsolutePath());

            imgKuangBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            ImageUtil.rotaingImageView(degree, bm);
            // 显示拍照或选图后的手持身份证
            if (bm != null) {
                ivImg.setImageBitmap(bm);
                btimgButton.setVisibility(View.GONE);
            }

            // base64解码
            byte[] decode = Base64.decode(imgKuangBase64, Base64.NO_WRAP);

            // TODO--将图片保存在SD卡
            BufferedOutputStream bos = null;
            FileOutputStream fos = null;
            File file = new File(personalVehicleTempFileDir
                    + "Kuangfa.jpg");
            try {
                if (file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(decode);
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        if (requestCode == 2 || requestCode == 4){
            // 判断本地文件是否存在
            if (qianImageFile == null) {
                return;
            }

            // TODO--
            byte[] bytes = handleImage(qianImageFile, 800, 800);

            if (bytes == null || bytes.length == 0) {
                return;
            }
            int degree = ImageUtil.readPictureDegree(qianImageFile
                    .getAbsolutePath());

            imgQianBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            ImageUtil.rotaingImageView(degree, bm);
            // 显示拍照或选图后的手持身份证
            if (bm != null) {
                ivImg.setImageBitmap(bm);
                btimgButton.setVisibility(View.GONE);
            }

            // base64解码
            byte[] decode = Base64.decode(imgQianBase64, Base64.NO_WRAP);

            // TODO--将图片保存在SD卡
            BufferedOutputStream bos = null;
            FileOutputStream fos = null;
            File file = new File(personalVehicleTempFileDir
                    + "Qianshou.jpg");
            try {
                if (file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(decode);
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 处理图片
     *
     * @param avatarFile
     * @return
     */
    private byte[] handleImage(File avatarFile, int out_Width, int out_Height) {
        if (avatarFile.exists()) { // 本地文件存在
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // 获取这个图片原始的宽和高 在outHeight 及 outWidth
            Bitmap bm = BitmapFactory.decodeFile(avatarFile.getPath(), options);

            // 此时返回bm为空
            // 我们要得到高及宽都不超过W H的缩略图
            int zW = options.outWidth / out_Width;
            int zH = options.outHeight / out_Height;
            int be = zH;
            if (zW > be)
                be = zW;
            if (be == 0)
                be = 1;
            options.inSampleSize = be;
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(avatarFile.getPath(), options);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            if (bm == null) {
                return null;
            }
            bm.copy(Bitmap.Config.ARGB_8888, false);

            // TODO--
            bm.compress(
                    avatarFile.getAbsolutePath().endsWith("jpg") ? Bitmap.CompressFormat.JPEG
                            : Bitmap.CompressFormat.PNG, 50, outputStream);

            return outputStream.toByteArray();

        }
        return null;
    }

    @Override
    protected void initStatus(boolean isNet) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                manager.hideSoftInputFromWindow(etDun.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (etYear.hasFocus() && s.length() == 4){
                mYearText = Integer.valueOf(etYear.getText().toString().trim());
                if (mYearText > 0){
                    etMonth.setFocusable(true);
                    etMonth.setFocusableInTouchMode(true);
                    etMonth.requestFocus();
                } else {
                    T.showShort(mContext,"请输入正确的年份");
                }
            } else if (etMonth.hasFocus() && s.length() == 2){
                mMonthText = Integer.valueOf(etMonth.getText().toString().trim());
                if (mMonthText < 12 && mMonthText > 0){
                    etDay.setFocusable(true);
                    etDay.setFocusableInTouchMode(true);
                    etDay.requestFocus();
                } else {
                    T.showShort(mContext,"请输入正确的月份");
                }
            } else if (etDay.hasFocus() && s.length() == 2){
                mDayText = Integer.valueOf(etDay.getText().toString().trim());
                if (StringUtils.isLeapYear(mYearText)){
                    if (mMonthText == 2){
                        if (mDayText > 29 || mDayText == 0){
                            T.showShort(mContext,"请输入正确的日期");
                        } else {
                            etHour.setFocusable(true);
                            etHour.setFocusableInTouchMode(true);
                            etHour.requestFocus();
                        }
                    } else if (mMonthText == 1 || mMonthText == 3|| mMonthText == 5
                            || mMonthText == 7|| mMonthText == 8|| mMonthText == 10|| mMonthText == 12){
                        if (mDayText > 31 || mDayText == 0){
                            T.showShort(mContext,"请输入正确的日期");
                        } else {
                            etHour.setFocusable(true);
                            etHour.setFocusableInTouchMode(true);
                            etHour.requestFocus();
                        }
                    } else if (mMonthText == 4 || mMonthText == 6|| mMonthText == 9
                            || mMonthText == 11){
                        if (mDayText > 30 || mDayText == 0){
                            T.showShort(mContext,"请输入正确的日期");
                        } else {
                            etHour.setFocusable(true);
                            etHour.setFocusableInTouchMode(true);
                            etHour.requestFocus();
                        }
                    }
                } else {
                    if (mMonthText == 2){
                        if (mDayText > 28 || mDayText == 0){
                            T.showShort(mContext,"请输入正确的日期");
                        } else {
                            etHour.setFocusable(true);
                            etHour.setFocusableInTouchMode(true);
                            etHour.requestFocus();
                        }
                    } else if (mMonthText == 1 || mMonthText == 3|| mMonthText == 5
                            || mMonthText == 7|| mMonthText == 8|| mMonthText == 10|| mMonthText == 12){
                        if (mDayText > 31 || mDayText == 0){
                            T.showShort(mContext,"请输入正确的日期");
                        } else {
                            etHour.setFocusable(true);
                            etHour.setFocusableInTouchMode(true);
                            etHour.requestFocus();
                        }
                    } else if (mMonthText == 4 || mMonthText == 6|| mMonthText == 9
                            || mMonthText == 11){
                        if (mDayText > 30 || mDayText == 0){
                            T.showShort(mContext,"请输入正确的日期");
                        } else {
                            etHour.setFocusable(true);
                            etHour.setFocusableInTouchMode(true);
                            etHour.requestFocus();
                        }
                    }
                }
            } else if (etHour.hasFocus() && s.length() == 2){
                mHourText = Integer.valueOf(etHour.getText().toString().trim());
                if (mHourText < 24){
                    etMinute.setFocusable(true);
                    etMinute.setFocusableInTouchMode(true);
                    etMinute.requestFocus();
                } else {
                    T.showShort(mContext,"请输入正确的时间");
                }
            } else if (etMinute.hasFocus() && s.length() == 2){
                mMinuteText = Integer.valueOf(etMinute.getText().toString().trim());
                if (mMinuteText > 59){
                    T.showShort(mContext,"请输入正确的时间");
                }
            }
        }
    };
}
