package com.yunqitechbj.clientandroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.StringUtils;
import com.yunqitechbj.clientandroid.weight.ChoiceListItemView;

import java.util.HashMap;
import java.util.List;

/**
 * 离线数据展示页面适配器
 * Created by mengwei on 2016/12/29.
 */

public class OfflineDisplayAdapter extends BaseAdapter {
    private Context mContext;
    private List<OfflineStorage> offlineList;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;

    public OfflineDisplayAdapter(Context context, List<OfflineStorage> offlineList) {
        this.mContext = context;
        this.offlineList = offlineList;
        isSelected = new HashMap<Integer, Boolean>();
        initDatas();
    }

    // 初始化isSelected的数据
    private void initDatas() {
        for (int i = 0; i < offlineList.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        return offlineList.size();
    }

    @Override
    public Object getItem(int position) {
        return offlineList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_offline_diplay,null);
            holder.tvVehicleNo = (TextView) convertView.findViewById(R.id.tv_item_display_vehiclename);
            holder.tvTicketCode = (TextView) convertView.findViewById(R.id.tv_item_display_ticketcode);
            holder.tvWeightInit = (TextView) convertView.findViewById(R.id.tv_item_display_kuangdun);
            holder.tvInitTime = (TextView) convertView.findViewById(R.id.tv_item_display_kuangdate);
            holder.tvWeightReach = (TextView) convertView.findViewById(R.id.tv_item_display_qiandun);
            holder.tvReachTime = (TextView) convertView.findViewById(R.id.tv_item_display_qiandate);
            holder.cbSelect = (CheckBox) convertView.findViewById(R.id.cb_item_display_shangchuan);
            holder.rlCheck = (RelativeLayout) convertView.findViewById(R.id.rl_display_check);
            holder.tvLine = (TextView) convertView.findViewById(R.id.tv_display_line);
            holder.ivGuoQi = (ImageView) convertView.findViewById(R.id.iv_item_display_guoqi);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OfflineStorage offlineStorage = offlineList.get(position);
        String mVehicleNo = offlineStorage.getVehicleName();
        String mTicketCode = offlineStorage.getTicketCode();
        String mWeightInit = offlineStorage.getMinehairDun();
        String mInitTime = offlineStorage.getMinehairDate();
        String mWeightReach = offlineStorage.getSignedDun();
        String mReachTime = offlineStorage.getSignedDate();
        long mUploadTime = offlineStorage.getUploadTime();
        long mUpdateTime = offlineStorage.getUpdateTime();
        long mExpired = offlineStorage.getExpired();

        if (StringUtils.isEmpty(mVehicleNo)){
            mVehicleNo = "";
        }
        if (StringUtils.isEmpty(mTicketCode)){
            mTicketCode = "";
        }
        if (StringUtils.isEmpty(mWeightInit)){
            mWeightInit = "0";
        }
        if (StringUtils.isEmpty(mInitTime)){
            mInitTime = "";
        } else {
            mInitTime = "("+ mInitTime.substring(0,16) +")";
        }
        if (StringUtils.isEmpty(mWeightReach)){
            mWeightReach = "0";
        }
        if (StringUtils.isEmpty(mReachTime)){
            mReachTime = "";
        } else {
            mReachTime = "("+ mReachTime.substring(0,16) +")";
        }

        L.i("----------mUploadTime-----------"+mUploadTime);
        L.i("----------mUpdateTime-----------"+mUpdateTime);

        if (mUploadTime > mUpdateTime){
            holder.rlCheck.setBackgroundResource(R.drawable.item_shape_yuanjiao_lv);
            holder.tvVehicleNo.setTextColor(mContext.getResources().getColor(R.color.color_ffffff));
            holder.tvTicketCode.setTextColor(mContext.getResources().getColor(R.color.color_ffffff));
            holder.tvLine.setBackgroundResource(R.color.color_22bb22);
            holder.cbSelect.setVisibility(View.GONE);
            if (mExpired == 1){
                holder.ivGuoQi.setVisibility(View.VISIBLE);
            }
        } else {
            holder.rlCheck.setBackgroundResource(R.drawable.item_shape_yuan_jiao);
            holder.tvVehicleNo.setTextColor(mContext.getResources().getColor(R.color.color_00bbff));
            holder.tvTicketCode.setTextColor(mContext.getResources().getColor(R.color.color_00bbff));
            holder.tvLine.setBackgroundResource(R.color.color_ccf1ff);
            holder.cbSelect.setVisibility(View.VISIBLE);
            holder.ivGuoQi.setVisibility(View.GONE);
        }

        holder.tvVehicleNo.setText(mVehicleNo);
        holder.tvTicketCode.setText(mTicketCode);
        holder.tvWeightInit.setText(mWeightInit);
        holder.tvInitTime.setText(mInitTime);
        holder.tvWeightReach.setText(mWeightReach);
        holder.tvReachTime.setText(mReachTime);
        holder.cbSelect.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }
    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        OfflineDisplayAdapter.isSelected = isSelected;
    }

    public class ViewHolder{
        public TextView tvVehicleNo,tvTicketCode,tvWeightInit,tvInitTime,tvWeightReach,tvReachTime,tvLine;
        public RelativeLayout rlCheck;
        public CheckBox cbSelect;
        public ImageView ivGuoQi;
    }
}
