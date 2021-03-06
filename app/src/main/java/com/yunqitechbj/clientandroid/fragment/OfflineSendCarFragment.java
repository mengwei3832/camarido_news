package com.yunqitechbj.clientandroid.fragment;

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
import com.tencent.smtt.sdk.WebView;
import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.activity.LoginActivity;
import com.yunqitechbj.clientandroid.activity.MainActivity;
import com.yunqitechbj.clientandroid.activity.OfflineSendCarActivity;
import com.yunqitechbj.clientandroid.activity.UploadDataActivity;
import com.yunqitechbj.clientandroid.adapter.KeyboardAdapter;
import com.yunqitechbj.clientandroid.adapter.OfflineSendcarAdapter;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;
import com.yunqitechbj.clientandroid.utils.Constants;
import com.yunqitechbj.clientandroid.utils.ImageUtil;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.Order;
import com.yunqitechbj.clientandroid.utils.StringUtils;
import com.yunqitechbj.clientandroid.utils.T;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 离线派车页面
 * Created by mengwei on 2017/5/19.
 */

public class OfflineSendCarFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
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
    protected void initView(View _rootView) {
        etVehicleName = obtainView(R.id.et_sendcar_name);
        etVehiclePhone = obtainView(R.id.et_sendcar_phone);
        btSendCar = obtainView(R.id.bt_send_car);
        lvSendCarView = obtainView(R.id.lv_send_car_view);
        tvTitle = obtainView(R.id.tv_sendcar_title);
        rlOn = obtainView(R.id.rl_sendcar_on);
        rlAll = obtainView(R.id.rl_sendcar_all);
        rlUpload = obtainView(R.id.rl_sendxar_upload);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        querylist();
    }

    @Override
    public void onResume() {
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

        mOfflineSendcarAdapter = new OfflineSendcarAdapter(getActivity(), offlineList);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_sendcar_name:
                inputVehicleDialog();
                break;
            case R.id.rl_sendxar_upload:
                getActivity().finish();
                break;
            case R.id.bt_send_car://点击派车
                //                shareText("http://qa.yqtms.com:80/UploadFile/QRImgs/20170330142956-11918cb2-6367-4494-9d3a-2edc7af01aad.jpg");

                //获取输入框的值
                String mVehicleName = etVehicleName.getText().toString().trim();
                String mVehiclePhone = etVehiclePhone.getText().toString().trim();

                if (StringUtils.isEmpty(mVehicleName)) {
                    T.showShort(getActivity(), "请输入车牌号码");
                    return;
                }
                if (!StringUtils.isVehicle(mVehicleName)) {
                    T.showShort(getActivity(), "请输入正确车牌号码");
                    return;
                }
                if (StringUtils.isEmpty(mVehiclePhone)) {
                    T.showShort(getActivity(), "请输入跟车电话");
                    return;
                }
                if (!StringUtils.isPhone(mVehiclePhone)) {
                    T.showShort(getActivity(), "请输入正确手机号");
                    return;
                }

                List<OfflineStorage> offlist = DataSupport.where("vehicleName = ?", mVehicleName).find(OfflineStorage.class);

                if (offlist.size() != 0) {
                    chongfuDialog(getActivity(), getString(R.string.activity_offline_sendcar_dialog_chongfu1),
                            getString(R.string.activity_offline_sendcar_dialog_chongfu2), mVehicleName, mVehiclePhone);
                } else {
                    OfflineStorage offlineStorage = new OfflineStorage();
                    offlineStorage.setVehicleName(mVehicleName);
                    offlineStorage.setVehiclePhone(mVehiclePhone);
                    offlineStorage.save();

                    if (offlineStorage.isSaved()) {
                        T.showShort(getActivity(), "派车成功");
                        querylist();
                    } else {
                        T.showShort(getActivity(), "派车失败，请重新派车");
                    }
                }

                break;
        }
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
        Intent intent = new Intent(getActivity(), UploadDataActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("OfflineModel", mOfflineStorage1);
        intent.putExtras(bundle);
        startActivity(intent);
    }

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

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
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

        KeyboardAdapter mAdapter = new KeyboardAdapter(providesList,getActivity());
        gvView.setAdapter(mAdapter);

        KeyboardAdapter mAdapterText = new KeyboardAdapter(textsList,getActivity());
        gvText.setAdapter(mAdapterText);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
                    T.showShort(getActivity(),"车牌号不完整");
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
}
