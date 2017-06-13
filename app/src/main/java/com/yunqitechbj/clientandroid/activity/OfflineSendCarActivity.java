package com.yunqitechbj.clientandroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.adapter.KeyboardAdapter;
import com.yunqitechbj.clientandroid.adapter.OfflineSendcarAdapter;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;
import com.yunqitechbj.clientandroid.utils.Constants;
import com.yunqitechbj.clientandroid.utils.ImageUtil;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.Order;
import com.yunqitechbj.clientandroid.utils.StringUtils;
import com.yunqitechbj.clientandroid.utils.T;

import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.yunqitechbj.clientandroid.utils.Util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.litepal.crud.DataSupport;

/**
 * 离线派车界面
 * Created by mengwei on 2016/12/21.
 */

public class OfflineSendCarActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    /* 页面空间变量 */
    private EditText etVehicleName;
    private EditText etVehiclePhone;
    private Button btSendCar;
    private ListView lvSendCarView;
    private TextView tvTitle;
    private RelativeLayout rlOn;
    private RelativeLayout rlAll;
    private RelativeLayout rlUpload;

    /* 数据库中所有的数据集合 */
    private ArrayList<OfflineStorage> offlineList = new ArrayList<OfflineStorage>();
    private OfflineSendcarAdapter mOfflineSendcarAdapter;

    private PopupWindow mStatusChooseWindow;

    /* 网络提示标记 */
    private boolean isToast = false;
    private long lastTime;
    //车牌号码输入框
    AlertDialog builder;
    private TextView tvCancel;
    private TextView tvSure;
    private EditText inputbox1, inputbox2,
            inputbox3, inputbox4,
            inputbox5, inputbox6, inputbox7;

    // IWXAPI：第三方APP和微信通信的接口
    public static IWXAPI api;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_offline_send_car;
    }

    @Override
    protected void initView() {
//        // 初始化
//        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
//        // 向微信终端注册你的id
//        api.registerApp(Constants.APP_ID);

        etVehicleName = obtainView(R.id.et_sendcar_name);
        etVehiclePhone = obtainView(R.id.et_sendcar_phone);
        btSendCar = obtainView(R.id.bt_send_car);
        lvSendCarView = obtainView(R.id.lv_send_car_view);
        tvTitle = obtainView(R.id.tv_sendcar_title);
        rlOn = obtainView(R.id.rl_sendcar_on);
        rlAll = obtainView(R.id.rl_sendcar_all);
        rlUpload = obtainView(R.id.rl_sendxar_upload);

        createChooseStatusWindow();
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();

        querylist();

    }

    public void querylist() {
        offlineList.clear();

        DataSupport.deleteAll(OfflineStorage.class, "uploadSuccess = ?", "1");
        DataSupport.deleteAll(OfflineStorage.class,"expired = ?","1");

        offlineList = (ArrayList<OfflineStorage>) DataSupport.findAll(OfflineStorage.class);
        L.e("-------------offlineList----------------" + offlineList.toString());

        Collections.sort(offlineList, new Order());

        mOfflineSendcarAdapter = new OfflineSendcarAdapter(mContext, offlineList);
        lvSendCarView.setAdapter(mOfflineSendcarAdapter);

        mOfflineSendcarAdapter.notifyDataSetChanged();
    }

    @Override
    protected void setListener() {
        btSendCar.setOnClickListener(this);
        rlOn.setOnClickListener(this);
        lvSendCarView.setOnItemClickListener(this);
        rlUpload.setOnClickListener(this);
        etVehicleName.setOnClickListener(this);
    }

    private void dismissPupWindows() {
        if (null != mStatusChooseWindow) {
            mStatusChooseWindow.dismiss();
        }
    }

    private void showPupWindow() {
        int width = rlAll.getMeasuredWidth();
        mStatusChooseWindow.showAsDropDown(rlAll, width, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_sendcar_name:
                inputVehicleDialog();
                break;
            case R.id.rl_sendxar_upload:
                finish();
                break;
//            case R.id.rl_sendcar_on://选择页面
//                if (mStatusChooseWindow.isShowing()) {
//                    dismissPupWindows();
//                } else {
//                    showPupWindow();
//                }
//                break;
//            case R.id.tv_web_trans://交易广场
//                dismissPupWindows();
//                if (StringUtils.isNetworkConnected(mContext)) {
//                    MainActivity.invoke(mContext, 0);
//                    OfflineSendCarActivity.this.finish();
//                } else {
//                    T.showShort(mContext, "当前无网络，请检查网络");
//                }
//                break;
//            case R.id.tv_web_run://长跑路线
//                dismissPupWindows();
//                if (StringUtils.isNetworkConnected(mContext)) {
//                    MainActivity.invoke(mContext, 1);
//                    OfflineSendCarActivity.this.finish();
//                } else {
//                    T.showShort(mContext, "当前无网络，请检查网络");
//                }
//                break;
//            case R.id.tv_web_wallet://企业钱包
//                dismissPupWindows();
//                if (StringUtils.isNetworkConnected(mContext)) {
//                    MainActivity.invoke(mContext, 2);
//                    OfflineSendCarActivity.this.finish();
//                } else {
//                    T.showShort(mContext, "当前无网络，请检查网络");
//                }
//                break;
//            case R.id.tv_web_vehicle://车辆管理
//                dismissPupWindows();
//                if (StringUtils.isNetworkConnected(mContext)) {
//                    MainActivity.invoke(mContext, 3);
//                    OfflineSendCarActivity.this.finish();
//                } else {
//                    T.showShort(mContext, "当前无网络，请检查网络");
//                }
//                break;
//            case R.id.tv_web_help://系统帮助
//                dismissPupWindows();
//                if (StringUtils.isNetworkConnected(mContext)) {
//                    MainActivity.invoke(mContext, 4);
//                    OfflineSendCarActivity.this.finish();
//                } else {
//                    T.showShort(mContext, "当前无网络，请检查网络");
//                }
//                break;
            case R.id.tv_web_offline://离线派车
                dismissPupWindows();
                break;
            case R.id.tv_web_back://退出登录
                dismissPupWindows();
                Intent intent1 = new Intent(OfflineSendCarActivity.this, LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                OfflineSendCarActivity.this.finish();
                break;
            case R.id.bt_send_car://点击派车
//                shareText("http://qa.yqtms.com:80/UploadFile/QRImgs/20170330142956-11918cb2-6367-4494-9d3a-2edc7af01aad.jpg");

                //获取输入框的值
                String mVehicleName = etVehicleName.getText().toString().trim();
                String mVehiclePhone = etVehiclePhone.getText().toString().trim();

                if (StringUtils.isEmpty(mVehicleName)) {
                    T.showShort(mContext, "请输入车牌号码");
                    return;
                }
                if (!StringUtils.isVehicle(mVehicleName)) {
                    T.showShort(mContext, "请输入正确车牌号码");
                    return;
                }
                if (StringUtils.isEmpty(mVehiclePhone)) {
                    T.showShort(mContext, "请输入跟车电话");
                    return;
                }
                if (!StringUtils.isPhone(mVehiclePhone)) {
                    T.showShort(mContext, "请输入正确手机号");
                    return;
                }

                List<OfflineStorage> offlist = DataSupport.where("vehicleName = ?", mVehicleName).find(OfflineStorage.class);

                if (offlist.size() != 0) {
                    chongfuDialog(OfflineSendCarActivity.this, getString(R.string.activity_offline_sendcar_dialog_chongfu1),
                            getString(R.string.activity_offline_sendcar_dialog_chongfu2), mVehicleName, mVehiclePhone);
                } else {
                    OfflineStorage offlineStorage = new OfflineStorage();
                    offlineStorage.setVehicleName(mVehicleName);
                    offlineStorage.setVehiclePhone(mVehiclePhone);
                    offlineStorage.save();

                    if (offlineStorage.isSaved()) {
                        T.showShort(mContext, "派车成功");
                        querylist();
                    } else {
                        T.showShort(mContext, "派车失败，请重新派车");
                    }
                }

                break;
        }
    }

    //    @Override
    //    protected void onNetworkConnected(NetUtils.NetType type) {
    //        T.showShort(mContext,"网络已连接");
    //    }
    //
    //    @Override
    //    protected void onNetworkDisConnected() {
    //
    //    }

    /**
     * 离线派车跳转页
     *
     * @param activity
     */
    public static void invoke(Context activity) {
        Intent intent = new Intent(activity, OfflineSendCarActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 选择页面
     */
    private void createChooseStatusWindow() {
        View popupView = getLayoutInflater().inflate(
                R.layout.employer_current_ticket_popuwindow, null);
        mStatusChooseWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mStatusChooseWindow.setFocusable(true);
        mStatusChooseWindow.setOutsideTouchable(true);
        mStatusChooseWindow.setBackgroundDrawable(new BitmapDrawable());

        popupView.findViewById(R.id.tv_web_trans)
                .setOnClickListener(this);
        popupView.findViewById(R.id.tv_web_run)
                .setOnClickListener(this);
        popupView.findViewById(R.id.tv_web_wallet)
                .setOnClickListener(this);
        popupView.findViewById(R.id.tv_web_help)
                .setOnClickListener(this);
        popupView.findViewById(R.id.tv_web_back)
                .setOnClickListener(this);
        popupView.findViewById(R.id.tv_web_vehicle)
                .setOnClickListener(this);
        popupView.findViewById(R.id.tv_web_offline)
                .setOnClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OfflineStorage offlineStorage = offlineList.get(position);
        long mId = offlineStorage.getId();
        String vehicleName = offlineStorage.getVehicleName();
        String ticketCode = offlineStorage.getTicketCode();
        String mWeightInit = offlineStorage.getMinehairDun();
        String mInitTime = offlineStorage.getMinehairDate();
        String mInitImg = offlineStorage.getMinehireImg();
        String mWeightReach = offlineStorage.getSignedDun();
        String mReachTime = offlineStorage.getSignedDate();
        String mReachImg = offlineStorage.getSignedImg();

        L.e("----------mId----------" + mId);
        L.e("----------vehicleName----------" + vehicleName);
        L.e("----------ticketCode----------" + ticketCode);
        L.e("----------mWeightInit----------" + mWeightInit);
        L.e("----------mInitTime----------" + mInitTime);
        L.e("----------mInitImg----------" + mInitImg);
        L.e("----------mWeightReach----------" + mWeightReach);
        L.e("----------mReachTime----------" + mReachTime);
        L.e("----------mReachImg----------" + mReachImg);

        OfflineStorage mOfflineStorage1 = new OfflineStorage();
        mOfflineStorage1.setId(mId);
        //        mOfflineStorage1.setVehicleName(vehicleName);
        //        mOfflineStorage1.setTicketCode(ticketCode);
        //        mOfflineStorage1.setMinehairDun(mWeightInit);
        //        mOfflineStorage1.setMinehairDate(mInitTime);
        //        mOfflineStorage1.setMinehireImg(mInitImg);
        //        mOfflineStorage1.setSignedDun(mWeightReach);
        //        mOfflineStorage1.setSignedDate(mReachTime);
        //        mOfflineStorage1.setSignedImg(mReachImg);

        //
        Intent intent = new Intent(OfflineSendCarActivity.this, UploadDataActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("OfflineModel", mOfflineStorage1);
        intent.putExtras(bundle);
        startActivity(intent);
        //        UploadDataActivity.invoke(mContext,mId,vehicleName,ticketCode);
    }

    //    @Override
    //    public boolean onKeyDown(int keyCode, KeyEvent event) {
    //        if (keyCode == KeyEvent.KEYCODE_BACK) {
    //            // 再按一次退出程序
    //            finish();
    //        }
    //        return false;
    //    }

    @Override
    protected void initStatus(boolean isNet) {
        if (isNet) {
            if (isToast) {
                T.showShort(mContext, "网络恢复连接");
            }
            isToast = true;
        }
    }

    //    @Override
    //    public boolean onKeyDown(int keyCode, KeyEvent event) {
    //        long currentTime = System.currentTimeMillis();
    //        if (currentTime - lastTime < 2 * 1000){
    //            return super.onKeyDown(keyCode, event);
    //        } else {
    //            T.showShort(mContext,"再按一次，退出程序");
    //            lastTime = currentTime;
    //        }
    //        return true;
    //    }

    //    @Override
    //    public void onBackPressed() {
    //        long currentTime = System.currentTimeMillis();
    //        if (currentTime - lastTime < 2 * 1000){
    //            super.onBackPressed();
    //        } else {
    //            T.showShort(mContext,"再按一次，退出程序");
    //            lastTime = currentTime;
    //        }
    //    }

    /**
     * 重复派车弹窗提示
     *
     * @param context
     * @param str1
     * @param str2
     * @param vehicleNo
     * @param vehiclePhone
     */
    public void chongfuDialog(final Activity context, String str1, String str2, final String vehicleNo,
                              final String vehiclePhone) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View net_view = layoutInflater.inflate(R.layout.dialog_unnetwork, null);

        TextView tvCancel = (TextView) net_view.findViewById(R.id.tv_dialog_net_cancel);
        TextView tvSure = (TextView) net_view.findViewById(R.id.tv_dialog_net_sure);
        TextView tvText1 = (TextView) net_view.findViewById(R.id.tv_dialogutils_text1);
        TextView tvText2 = (TextView) net_view.findViewById(R.id.tv_dialogutils_text2);
        tvText1.setText(str1);
        tvText2.setText(str2);
        if (!StringUtils.isEmpty(str2)) {
            tvText2.setVisibility(View.VISIBLE);
        }
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context);
        final android.support.v7.app.AlertDialog builder = dialog.create();
        builder.setView(net_view);
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        builder.show();

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflineStorage offlineStorage = new OfflineStorage();
                offlineStorage.setVehicleName(vehicleNo);
                offlineStorage.setVehiclePhone(vehiclePhone);
                offlineStorage.save();

                if (offlineStorage.isSaved()) {
                    T.showShort(context, "派车成功");
                    querylist();
                } else {
                    T.showShort(context, "派车失败，请重新派车");
                }
                builder.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

    }

    public void inputVehicleDialog() {
        String[] provides = new String[]{
                "陕", "晋", "鲁", "豫", "新", "蒙", "青", "津", "辽", "吉", "黑"
                , "沪", "苏", "浙", "皖", "闽", "赣", "冀", "鄂", "湘"
                , "粤", "桂", "渝", "川", "贵", "云", "藏", "京", "甘"
                , "琼", "港", "澳", "台", "宁"
        };
        String[] texts = new String[]{
                "0","1", "2", "3", "4", "5", "6", "7", "8", "9"
                , "A", "B", "C", "D", "E", "F", "G", "H", "J"
                , "K", "L", "M", "N", "P", "Q", "R", "S"
                , "T", "U", "V", "W", "X", "Y", "Z"
        };
        // 实例化键盘集合
        final ArrayList<String> providesList = new ArrayList<>();
        final ArrayList<String> textsList = new ArrayList<>();

        // 把键盘的数据添加进集合中
        for (int i = 0, len = provides.length; i < len; i++) {
            providesList.add(provides[i]);
        }
        for (int i = 0, len = texts.length; i < len; i++) {
            textsList.add(texts[i]);
        }

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View net_view = layoutInflater.inflate(R.layout.dialog_vehicle_input, null);

        final EditText etInputBox1 = (EditText) net_view.findViewById(R.id.et_input_box1);
        final EditText etInputBox2 = (EditText) net_view.findViewById(R.id.et_input_box2);
        final EditText etInputBox3 = (EditText) net_view.findViewById(R.id.et_input_box3);
        final EditText etInputBox4 = (EditText) net_view.findViewById(R.id.et_input_box4);
        final EditText etInputBox5 = (EditText) net_view.findViewById(R.id.et_input_box5);
        final EditText etInputBox6 = (EditText) net_view.findViewById(R.id.et_input_box6);
        final EditText etInputBox7 = (EditText) net_view.findViewById(R.id.et_input_box7);
        final GridView gvView = (GridView) net_view.findViewById(R.id.gv_view);
        final GridView gvText = (GridView) net_view.findViewById(R.id.gv_view_text);
        final Button btCancel = (Button) net_view.findViewById(R.id.bt_cancel);
        final Button btSure = (Button) net_view.findViewById(R.id.bt_sure);
        final ImageView ivDelete = (ImageView) net_view.findViewById(R.id.iv_input_delete);

        KeyboardAdapter mAdapter = new KeyboardAdapter(providesList,mContext);
        gvView.setAdapter(mAdapter);

        KeyboardAdapter mAdapterText = new KeyboardAdapter(textsList,mContext);
        gvText.setAdapter(mAdapterText);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        builder = dialog.create();
        builder.setView(net_view);
        builder.setCancelable(true);
        builder.show();

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        btSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入框的值
                String input1 = etInputBox1.getText().toString().trim();
                String input2 = etInputBox2.getText().toString().trim();
                String input3 = etInputBox3.getText().toString().trim();
                String input4 = etInputBox4.getText().toString().trim();
                String input5 = etInputBox5.getText().toString().trim();
                String input6 = etInputBox6.getText().toString().trim();
                String input7 = etInputBox7.getText().toString().trim();
                if (!TextUtils.isEmpty(input1) && !TextUtils.isEmpty(input2)
                        && !TextUtils.isEmpty(input3) && !TextUtils.isEmpty(input4)
                        && !TextUtils.isEmpty(input5) && !TextUtils.isEmpty(input6)
                        && !TextUtils.isEmpty(input7)){
                    etVehicleName.setText(input1+input2+input3+input4+input5
                            +input6+input7);
                    builder.dismiss();
                } else {
                    T.showShort(mContext,"车牌号不完整");
                }
            }
        });

        gvView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dCar = providesList.get(position).toString();
                etInputBox1.setText(dCar);
                gvView.setVisibility(View.GONE);
                gvText.setVisibility(View.VISIBLE);
                ivDelete.setVisibility(View.VISIBLE);
            }
        });
        gvText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dVehicle = textsList.get(position).toString();
                //获取输入框的值
                String input1 = etInputBox1.getText().toString().trim();
                String input2 = etInputBox2.getText().toString().trim();
                String input3 = etInputBox3.getText().toString().trim();
                String input4 = etInputBox4.getText().toString().trim();
                String input5 = etInputBox5.getText().toString().trim();
                String input6 = etInputBox6.getText().toString().trim();
                String input7 = etInputBox7.getText().toString().trim();
                if (!dVehicle.equals("←")){
                    if (!TextUtils.isEmpty(input1) && TextUtils.isEmpty(input2)){
                        Pattern pattern = Pattern.compile("[0-9]*");
                        Matcher isNum = pattern.matcher(dVehicle);
                        if( !isNum.matches() ){
                            etInputBox2.setText(dVehicle);
                        }
                    } else if (!TextUtils.isEmpty(input2) && TextUtils.isEmpty(input3)){
                        etInputBox3.setText(dVehicle);
                    } else if (!TextUtils.isEmpty(input3) && TextUtils.isEmpty(input4)){
                        etInputBox4.setText(dVehicle);
                    } else if (!TextUtils.isEmpty(input4) && TextUtils.isEmpty(input5)){
                        etInputBox5.setText(dVehicle);
                    } else if (!TextUtils.isEmpty(input5) && TextUtils.isEmpty(input6)){
                        etInputBox6.setText(dVehicle);
                    } else if (!TextUtils.isEmpty(input6) && TextUtils.isEmpty(input7)){
                        etInputBox7.setText(dVehicle);
                    }
                }
            }
        });
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入框的值
                String input1 = etInputBox1.getText().toString().trim();
                String input2 = etInputBox2.getText().toString().trim();
                String input3 = etInputBox3.getText().toString().trim();
                String input4 = etInputBox4.getText().toString().trim();
                String input5 = etInputBox5.getText().toString().trim();
                String input6 = etInputBox6.getText().toString().trim();
                String input7 = etInputBox7.getText().toString().trim();
                if (!TextUtils.isEmpty(input7)){
                    etInputBox7.setText("");
                } else if (!TextUtils.isEmpty(input6)) {
                    etInputBox6.setText("");
                } else if (!TextUtils.isEmpty(input5)) {
                    etInputBox5.setText("");
                } else if (!TextUtils.isEmpty(input4)) {
                    etInputBox4.setText("");
                } else if (!TextUtils.isEmpty(input3)) {
                    etInputBox3.setText("");
                } else if (!TextUtils.isEmpty(input2)) {
                    etInputBox2.setText("");
                } else if (!TextUtils.isEmpty(input1)) {
                    etInputBox1.setText("");
                    gvText.setVisibility(View.GONE);
                    gvView.setVisibility(View.VISIBLE);
                    ivDelete.setVisibility(View.GONE);
                }
            }
        });
    }

//    // 文本分享
//    private void shareText(String url) {
//        try {
//            //初始化一个WXWebpageObject对象，填写url
//            WXWebpageObject webpage = new WXWebpageObject();
//            webpage.webpageUrl = url;
//            //用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题、描述
//            WXMediaMessage msg = new WXMediaMessage(webpage);
//            msg.title = "网页标题";
//            msg.description = "网页描述";
//            Bitmap thumb =BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);//压缩Bitmap
//            Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, 150, 150, true);
//            thumb.recycle();
//            msg.thumbData = Util.bmpToByteArray(thumbBmp,true);
//            //构造一个Req
//            SendMessageToWX.Req req = new SendMessageToWX.Req();
//            req.transaction = buildTransaction("webpage");//transaction字段用于唯一标识一个请求
//            req.message = msg;
//            req.scene = SendMessageToWX.Req.WXSceneSession;
//            //调用api接口发送数据到微信
//            api.sendReq(req);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    private String buildTransaction(final String type) {
//        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
//    }

}
